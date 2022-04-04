package com.example.qrquest;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomList extends ArrayAdapter<Data> {
    private ArrayList<Data> datas;
    private Context context;

    public CustomList(@NonNull Context context, ArrayList<Data> sessions) {
        super(context, 0,sessions);
        this.datas = sessions;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content,parent,false);
        }
        Data data = datas.get(position);
        TextView rank = view.findViewById(R.id.Rank_Content);
        TextView username = view.findViewById(R.id.Username_Content);
        TextView score = view.findViewById(R.id.Score_Content);
        rank.setText(data.getRank());
        username.setText(data.getUsername());
        score.setText(data.getScore());
        return view;
    }
}


