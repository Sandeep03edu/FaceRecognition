package com.sanedu.fcrecognition.Start;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.R;
import com.sanedu.common.Utils.ViewPagerAdapter;

public class AuthenticationActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        _init();

        AddFragments();

        SetInitialPager(Constants.START_LOGIN);
    }

    public void SetInitialPager(int i) {
        viewPager.setCurrentItem(i);
    }

    private void AddFragments() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(), "Login");
        adapter.addFragment(new RegistrationFragment(), "Registration");
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
    }

    private void _init() {
        viewPager = findViewById(R.id.authentication_view_pager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==Constants.START_LOGIN){
                    setTitle("Login");
                }
                else if(position==Constants.START_REGISTRATION){
                    setTitle("Registered");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}