package com.example.android.whotsapp.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.android.whotsapp.BuildConfig;
import com.example.android.whotsapp.R;
import com.example.android.whotsapp.databinding.ActivityMainBinding;
import com.example.android.whotsapp.menu.CallsFragment;
import com.example.android.whotsapp.menu.CameraFragment;
import com.example.android.whotsapp.menu.ChatsFragment;
import com.example.android.whotsapp.menu.StatusFragment;
import com.example.android.whotsapp.view.activities.contact.ContactsActivity;
import com.example.android.whotsapp.view.activities.settings.SettingsActivity;
import com.example.android.whotsapp.view.activities.status.AddStatusPicActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 9564;
    private static final int WRITE_EXTERNAL_STORAGE = 456483;
    private static final int IMAGE_INTENT_CODE = 76543513;
    private ActivityMainBinding binding;
    private Uri imageUri=null;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setUpWithViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        try {
            binding.tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_photo_camera_24);
            LinearLayout layout = ((LinearLayout) ((LinearLayout) binding.tabLayout.getChildAt(0)).getChildAt(0));
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
            layoutParams.weight = 0.5f;
            layout.setLayoutParams(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.viewPager.setCurrentItem(1);

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

        binding.fabAction.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ContactsActivity.class)));
    }

    private void setUpWithViewPager(ViewPager viewPager) {
        MainActivity.SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment(), "");
        adapter.addFragment(new ChatsFragment(), "CHATS");
        adapter.addFragment(new StatusFragment(), "STATUS");
        adapter.addFragment(new CallsFragment(), "CALLS");
        viewPager.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void changeFabIcon(final int index) {
        binding.fabAction.hide();
        binding.btnAddStatus.setVisibility(View.GONE);
        new Handler().postDelayed(() -> {
            binding.fabAction.show();
            switch (index) {
                case 0:
                    binding.fabAction.hide();
                    return;
                case 1:
                    binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_chat_24));
                    break;
                case 2:
                    binding.btnAddStatus.setVisibility(View.VISIBLE);
                    binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_photo_camera_24));
                    break;
                case 3:
                    binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_ic_call_24));
                    break;
            }
        }, 50);
        performOnClick(index);
    }

    private static class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
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

    private void performOnClick(final int index){
        binding.fabAction.setOnClickListener(v -> {
            if(index==1){
                startActivity(new Intent(MainActivity.this,ContactsActivity.class));
            }else  if(index==2){
                checkCameraPermission();
            }else if(index==3){
                Toast.makeText(this, "CALL", Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnAddStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "WRITE TO STATUS", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";
        try {
            File file = File.createTempFile("IMG_" + timeStamp, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("listPhotoName", imageFileName);
            startActivityForResult(intent, IMAGE_INTENT_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_INTENT_CODE
                && resultCode == RESULT_OK) {
            if(imageUri!=null){
                startActivity(new Intent(MainActivity.this, AddStatusPicActivity.class));
            }
        }
    }

}