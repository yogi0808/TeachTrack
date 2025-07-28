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
import com.example.teachtrack.models.ClassModel;

import java.util.List;



public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder>{

    private Context context;
    private List<ClassModel> classList;
    private OnClassClickListener listener;

    public ClassAdapter(Context context, List<ClassModel> classList,OnClassClickListener listener) {
        this.context = context;
        this.classList = classList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.class_tile, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClassViewHolder holder, int position) {
        ClassModel classModel = classList.get(position);
        holder.textViewClassName.setText(classModel.getName());

        int studentCount = classModel.getStudentCount();
        holder.textViewStudentCount.setText(studentCount + " Students");

        holder.itemView.setOnClickListener(v -> {
            if(listener != null){
                listener.onClassClick(classModel);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.class_item_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (listener != null) {
                    if (item.getItemId() == R.id.menu_edit) {
                        listener.onClassEdit(classModel);
                        return true;
                    } else if (item.getItemId() == R.id.menu_delete) {
                        listener.onClassDelete(classModel);
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
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView textViewClassName, textViewStudentCount;

        public ClassViewHolder(View itemView) {
            super(itemView);
            textViewClassName = itemView.findViewById(R.id.className);
            textViewStudentCount = itemView.findViewById(R.id.studentCount);
        }
    }

    public interface OnClassClickListener {
        void onClassClick(ClassModel classModel);
        void onClassEdit(ClassModel classModel);
        void onClassDelete(ClassModel classModel);
    }
}
