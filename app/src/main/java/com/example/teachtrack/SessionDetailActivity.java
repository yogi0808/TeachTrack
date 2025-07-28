package com.example.teachtrack;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.teachtrack.adapters.AttViewPagerAdapter;
import com.example.teachtrack.database.DbHelper;
import com.google.android.material.tabs.TabLayout;

public class SessionDetailActivity extends AppCompatActivity {

    private TextView topicTextView,dateTextView;
    private RelativeLayout attTakenView, attNotTakenView;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Button takeAttBtn;
    private ImageView backBtn;
    private int sessionId = -1;
    private int classId = -1;
    private DbHelper dbHelper;
    private AttViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_session_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backButton);
        topicTextView = findViewById(R.id.topicTextView);
        dateTextView = findViewById(R.id.dateTextView);
        attTakenView = findViewById(R.id.attTakenView);
        attNotTakenView = findViewById(R.id.attNotTakenView);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        takeAttBtn = findViewById(R.id.takeAttBtn);

        sessionId = getIntent().getIntExtra("session_id",-1);

        if (sessionId == -1) {
            Toast.makeText(this, "Invalid Session ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        dbHelper = DbHelper.getInstance(this);

        backBtn.setOnClickListener(v -> finish());

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM Session WHERE id = ?",new String[]{String.valueOf(sessionId)});

        if(cursor != null && cursor.moveToFirst()){
            classId = cursor.getInt(cursor.getColumnIndexOrThrow("class_id"));
            String sTopic = cursor.getString(cursor.getColumnIndexOrThrow("topic"));
            String sDate = cursor.getString(cursor.getColumnIndexOrThrow("date"));

            topicTextView.setText(sTopic);
            dateTextView.setText(sDate);
        }

        if (classId == -1) {
            Toast.makeText(this, "Invalid Class ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        takeAttBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, TakeAttendanceActivity.class);
            intent.putExtra("session_id",sessionId);
            startActivity(intent);
        });

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

        viewPagerAdapter = new AttViewPagerAdapter(this,sessionId,classId);

        viewPager.setAdapter(viewPagerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAttendanceView();
    }

    private void updateAttendanceView() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT att_done FROM Session WHERE id = ?",
                new String[]{String.valueOf(sessionId)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            boolean isAttDone = cursor.getInt(cursor.getColumnIndexOrThrow("att_done")) == 1;

            if (isAttDone) {
                attTakenView.setVisibility(View.VISIBLE);
                attNotTakenView.setVisibility(View.GONE);
            } else {
                attTakenView.setVisibility(View.GONE);
                attNotTakenView.setVisibility(View.VISIBLE);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}