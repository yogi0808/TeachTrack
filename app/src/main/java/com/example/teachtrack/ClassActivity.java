package com.example.teachtrack;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.teachtrack.adapters.ViewPagerAdapter;
import com.example.teachtrack.database.DbHelper;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClassActivity extends AppCompatActivity {

    private ImageView backBtn,exportCsv;
    private TextView title;
    private int classId = -1;
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
        exportCsv = findViewById(R.id.exportCsv);



        backBtn.setOnClickListener(v -> finish());

        classId = getIntent().getIntExtra("class_id",-1);

         if (classId == -1) {
             Toast.makeText(this, "Invalid class ID", Toast.LENGTH_SHORT).show();
             finish();
        }

        exportCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),v);
                popupMenu.getMenuInflater().inflate(R.menu.export_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    if(item.getItemId() == R.id.exportCsvBtn){

                        exportAttendanceToCsv(classId);

                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            }

        });

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

    public void exportAttendanceToCsv(int classId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<String> sessionDates = new ArrayList<>();
        List<Integer> sessionIds = new ArrayList<>();

        Cursor sessionCursor = db.rawQuery("SELECT id, date FROM Session WHERE class_id = ? ORDER BY date", new String[]{String.valueOf(classId)});

        while (sessionCursor.moveToNext()){
            sessionIds.add(sessionCursor.getInt(0));
            sessionDates.add(sessionCursor.getString(1));
        }

        sessionCursor.close();

        Cursor studentCursor = db.rawQuery("SELECT id, roll_no, name FROM Student WHERE class_id = ? ORDER BY roll_no", new String[]{String.valueOf(classId)});
        List<String[]> dataRows = new ArrayList<>();

        List<String> header = new ArrayList<>();
        header.add("Roll No");
        header.add("Name");
        header.addAll(sessionDates);
        header.add("Total Present");
        header.add("Percentage");
        dataRows.add(header.toArray(new String[0]));

        while (studentCursor.moveToNext()){
            int studentId = studentCursor.getInt(0);
            String roll = studentCursor.getString(1);
            String name = studentCursor.getString(2);

            int totalPresent = 0;

            List<String> row = new ArrayList<>();
            row.add(roll);
            row.add(name);

            for (int sessionId : sessionIds) {
                Cursor attCursor = db.rawQuery("SELECT is_present FROM Attendance WHERE session_id = ? AND student_id = ?", new String[]{String.valueOf(sessionId), String.valueOf(studentId)});
                int present = 0;

                if (attCursor.moveToFirst()) {
                    present = attCursor.getInt(0);
                }
                attCursor.close();
                row.add(present == 1 ? "P" : "A");
                totalPresent += present;
            }

            int totalSessions = sessionIds.size();
            double percentage = totalSessions > 0 ? (totalPresent * 100.0) / totalSessions : 0.0;

            row.add(String.valueOf(totalPresent));
            row.add(String.format(Locale.getDefault(), "%.2f%%", percentage));

            dataRows.add(row.toArray(new String[0]));

        }

        studentCursor.close();

        Cursor classCursor = dbHelper.getReadableDatabase().rawQuery("SELECT name FROM Class WHERE id = ?", new String[]{String.valueOf(classId)});

        String className = "";

        if(classCursor.moveToFirst()){
             className = classCursor.getString(classCursor.getColumnIndexOrThrow("name"));

        }

       String fileName = "Attendance_class_"+className+".csv";
       StringBuilder csvContent = new StringBuilder();
       for(String[] row: dataRows){
           csvContent.append(TextUtils.join(",", row)).append("\n");
       }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
            values.put(MediaStore.Downloads.IS_PENDING, 1);

            ContentResolver resolver = getContentResolver();
            Uri fileUri = resolver.insert(MediaStore.Files.getContentUri("external"), values);

            if (fileUri != null) {
                try (OutputStream out = resolver.openOutputStream(fileUri)) {
                    out.write(csvContent.toString().getBytes());
                    values.clear();
                    values.put(MediaStore.MediaColumns.IS_PENDING, 0);
                    resolver.update(fileUri, values, null, null);
                    Toast.makeText(this, "Saved to Downloads: " + fileName, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show();
            }
        }else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                Toast.makeText(this, "Storage permission needed", Toast.LENGTH_SHORT).show();
                return;
            }

            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File csvFile = new File(downloadsDir, fileName);
            try (FileWriter fw = new FileWriter(csvFile);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(csvContent.toString());
                Toast.makeText(this, "Saved to Downloads: " + csvFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}