package com.example.teachtrack;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.teachtrack.database.DbHelper;

public class UpdateStudentActivity extends AppCompatActivity {

    private int studentId = -1;
    private EditText sNameInput,sRollNoInput;
    private Button updateBtn;
    private ImageView backBtn;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backButton);
        studentId = getIntent().getIntExtra("student_id",-1);
        sNameInput = findViewById(R.id.sNameInput);
        sRollNoInput = findViewById(R.id.sRollNoInput);
        updateBtn = findViewById(R.id.updateBtn);
        dbHelper = DbHelper.getInstance(this);

        backBtn.setOnClickListener(v -> finish());

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM Student WHERE id = ?", new String[]{String.valueOf(studentId)});

        if(cursor != null && cursor.moveToFirst()){
            sNameInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            sRollNoInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("roll_no")));
        }

        updateBtn.setOnClickListener(v -> {
            String sName = sNameInput.getText().toString().trim();
            String sRollNo = sRollNoInput.getText().toString().trim();

            if(!sName.isEmpty() && !sRollNo.isEmpty()){
                ContentValues values = new ContentValues();
                values.put("name",sName);
                values.put("roll_no",sRollNo);

                dbHelper.getWritableDatabase().update("Student",values,"id = ?",new String[]{String.valueOf(studentId)});

                Toast.makeText(this,"Student Updated Successfully",Toast.LENGTH_LONG).show();
                finish();
            }else {
                Toast.makeText(this,"Both input is Required",Toast.LENGTH_LONG).show();
            }
        });

    }
}