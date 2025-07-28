package com.example.teachtrack;

import android.content.ContentValues;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.teachtrack.database.DbHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddSessionActivity extends AppCompatActivity {

    private ImageView backBtn;
    private EditText topicInput;
    private Button saveBtn;
    private DatePicker datePicker;
    private DbHelper dbHelper;
    private int classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_session);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backButton);
        datePicker = findViewById(R.id.datePicker);
        topicInput = findViewById(R.id.topicInput);
        saveBtn = findViewById(R.id.saveBtn);
        dbHelper = DbHelper.getInstance(this);
        datePicker.setMinDate(System.currentTimeMillis());
        backBtn.setOnClickListener(v -> finish());

        classId = getIntent().getIntExtra("class_id",-1);

        if (classId == -1) {
            Toast.makeText(this, "Invalid class ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        saveBtn.setOnClickListener(v -> {
            String topic = topicInput.getText().toString().trim();
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            String date = sdf.format(calendar.getTime());

            if(!topic.isEmpty() && !date.isEmpty()) {
                ContentValues values = new ContentValues();
                values.put("topic",topic);
                values.put("date",date);
                values.put("class_id",classId);
                dbHelper.getWritableDatabase().insert("Session",null,values);
                Toast.makeText(this,"Session added",Toast.LENGTH_LONG).show();
                finish();
            }else {
                Toast.makeText(this,"Please enter the valid input",Toast.LENGTH_LONG).show();
            }
        });



    }
}