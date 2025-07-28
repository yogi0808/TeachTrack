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
import com.example.teachtrack.models.SessionModel;

import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {
    private Context context;
    private List<SessionModel> sessionList;
    private OnSessionClickListener listener;

    public SessionAdapter(Context context,List<SessionModel> sessionList,OnSessionClickListener listener){
        this.context = context;
        this.sessionList = sessionList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.class_tile, parent,false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SessionViewHolder holder, int position) {
            SessionModel sessionModel = sessionList.get(position);
            holder.textViewTopic.setText(sessionModel.getTopic());
            holder.textViewDate.setText(sessionModel.getDate());

            holder.itemView.setOnLongClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.session_item_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (listener != null){
                        if(item.getItemId() == R.id.menu_delete){
                            listener.onSessionDelete(sessionModel);
                            return true;
                        }
                    }
                    return false;
                });

                popupMenu.show();
                return  true;
            });

            holder.itemView.setOnClickListener(v -> listener.onSessionClick(sessionModel));

    }

    @Override
    public int getItemCount() { return sessionList.size();}

    public static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTopic, textViewDate;

        public SessionViewHolder(View itemView){
            super(itemView);
            textViewTopic = itemView.findViewById(R.id.className);
            textViewDate = itemView.findViewById(R.id.studentCount);
        }
    }

    public interface OnSessionClickListener {
        void onSessionDelete(SessionModel sessionModel);
        void onSessionClick(SessionModel sessionModel);
    }
}
