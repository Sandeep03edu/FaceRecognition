package com.sanedu.fcrecognition.Start;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.R;
import com.sanedu.common.Utils.ViewPagerAdapter;

/**
 * Activity prompted just after splash for unregistered users
 */
public class AuthenticationActivity extends AppCompatActivity {

    // Pager for Login and register fragment
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // Initialising views
        _init();

        // Adding fragments
        AddFragments();

        // Setting initial page as Login page
        SetInitialPager(Constants.START_LOGIN);
    }

    /**
     * Public method to change viewPager page
     * @param i - int - position for ViewPager
     */
    public void SetInitialPager(int i) {
        viewPager.setCurrentItem(i);
    }

    /**
     * Adding fragments to viewPager
     */
    private void AddFragments() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(), "Login");
        adapter.addFragment(new RegistrationFragment(), "Registration");
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
    }

    /**
     * Initialising views
     */
    private void _init() {
        viewPager = findViewById(R.id.authentication_view_pager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==Constants.START_LOGIN){
                    // Changing title
                    setTitle("Login");
                }
                else if(position==Constants.START_REGISTRATION){
                    // Changing title
                    setTitle("Registered");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}