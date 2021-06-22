package com.faint.cucina.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.faint.cucina.R;
import com.faint.cucina.classes.Announcement;
import com.faint.cucina.classes.Cafe;
import com.faint.cucina.classes.DishGroup;
import com.faint.cucina.classes.User;
import com.faint.cucina.fragments.MapFragment;
import com.faint.cucina.fragments.NewsFragment;
import com.faint.cucina.fragments.OrderFragment;
import com.faint.cucina.login_register.UserDataSP;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ArrayList<Announcement> eventList;
    public static ArrayList<Cafe> cafes;
    public static ArrayList<DishGroup> scGroups, rmGroups;

    public static User user;

    DrawerLayout drawer;

    SharedPreferences prefs;

    public static int themeCode;

    private boolean backPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        themeCode = getIntent().getIntExtra("THEME", 0);

        user = UserDataSP.getInstance(this).getUser();

        // getting ArrayList we passed via intent -- it`s available to Fragments !
        eventList = getIntent().getParcelableArrayListExtra("EVENT_LIST");
        cafes = getIntent().getParcelableArrayListExtra("CAFE_LIST");

        scGroups = getIntent().getParcelableArrayListExtra("ORDER_SC_LIST");
        rmGroups = getIntent().getParcelableArrayListExtra("ORDER_RM_LIST");

        drawer = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // setting controls for Action(Tool)Bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View navHeader = navigationView.getHeaderView(0);

        TextView nameTxt = navHeader.findViewById(R.id.name_txt);
        TextView phoneTxt = navHeader.findViewById(R.id.phone_txt);

        nameTxt.setText(user.getName());
        String phoneForm = "+" + user.getPhone();
        phoneTxt.setText(phoneForm);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                    new NewsFragment() ).commit();
            navigationView.setCheckedItem(R.id.news);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        int newTheme = Integer.parseInt(prefs.getString("change_theme", "NONE"));

        if(newTheme != themeCode) {
            Intent restartIntent = new Intent(this, StartActivity.class);
            startActivity(restartIntent);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START); // if menu is opened, it closes menu
        }
        else {
            if(backPressedOnce) {
                super.onBackPressed();
                finishAffinity();   // and then it closes all activities
            }

            this.backPressedOnce = true;    // double click check
            Toast.makeText(this, R.string.press_again, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressedOnce = false;
                }
            }, 2000);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.order:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace( R.id.fragment_container, new OrderFragment() )
                        .commit();
                break;
            case R.id.map:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace( R.id.fragment_container, new MapFragment() )
                        .commit();
                break;
            case R.id.news:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace( R.id.fragment_container, new NewsFragment() )
                        .commit();
                break;
            case R.id.setts:
                startActivity( new Intent(this, SettingsActivity.class) );
                break;
            case R.id.logout:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("Вы уверены что хотите выйти из учётной записи?")
                        .setCancelable(true)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                UserDataSP.getInstance(getApplicationContext()).logout();
                            }
                        })
                        .setNegativeButton("Нет", null);

                final AlertDialog alert = builder.create();
                alert.show();

                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}