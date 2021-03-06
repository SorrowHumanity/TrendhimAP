package com.example.asus.trendhimapp.shoppingCartPage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.asus.trendhimapp.R;
import com.example.asus.trendhimapp.categoryPage.CategoryProduct;
import com.example.asus.trendhimapp.mainActivities.BaseActivity;
import com.example.asus.trendhimapp.shoppingCartPage.credentialsPage.CredentialsActivity;
import com.example.asus.trendhimapp.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends BaseActivity {

    //static because they are access from the Shopping Cart Adapter
    private static TextView subtotalTextView, shippingTextView, totalTextView;
    public static ShoppingCartAdapter adapter;
    public static RelativeLayout footer, header, emptyBasketLayout;
    public static Button checkOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_shopping_cart, null, false);
        BaseActivity.drawer.addView(contentView, 0);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle(R.string.shopping_cart_title);

        initializeComponents();
    }

    /**
     * Initialize shopping cart components
     */
    private void initializeComponents() {

        // Static TextViews so they can be updated from the adapter
        subtotalTextView = findViewById(R.id.subtotal_value_text_view);
        shippingTextView = findViewById(R.id.shipping_value_text_view);
        totalTextView = findViewById(R.id.grand_total_value_text_view);

        //Use to display a message to the user if the basket is empty
        emptyBasketLayout = findViewById(R.id.emptyBasketLayout);

        //Hide layouts if the basket is empty
        footer = findViewById(R.id.footer);
        header = findViewById(R.id.header);
        checkOutButton = findViewById(R.id.checkout_button);

        // Setup the recycler view
        RecyclerView recyclerView = findViewById(R.id.the_recycler_view);
        List<CategoryProduct> shoppingCartProducts = new ArrayList<>();
        adapter = new ShoppingCartAdapter(this, shoppingCartProducts);
        recyclerView.setAdapter(adapter);
        adapter.populateRecyclerView();

        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
    }

    /**
     * set subtotal
     * @param newSubtotal
     */
    @SuppressLint("SetTextI18n")
    public static void setSubtotal(int newSubtotal) {
        String value = String.format(Constants.PRICE_FORMAT, newSubtotal);
        subtotalTextView.setText(value);
    }

    /**
     * @return the subtotal cost
     **/
    public static int getSubtotalCost() {
        String value = subtotalTextView.getText().toString();
        value = value.replace("€", ""); // Get rid of the euro sign
        return Integer.parseInt(value);
    }

    /**
     * set shipping cost
     * @param shippingCost
     */
    @SuppressLint("SetTextI18n")
    public static void setShippingCost(int shippingCost) {
        String value = String.format(Constants.PRICE_FORMAT, shippingCost);
        shippingTextView.setText(value);
    }

    /**
     * @return the shipping cost
     **/
    public static int getShippingCost() {
        String value = shippingTextView.getText().toString();
        value = value.replace("€", ""); // Get rid of the euro sign
        return Integer.parseInt(value);
    }

    /**
     * set grand total cost
     * @param totalCost
     */
    @SuppressLint("SetTextI18n")
    public static void setGrandTotalCost(int totalCost) {
        String value = String.format(Constants.PRICE_FORMAT, totalCost);
        totalTextView.setText(value);
    }

    /**
     * @return the grand total cost
     **/
    public static int getGrandTotalCost() {
        String value = totalTextView.getText().toString();
        value = value.replace("€", ""); // Get rid of the euro sign
        return Integer.parseInt(value);
    }

    /**
     * Go to check out
     *
     * @param view
     */
    public void toCheckOut(View view) {
        Intent intent = new Intent(this, CredentialsActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(ShoppingCartActivity.this, view, "profile");
        startActivity(intent, options.toBundle());
    }

}
