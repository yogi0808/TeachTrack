package com.example.teachtrack.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teachtrack.R;
import com.example.teachtrack.adapters.PresentStudentAdapter;
import com.example.teachtrack.database.DbHelper;
import com.example.teachtrack.models.StudentModel;

import java.util.ArrayList;
import java.util.List;

public class PresentStudentFragment extends Fragment {

    private int classId = -1;
    private List<StudentModel> studentList;
    private int sessionId = -1;
    private TextView emptyTextView;
    private RecyclerView presentStudentList;
    private DbHelper dbHelper;
    private PresentStudentAdapter studentAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_present_student, container, false);

        emptyTextView = view.findViewById(R.id.emptyTextView);
        presentStudentList = view.findViewById(R.id.presentStudentList);
        studentList = new ArrayList<>();
        dbHelper = DbHelper.getInstance(view.getContext());


        Bundle args = getArguments();
        if (args != null) {
            classId = args.getInt("class_id", -1);
            sessionId = args.getInt("session_id",-1);
        }

        if(sessionId < 0 && classId < 0){
            Toast.makeText(view.getContext(),"Invalid session and class Id",Toast.LENGTH_LONG).show();
        }

        studentAdapter = new PresentStudentAdapter(view.getContext(),studentList);
        presentStudentList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        presentStudentList.setAdapter(studentAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStudents(); // Refresh data every time fragment is resumed
    }

    public void loadStudents(){
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT Student.* FROM" +
                        " Attendance JOIN Student ON Attendance.student_id = Student.id " +
                        "WHERE Attendance.session_id = ? AND Student.class_id = ? AND Attendance.is_present = 1",
                new String[]{String.valueOf(sessionId),String.valueOf(classId)}
        );

        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String rollNo = cursor.getString(cursor.getColumnIndexOrThrow("roll_no"));
            int cId = cursor.getInt(cursor.getColumnIndexOrThrow("class_id"));

            studentList.add(new StudentModel(id,name,rollNo,cId));
        }
        cursor.close();

        if(studentList.isEmpty()){
            emptyTextView.setVisibility(View.VISIBLE);
            presentStudentList.setVisibility(View.GONE);
        }else{
            emptyTextView.setVisibility(View.GONE);
            presentStudentList.setVisibility(View.VISIBLE);
        }
    }
}