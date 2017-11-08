package com.example.asus.trendhimapp.weeklyLook;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.asus.trendhimapp.R;
import com.example.asus.trendhimapp.mainActivities.BaseActivity;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class WeeklyLookActivity extends BaseActivity {

    ArrayList<WeeklyLook> weeklyLooks;
    WeeklyLookAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_weekly_look, null, false);
        BaseActivity.drawer.addView(contentView, 0);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        setTitle("Weekly Looks");

        // Lookup the recycler view in activity layout
        recyclerView = findViewById(R.id.recyclerViewWeeklyLook);

        //Define animators to change the behavior of the RecyclerView
        recyclerView.setItemAnimator(new SlideInUpAnimator());

        // Initialize categories
        weeklyLooks = new ArrayList<>();

        // Create adapter for the categories
        adapter = new WeeklyLookAdapter(this, weeklyLooks);

        // Attach the adapter to the recycler view
        recyclerView.setAdapter(adapter);

        //Populate the recycler view
        adapter.addData();

        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayout.VERTICAL, false));
    }
}

