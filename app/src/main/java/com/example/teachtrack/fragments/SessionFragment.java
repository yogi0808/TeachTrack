package com.example.teachtrack.fragments;

import android.content.Intent;
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

import com.example.teachtrack.MainActivity;
import com.example.teachtrack.R;
import com.example.teachtrack.SessionDetailActivity;
import com.example.teachtrack.adapters.SessionAdapter;
import com.example.teachtrack.AddSessionActivity;
import com.example.teachtrack.database.DbHelper;
import com.example.teachtrack.models.SessionModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SessionFragment extends Fragment {

    private FloatingActionButton addSessionBtn;
    private List<SessionModel> sessionList;
    private SessionAdapter sessionAdapter;
    private int classId = -1;
    private RecyclerView sessionListView;

    private DbHelper dbHelper;
    private TextView emptyTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session, container, false);

        Bundle args = getArguments();
        if (args != null) {
            classId = args.getInt("class_id", -1);
        }

        addSessionBtn = view.findViewById(R.id.addSessionBtn);
        dbHelper = DbHelper.getInstance(view.getContext());
        sessionListView = view.findViewById(R.id.sessionListView);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        sessionList = new ArrayList<>();
        sessionAdapter = new SessionAdapter(view.getContext(), sessionList, new SessionAdapter.OnSessionClickListener() {
            @Override
            public void onSessionDelete(SessionModel sessionModel) {
                dbHelper.getWritableDatabase().delete("Session" , "id = ?",new String[]{String.valueOf(sessionModel.getId())});
                loadSessions();
                Toast.makeText(view.getContext(),"Session is Deleted successfully",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSessionClick(SessionModel sessionModel) {
                Intent intent = new Intent(view.getContext(), SessionDetailActivity.class);
                intent.putExtra("session_id",sessionModel.getId());
                startActivity(intent);
            }
        });

        addSessionBtn.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), AddSessionActivity.class);
            intent.putExtra("class_id",classId);
            startActivity(intent);
        });

        sessionListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        sessionListView.setAdapter(sessionAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSessions(); // Refresh data every time fragment is resumed
    }

    private void loadSessions(){
        sessionList.clear();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM Session WHERE class_id = ?",new String[]{String.valueOf(classId)});
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int cId = cursor.getInt(cursor.getColumnIndexOrThrow("class_id"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String topic = cursor.getString(cursor.getColumnIndexOrThrow("topic"));
            boolean isAtDone = cursor.getInt(cursor.getColumnIndexOrThrow("att_done")) == 1;

            sessionList.add(new SessionModel(id,cId,topic,date,isAtDone));
        }
        cursor.close();

        if(sessionList.isEmpty()){
            emptyTextView.setVisibility(View.VISIBLE);
            sessionListView.setVisibility(View.GONE);
        }else{
            emptyTextView.setVisibility(View.GONE);
            sessionListView.setVisibility(View.VISIBLE);
        }

        sessionAdapter.notifyDataSetChanged();
    }
}