package com.example.android.whotsapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.whotsapp.R;
import com.example.android.whotsapp.adapter.ContactsAdapter;
import com.example.android.whotsapp.databinding.ActivityMainBinding;
import com.example.android.whotsapp.menu.CallsFragment;
import com.example.android.whotsapp.menu.ChatsFragment;
import com.example.android.whotsapp.menu.StatusFragment;
import com.example.android.whotsapp.view.contact.ContactsActivity;
import com.example.android.whotsapp.view.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_main);

        setUpWithViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        setSupportActionBar(binding.toolbar);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeFabIcon(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setUpWithViewPager(ViewPager viewPager){
        MainActivity.SectionsPagerAdapter adapter=new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatsFragment(),"CHATS");
        adapter.addFragment(new StatusFragment(),"STATUS");
        adapter.addFragment(new CallsFragment(),"CALLS");
        viewPager.setAdapter(adapter);
    }

    private static class SectionsPagerAdapter extends FragmentPagerAdapter{

        private final List<Fragment> mFragmentList=new ArrayList<>();
        private final List<String> mFragmentTitleList=new ArrayList<>();

        public SectionsPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment,String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.menu_search:
                Toast.makeText(this, "Action Search", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_new_group:
                Toast.makeText(this, "Action new group", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_new_broadcast:
                Toast.makeText(this, "Action new broadcast", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_wa_web:
                Toast.makeText(this, "Action linked devices", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_star_msgs:
                Toast.makeText(this, "Action starred devices", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_payments:
                Toast.makeText(this, "Action payments", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeFabIcon(final int index){
        binding.fabAction.hide();
        new Handler().postDelayed(() -> {
            switch (index){
                case 0:
                    binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_chat_24));
                    binding.fabAction.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ContactsActivity.class)));
                    break;
                case 1: binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_photo_camera_24));break;
                case 2: binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_ic_call_24));break;
            }
            binding.fabAction.show();
        },50);
    }
}