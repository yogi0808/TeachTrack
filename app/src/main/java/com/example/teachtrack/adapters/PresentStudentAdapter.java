package com.example.teachtrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teachtrack.R;
import com.example.teachtrack.models.StudentModel;

import java.util.List;

public class PresentStudentAdapter extends RecyclerView.Adapter<PresentStudentAdapter.PresentStudentViewHolder> {

    private Context context;
    private List<StudentModel> studentList;

    public PresentStudentAdapter(Context context,List<StudentModel> studentList){
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public PresentStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.class_tile, parent,false);
        return new PresentStudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PresentStudentViewHolder holder, int position) {
        StudentModel studentModel = studentList.get(position);
        holder.textViewSName.setText(studentModel.getName());
        holder.textViewSRollNo.setText(studentModel.getRollNo());
    }

    @Override
    public int getItemCount() {return studentList.size();}

    public static class PresentStudentViewHolder extends RecyclerView.ViewHolder{
        TextView textViewSName, textViewSRollNo;

        public PresentStudentViewHolder(View itemView){
            super(itemView);
            textViewSName = itemView.findViewById(R.id.className);
            textViewSRollNo = itemView.findViewById(R.id.studentCount);
        }
    }
}
