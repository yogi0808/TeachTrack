package com.example.teachtrack;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teachtrack.adapters.AttendanceAdapter;
import com.example.teachtrack.database.DbHelper;
import com.example.teachtrack.models.StudentModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TakeAttendanceActivity extends AppCompatActivity {

    private ImageView backBtn;
    private TextView topicTextView,dateTextView;
    private List<StudentModel> studentList;
    private int sessionId = -1;
    private int classId = -1;
    private DbHelper dbHelper;
    private RecyclerView studentListView;
    private AttendanceAdapter attAdapter;
    private Button allPresentBtn, allAbsentBtn, saveAttBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_take_attendance);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backButton);
        topicTextView = findViewById(R.id.topicTextView);
        dateTextView = findViewById(R.id.dateTextView);
        studentList = new ArrayList<>();
        backBtn.setOnClickListener(v -> finish());
        studentListView = findViewById(R.id.studentListView);

        allPresentBtn = findViewById(R.id.allPresentBtn);
        allAbsentBtn = findViewById(R.id.allAbsentBtn);
        saveAttBtn = findViewById(R.id.saveAttBtn);

        sessionId = getIntent().getIntExtra("session_id",-1);

        if (sessionId == -1) {
            Toast.makeText(this, "Invalid Session ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        dbHelper = DbHelper.getInstance(this);

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

        Cursor studentCursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM Student WHERE class_id = ? ORDER BY roll_no COLLATE NOCASE ASC",new String[]{String.valueOf(classId)});

        if(studentCursor != null) {
            while (studentCursor.moveToNext()) {
                int id = studentCursor.getInt(studentCursor.getColumnIndexOrThrow("id"));
                String name = studentCursor.getString(studentCursor.getColumnIndexOrThrow("name"));
                String rollNo = studentCursor.getString(studentCursor.getColumnIndexOrThrow("roll_no"));
                int cId = studentCursor.getInt(studentCursor.getColumnIndexOrThrow("class_id"));

                studentList.add(new StudentModel(id, name, rollNo, cId));
            }

            studentCursor.close();
        }

        attAdapter = new AttendanceAdapter(this,studentList);
        studentListView.setLayoutManager(new LinearLayoutManager(this));
        studentListView.setAdapter(attAdapter);


        allPresentBtn.setOnClickListener(v -> {
            attAdapter.markAll(true);
        });

        allAbsentBtn.setOnClickListener(v -> {
            attAdapter.markAll(false);
        });



        saveAttBtn.setOnClickListener(v -> {
            HashMap<Integer, Boolean> attMap = attAdapter.getAttMap();

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.beginTransaction();

            try{
                for(Integer studentId: attMap.keySet()){
                    Boolean isPresent = attMap.get(studentId);

                    ContentValues values = new ContentValues();
                    values.put("session_id",sessionId);
                    values.put("student_id",studentId);
                    values.put("is_present",isPresent ? 1 : 0);
                    db.insert("Attendance",null,values);
                }

                ContentValues values = new ContentValues();

                values.put("att_done",1);
                db.update("Session",values,"id = ?",new String[]{String.valueOf(sessionId)});

                db.setTransactionSuccessful();
                Toast.makeText(this, "Attendance saved successfully!", Toast.LENGTH_SHORT).show();
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Error saving attendance", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        });

    }
}