package com.example.teachtrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teachtrack.R;
import com.example.teachtrack.models.StudentModel;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private Context context;
    private List<StudentModel> StudentList;
    private OnStudentClickListener listener;


    public StudentAdapter(Context context, List<StudentModel> studentList,OnStudentClickListener listener){
        this.context = context;
        this.StudentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_card,parent,false);

        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.StudentViewHolder holder, int position) {
        StudentModel studentModel = StudentList.get(position);
        holder.textViewName.setText(studentModel.getName());
        holder.textViewRollNo.setText(studentModel.getRollNo());
        holder.presentCount.setText("Present: " + studentModel.getPresentCount());
        holder.absentCount.setText("Absent: " + studentModel.getAbsentCount());

        int total = studentModel.getPresentCount() + studentModel.getAbsentCount();
        int percentage = total > 0 ? (studentModel.getPresentCount() * 100 / total) : 0;
        holder.percentage.setText("Total: " + percentage + "%");

        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context,v);
            popupMenu.getMenuInflater().inflate(R.menu.student_item_menu,popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if(listener != null){
                    if(item.getItemId() == R.id.menu_delete){
                        listener.onStudentDelete(studentModel);
                        return true;
                    } else if (item.getItemId() == R.id.menu_edit) {
                        listener.onStudentEdit(studentModel);
                        return true;
                    }
                }

                return false;
            });

            popupMenu.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {return  StudentList.size();}

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewRollNo,presentCount,absentCount,percentage;

        public StudentViewHolder(View itemView){
            super(itemView);
            textViewName = itemView.findViewById(R.id.studentName);
            textViewRollNo = itemView.findViewById(R.id.rollNo);
            presentCount = itemView.findViewById(R.id.presentCount);
            absentCount = itemView.findViewById(R.id.absentCount);
            percentage = itemView.findViewById(R.id.percentage);
        }
    }

    public interface OnStudentClickListener{
        void onStudentDelete(StudentModel studentModel);
        void onStudentEdit(StudentModel studentModel);
    }
}
