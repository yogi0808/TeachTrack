package com.example.teachtrack.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teachtrack.R;
import com.example.teachtrack.models.StudentModel;
import com.google.android.material.card.MaterialCardView;

import java.util.HashMap;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private Context context;
    private List<StudentModel> StudentList;
    private HashMap<Integer,Boolean> attMap;


    public AttendanceAdapter(Context context, List<StudentModel> studentList){
        this.context = context;
        this.StudentList = studentList;
        this.attMap = new HashMap<>();

        for (StudentModel s : studentList) {
            attMap.put(s.getId(),false);
        }
    }

    public HashMap<Integer,Boolean> getAttMap(){
        return attMap;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_att_card,parent,false);

        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.AttendanceViewHolder holder, int position) {
        StudentModel studentModel = StudentList.get(position);
        holder.textViewName.setText(studentModel.getName());
        holder.textViewRollNo.setText(studentModel.getRollNo());

        holder.checkBox.setOnCheckedChangeListener(null);

        Boolean isPresent = attMap.get(studentModel.getId());
        holder.checkBox.setChecked(isPresent != null && isPresent);

        holder.cardMain.setBackgroundColor((isPresent != null && isPresent)
                ? Color.parseColor("#1466BB6A")
                : Color.parseColor("#14ffffff"));

        holder.checkBox.setOnCheckedChangeListener((buttonView,isChecked) -> {
            attMap.put(studentModel.getId(), isChecked);

            holder.cardMain.setBackgroundColor(isChecked
                    ? Color.parseColor("#1466BB6A")
                    : Color.parseColor("#14ffffff"));
        });
    }

    @Override
    public int getItemCount() {return  StudentList.size();}

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewRollNo;
        CheckBox checkBox;
        MaterialCardView cardMain;

        public AttendanceViewHolder(View itemView){
            super(itemView);
            textViewName = itemView.findViewById(R.id.studentName);
            textViewRollNo = itemView.findViewById(R.id.rollNo);
            cardMain = itemView.findViewById(R.id.cardMain);
            checkBox= itemView.findViewById(R.id.isPresent);
        }
    }

    public void markAll(boolean isPresent){
        for (StudentModel s : StudentList){
            attMap.put(s.getId(),isPresent);
        }
        notifyDataSetChanged();
    }
}
