package com.sahabatpnj.hellounj;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;

import com.google.firebase.firestore.auth.User;
import com.sahabatpnj.hellounj.account.AccountFragment;
import com.sahabatpnj.hellounj.account.AccountLoginFragment;
import com.sahabatpnj.hellounj.category.CategoryFragment;
import com.sahabatpnj.hellounj.category.DetailCategoryFragment;
import com.sahabatpnj.hellounj.discussion.DiscussionFragment;
import com.sahabatpnj.hellounj.home.HomeFragment;
import com.sahabatpnj.hellounj.model.UserDetail;

import javax.annotation.meta.When;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private UserDetail mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Display DetailCategoryFragment When Launch
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();

        //init View
        initActionBar();
        initBottomNav();

        mUser = new UserDetail();

    }


    public void initActionBar() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

    }

    public void initBottomNav() {
        Log.d(TAG, "initBottomNav: Start");
        BottomNavigationView mBotNav = findViewById(R.id.bottomNavigation);
        mUser = new UserDetail();
        mBotNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.action_category:
                        selectedFragment = new CategoryFragment();
                        break;
                    case R.id.action_account:
                        if (mUser.isUserLogin()) {
                            selectedFragment = new AccountFragment();
                            Log.d(TAG, "onNavigationItemSelected: " + mUser.isUserLogin());
                        } else {
                            selectedFragment = new AccountLoginFragment();
                        }
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
                Log.d(TAG, "onNavigationItemSelected: ");
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        BottomNavigationView mBottomNavigationView;
        mBottomNavigationView = findViewById(R.id.bottomNavigation);
        if (mBottomNavigationView.getSelectedItemId() == R.id.action_category && fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
        } else if (mBottomNavigationView.getSelectedItemId() == R.id.action_home) {
            super.onBackPressed();
        } else {
            mBottomNavigationView.setSelectedItemId(R.id.action_home);
        }
    }

}
