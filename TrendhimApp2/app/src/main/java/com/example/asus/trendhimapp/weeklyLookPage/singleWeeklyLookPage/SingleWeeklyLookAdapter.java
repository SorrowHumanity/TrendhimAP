package com.example.asus.trendhimapp.weeklyLookPage.singleWeeklyLookPage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.trendhimapp.R;
import com.example.asus.trendhimapp.categoryPage.CategoryProduct;
import com.example.asus.trendhimapp.mainActivities.recentProducts.RecentProductsAdapter;
import com.example.asus.trendhimapp.productPage.Product;
import com.example.asus.trendhimapp.productPage.ProductActivity;
import com.example.asus.trendhimapp.shoppingCartPage.ShoppingCartProduct;
import com.example.asus.trendhimapp.util.BitmapFlyweight;
import com.example.asus.trendhimapp.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SingleWeeklyLookAdapter extends RecyclerView.Adapter<SingleWeeklyLookAdapter.ViewHolder> {

    private Context context;
    private List<CategoryProduct> productsWishList;

    SingleWeeklyLookAdapter(Context context, List<CategoryProduct> categories) {
        this.productsWishList = categories;
        this.context = context;
    }

    @Override
    public SingleWeeklyLookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View categoryProductsView = inflater.inflate(R.layout.item_look, parent, false);

        // Return a new holder instance
        return new SingleWeeklyLookAdapter.ViewHolder(categoryProductsView);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(SingleWeeklyLookAdapter.ViewHolder viewHolder, final int position) {

        // Get the data model based on position
        final CategoryProduct product = productsWishList.get(position);

        // Set item views based on the views and data model

        TextView productPrice = viewHolder.productPriceTextView;
        productPrice.setText(String.valueOf(product.getPrice() + "€"));

        TextView productName = viewHolder.productNameTextView;
        productName.setText(product.getName());

        Button addToCart = viewHolder.addToCart;
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               executeAddToCart(product.getKey());
            }
        });

        final ImageView productImage = viewHolder.productImage;
        BitmapFlyweight.getPicture(product.getBannerPictureURL(), productImage);

        /*
         * Handle click events - Redirect to the selected product
         */
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProduct(product); //Redirect the user to the product activity
            }
        });


    }

    /**
     * Adds an item to the shopping cart
     **/
    private void executeAddToCart(final String categoryProductKey) {

        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(Constants.TABLE_NAME_SHOPPING_CART);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final boolean[] exists = {false};

        if(user != null){ //if the user is logged in

            myRef.orderByChild(Constants.KEY_PRODUCT_KEY).equalTo(categoryProductKey)
                    .addListenerForSingleValueEvent(new ValueEventListener() { // get user recent products

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                ShoppingCartProduct shoppingCartProduct = dataSnapshot1.getValue(ShoppingCartProduct.class);

                                if(Objects.equals(shoppingCartProduct.getUserEmail(), user.getEmail())) {
                                    int currentQuantity = Integer.parseInt(shoppingCartProduct.getQuantity()) + 1;
                                    //Increase the product quantity if the product is already in the shopping cart
                                    dataSnapshot1.getRef().child(Constants.KEY_QUANTITY).setValue(String.valueOf(currentQuantity));

                                    Toast.makeText(context, R.string.item_added_to_cart_message,
                                            Toast.LENGTH_SHORT).show();
                                    exists[0] = true;
                                    break;
                                }
                            }

                            //Add the product if the product is not in the shopping cart
                            if(!exists[0]){
                                Map<String, Object> values = new HashMap<>();
                                values.put(Constants.KEY_PRODUCT_KEY, categoryProductKey);
                                values.put(Constants.KEY_USER_EMAIL, user.getEmail());
                                values.put(Constants.KEY_QUANTITY, "1");
                                myRef.push().setValue(values);
                                Toast.makeText(context, R.string.item_added_to_cart_message,
                                        Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
        }
    }


    @Override
    public int getItemCount() {
        return productsWishList.size();
    }

    /**
     * Populate the recycler view. Get data from the database which key is equal to one in the parameter.
     */
    public void addData(final String productKey) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(Constants.TABLE_NAME_WEEKLY_LOOK);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() { //get looks
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (final DataSnapshot product : dataSnapshot.getChildren()) {
                        if(Objects.equals(product.getKey(), productKey)){
                            GenericTypeIndicator<List<String>> genericTypeIndicator =
                                    new GenericTypeIndicator<List<String>>() {};
                            List<String> products = product.child(Constants.KEY_PRODUCTS).getValue(genericTypeIndicator);
                            if( products != null ) {
                              for(int i = 0; i < products.size(); i++){
                                  String category = getCategory(products.get(i));
                                  queryGetProducts(category, products.get(i));
                              }

                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });
    }

    /**
     * Query to the product category database to get an specific product
     *
     * @param productCategory
     * @param productKey
     */
    private void queryGetProducts(String productCategory, final String productKey){

        //Query to the product category database
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(productCategory);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot product : dataSnapshot.getChildren()) {

                        Product current = product.getValue(Product.class);
                        //If the key of the product found is equal to the recent product key
                        if (Objects.equals(product.getKey(), productKey)) {

                            productsWishList.add(0, new CategoryProduct(current.getProductName(), current.getPrice(),
                                    current.getBrand(), current.getBannerPictureUrl(), productKey));

                            // Notify the adapter that an item was inserted in position = 0
                            notifyItemInserted(0);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    /**
     * @param productKey
     * @return product category based on the given product key
     */
    private String getCategory(String productKey){
        String entityName;
        if (productKey.startsWith(Constants.WATCH_PREFIX))
            entityName = productKey.replaceAll(Constants.ALL_DIGITS_REGEX, "es");
        else if(productKey.startsWith(Constants.BEARD_CARE_PREFIX))
            entityName = productKey.replaceAll(Constants.ALL_DIGITS_REGEX, "");
        else
            entityName = productKey.replaceAll(Constants.ALL_DIGITS_REGEX, "s");

        return entityName;
    }


    /**
     * Redirect the user to the correct Product
     * @param product
     */
    private void getProduct(final CategoryProduct product) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(getCategory(product.getKey()));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        if(Objects.equals(product.getKey(), dataSnapshot1.getKey())) {

                            Product foundProduct = dataSnapshot1.getValue(Product.class);

                            Intent toProductActivity = new Intent(context, ProductActivity.class);

                            toProductActivity.putExtra(Constants.KEY_PRODUCT_KEY, product.getKey());
                            toProductActivity.putExtra(Constants.KEY_PRODUCT_NAME, foundProduct.getProductName());
                            toProductActivity.putExtra(Constants.KEY_BRAND_NAME, foundProduct.getBrand());
                            toProductActivity.putExtra(Constants.KEY_BANNER_PIC_URL, foundProduct.getBannerPictureUrl());
                            toProductActivity.putExtra(Constants.KEY_PRICE, String.valueOf(foundProduct.getPrice()));
                            toProductActivity.putExtra(Constants.KEY_LEFT_PIC_URL, foundProduct.getLeftPictureUrl());
                            toProductActivity.putExtra(Constants.KEY_RIGHT_PIC_URL, foundProduct.getRightPictureUrl());

                            context.startActivity(toProductActivity);
                        }

                    }
                    //Add product to recent activity
                    RecentProductsAdapter.addToRecent(product);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /**
     * Provide a direct reference to each of the views
     * used to cache the views within the layout for fast access
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView productPriceTextView, productNameTextView;
        Button addToCart;
        ImageView productImage;

        /**
         *  We also create a constructor that does the view lookups to find each subview
         */
        ViewHolder(View itemView) {
            /*
              Stores the itemView in a public final member variable that can be used
              to access the context from any ViewHolder instance.
             */
            super(itemView);

            productPriceTextView = itemView.findViewById(R.id.product_price_look);
            addToCart = itemView.findViewById(R.id.addLookToCart);
            productImage = itemView.findViewById(R.id.product_image_look);
            productNameTextView = itemView.findViewById(R.id.product_name_look);

        }
    }

}
