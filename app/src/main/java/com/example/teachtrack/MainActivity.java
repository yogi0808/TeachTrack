package com.example.teachtrack;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teachtrack.adapters.ClassAdapter;
import com.example.teachtrack.database.DbHelper;
import com.example.teachtrack.models.ClassModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton AddClassBtn;
    private DbHelper dbHelper;
    private List<ClassModel> classList;
    private ClassAdapter classAdapter;
    private RecyclerView classListView;
    private TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AddClassBtn = findViewById(R.id.addClassBtn);
        classListView = findViewById(R.id.classListView);
        emptyTextView = findViewById(R.id.emptyTextView);

        classList = new ArrayList<>();
        classAdapter = new ClassAdapter(this, classList, new ClassAdapter.OnClassClickListener() {
            @Override
            public void onClassClick(ClassModel classModel) {
                Intent intent = new Intent(MainActivity.this, ClassActivity.class);
                intent.putExtra("class_id", classModel.getId());
                startActivity(intent);
            }

            @Override
            public void onClassEdit(ClassModel classModel) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Edit Class");

                final EditText input = new EditText(MainActivity.this);
                input.setHint("Class name (e.g. Class 10A)");
                input.setText(classModel.getName());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Edit", (dialog, which) -> {
                    String className = input.getText().toString().trim();
                    if (!className.isEmpty()) {
                        ContentValues values = new ContentValues();
                        values.put("name", className);
                        dbHelper.getWritableDatabase().update("Class", values,"id = ?",new String[]{String.valueOf(classModel.getId())});
                        loadClasses();
                        Toast.makeText(MainActivity.this, "Class Edited", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", null);
                builder.show();

            }

            @Override
            public void onClassDelete(ClassModel classModel) {
                dbHelper.getWritableDatabase().delete("Class","id = ?",new String[]{String.valueOf(classModel.getId())});
                loadClasses();
                Toast.makeText(MainActivity.this,"Class is Deleted successfully",Toast.LENGTH_LONG).show();
            }
        });

        classListView.setAdapter(classAdapter);
        classListView.setLayoutManager(new LinearLayoutManager(this));


        dbHelper = DbHelper.getInstance(this);

        AddClassBtn.setOnClickListener(v -> showAddClassDialog());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadClasses();
    }

    private void loadClasses() {
        classList.clear();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM Class", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int studentCount = dbHelper.getStudentCountForClass(id);

            classList.add(new ClassModel(id, name, studentCount));
        }
        cursor.close();

        if(classList.isEmpty()){
            emptyTextView.setVisibility(View.VISIBLE);
            classListView.setVisibility(View.GONE);
        }else{
            emptyTextView.setVisibility(View.GONE);
            classListView.setVisibility(View.VISIBLE);
        }

        classAdapter.notifyDataSetChanged();

    }

    private void showAddClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Class");

        final EditText input = new EditText(this);
        input.setHint("Class name (e.g. Class 10A)");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String className = input.getText().toString().trim();
            if (!className.isEmpty()) {
                ContentValues values = new ContentValues();
                values.put("name", className);
                dbHelper.getWritableDatabase().insert("Class", null, values);
                loadClasses();
                Toast.makeText(this, "Class added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}