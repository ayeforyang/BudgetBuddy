package com.example.budgetbuddy;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    // Fragments
    private DashBoardFragment dashBoardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;

    private OverviewFragment overviewFragment;

    private FirebaseAuth mAuth;
    private boolean IsLoggedIn;
    private boolean disableBackbutton = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAu
        toolbar.setTitle("Budget Buddy");
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottomNavigationbar);
        frameLayout = findViewById(R.id.main_frame);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.naView);
        navigationView.setNavigationItemSelectedListener(this);



        View headerView = navigationView.getHeaderView(0);

        TextView emailTextView = headerView.findViewById(R.id.textView3);

        // Get current user information from Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            // Set user information in navigation header
            emailTextView.setText(email);
        } else {
            // User is not signed in, show default values or empty state
            emailTextView.setText("");
        }


        dashBoardFragment = new DashBoardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment = new ExpenseFragment();
        overviewFragment = new OverviewFragment();


        setFragment(dashBoardFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.dashboard) {
                    setFragment(dashBoardFragment);
                    bottomNavigationView.setItemBackgroundResource(R.color.dashboard_color);
                    return true;
                } else if (item.getItemId() == R.id.overview) {
                    setFragment(overviewFragment);
                    bottomNavigationView.setItemBackgroundResource(R.color.income_expense);
                    return true;
                }
                else if (item.getItemId() == R.id.income) {
                    setFragment(incomeFragment);
                    bottomNavigationView.setItemBackgroundResource(R.color.income_expense);
                    return true;
                } else if (item.getItemId() == R.id.expense) {
                    setFragment(expenseFragment);
                    bottomNavigationView.setItemBackgroundResource(R.color.income_expense);
                    return true;
                } else if (item.getItemId() == R.id.logout) {
                    mAuth.signOut();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    return true;
                } else {
                    return false;
                }

            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
                super.onBackPressed();
        }
    }

    public void displaySelectedListener(int itemId) {
        Fragment fragment = null;

        if (itemId == R.id.dashboard) {
            fragment = new DashBoardFragment();
        } else if (itemId == R.id.overview) {
                fragment = new OverviewFragment();
        } else if (itemId == R.id.income) {
            fragment = new IncomeFragment();
        } else if (itemId == R.id.expense) {
            fragment = new ExpenseFragment();

        } else if (itemId == R.id.logout) {
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListener(item.getItemId());
        return true;
    }
    public boolean dispatchKeyEvent(KeyEvent event){
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}
