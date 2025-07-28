package com.example.teachtrack;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.teachtrack.adapters.ViewPagerAdapter;
import com.example.teachtrack.database.DbHelper;
import com.google.android.material.tabs.TabLayout;

public class ClassActivity extends AppCompatActivity {

    private ImageView backBtn;
    private TextView title;
    private DbHelper dbHelper;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backButton);
        title = findViewById(R.id.classTitle);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        dbHelper = DbHelper.getInstance(this);

        backBtn.setOnClickListener(v -> finish());

        int classId = getIntent().getIntExtra("class_id",-1);

         if (classId == -1) {
             Toast.makeText(this, "Invalid class ID", Toast.LENGTH_SHORT).show();
             finish();
        }

         pagerAdapter = new ViewPagerAdapter(this,classId);

         Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT name FROM Class WHERE id = ?", new String[]{String.valueOf(classId)});

         if(cursor != null && cursor.moveToFirst()){
             String className = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            title.setText(className);
         }

         viewPager.setAdapter(pagerAdapter);

         tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
             @Override
             public void onTabSelected(TabLayout.Tab tab) {
                 viewPager.setCurrentItem(tab.getPosition());
             }

             @Override
             public void onTabUnselected(TabLayout.Tab tab) {

             }

             @Override
             public void onTabReselected(TabLayout.Tab tab) {

             }
         });

         viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
             @Override
             public void onPageSelected(int position) {
                 super.onPageSelected(position);
                 tabLayout.getTabAt(position).select();
             }
         });
    }
}