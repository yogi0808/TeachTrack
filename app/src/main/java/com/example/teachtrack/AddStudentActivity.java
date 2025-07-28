package com.example.teachtrack;

import android.content.ContentValues;
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

public class AddStudentActivity extends AppCompatActivity {

    private int classId = -1;
    private EditText sNameInput,sRollNoInput;
    private Button saveBtn;
    private DbHelper dbHelper;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        classId = getIntent().getIntExtra("class_id",-1);
        sNameInput = findViewById(R.id.sNameInput);
        sRollNoInput = findViewById(R.id.sRollNoInput);
        saveBtn = findViewById(R.id.saveBtn);
        dbHelper = DbHelper.getInstance(this);
        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(v -> finish());

        if (classId == -1) {
            Toast.makeText(this, "Invalid class ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        saveBtn.setOnClickListener(v -> {
            String sName = sNameInput.getText().toString().trim();
            String sRollNo = sRollNoInput.getText().toString().trim();

            if(!sName.isEmpty() && !sRollNo.isEmpty()){
                ContentValues values = new ContentValues();
                values.put("name",sName);
                values.put("class_id",classId);
                values.put("roll_no",sRollNo);

                dbHelper.getWritableDatabase().insert("Student",null,values);
                sNameInput.setText("");
                sRollNoInput.setText("");

                Toast.makeText(this,"Student added Successfully",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this,"Please enter the valid input",Toast.LENGTH_LONG).show();
            }
        });
    }
}