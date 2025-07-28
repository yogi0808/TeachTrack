package com.example.teachtrack.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teachtrack.AddStudentActivity;
import com.example.teachtrack.MainActivity;
import com.example.teachtrack.R;
import com.example.teachtrack.UpdateStudentActivity;
import com.example.teachtrack.adapters.StudentAdapter;
import com.example.teachtrack.database.DbHelper;
import com.example.teachtrack.models.StudentModel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StudentFragment extends Fragment {

    private Button addSingleStudentBtn, importCsvFile;
    private static final int PICK_CSV_REQUEST = 1;
    private int classId = -1;
    private List<StudentModel> studentList;
    private DbHelper dbHelper;
    private StudentAdapter studentAdapter;
    private RecyclerView studentListView;
    private TextView emptyTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student, container, false);

        Bundle args = getArguments();
        if (args != null) {
            classId = args.getInt("class_id", -1);
        }

        addSingleStudentBtn = view.findViewById(R.id.addSingleStudentBtn);
        importCsvFile = view.findViewById(R.id.importCsvFile);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        studentListView = view.findViewById(R.id.studentListView);
        dbHelper = DbHelper.getInstance(view.getContext());
        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(view.getContext(), studentList, new StudentAdapter.OnStudentClickListener() {
            @Override
            public void onStudentDelete(StudentModel studentModel) {
                dbHelper.getWritableDatabase().delete("Student","id = ?", new String[]{String.valueOf(studentModel.getId())});
                loadStudents();
                Toast.makeText(view.getContext(),"Student Deleted successfully",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStudentEdit(StudentModel studentModel) {
                Intent intent = new Intent(view.getContext(), UpdateStudentActivity.class);
                intent.putExtra("student_id",studentModel.getId());
                startActivity(intent);
            }
        });


        addSingleStudentBtn.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), AddStudentActivity.class);
            intent.putExtra("class_id",classId);
            startActivity(intent);
        });

        importCsvFile.setOnClickListener(v -> openCSVFilePicker());

        studentListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        studentListView.setAdapter(studentAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStudents(); // Refresh data every time fragment is resumed
    }

    private void openCSVFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        startActivityForResult(Intent.createChooser(intent, "Select CSV File"), PICK_CSV_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CSV_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                importStudentsFromCSV(uri);
            }
        }
    }

    private void loadStudents(){
        studentList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Student WHERE class_id = ? ORDER BY roll_no COLLATE NOCASE ASC",new String[]{String.valueOf(classId)});
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String rollNo = cursor.getString(cursor.getColumnIndexOrThrow("roll_no"));
            int cId = cursor.getInt(cursor.getColumnIndexOrThrow("class_id"));

            // Get present count
            Cursor presentCursor = db.rawQuery(
                    "SELECT COUNT(*) FROM Attendance A " +
                            "JOIN Session S ON A.session_id = S.id " +
                            "WHERE A.student_id = ? AND S.class_id = ? AND A.is_present = 1",
                    new String[]{String.valueOf(id),String.valueOf(cId)}
            );
            presentCursor.moveToFirst();
            int presentCount = presentCursor.getInt(0);
            presentCursor.close();

            // Get absent count
            Cursor absentCursor = db.rawQuery(
                    "SELECT COUNT(*) FROM Attendance A " +
                            "JOIN Session S ON A.session_id = S.id " +
                            "WHERE A.student_id = ? AND S.class_id = ? AND A.is_present = 0",
                    new String[]{String.valueOf(id),String.valueOf(cId)}
            );
            absentCursor.moveToFirst();
            int absentCount = absentCursor.getInt(0);
            absentCursor.close();

            StudentModel student = new StudentModel(id,name,rollNo,cId);
            student.setPresentCount(presentCount);
            student.setAbsentCount(absentCount);

            studentList.add(student);
        }
        cursor.close();
        if(studentList.isEmpty()){
            emptyTextView.setVisibility(View.VISIBLE);
            studentListView.setVisibility(View.GONE);
        }else{
            emptyTextView.setVisibility(View.GONE);
            studentListView.setVisibility(View.VISIBLE);
        }
        studentAdapter.notifyDataSetChanged();
    }

    public void importStudentsFromCSV(Uri uri){
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            // Skip header
            reader.readLine();

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    String roll = parts[1].trim();

                    ContentValues values = new ContentValues();
                    values.put("name", name);
                    values.put("roll_no", roll);
                    values.put("class_id", classId);

                    db.insert("Student", null, values);
                }
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            Toast.makeText(requireContext(), "Students imported successfully!", Toast.LENGTH_SHORT).show();
            loadStudents(); // Refresh your RecyclerView

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to import CSV: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}