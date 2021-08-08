package com.example.zavrsnirad;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zavrsnirad.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<User> userList;

    public MyAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @NotNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.serach_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyAdapter.MyViewHolder holder, int position) {
        User user = userList.get(position);
        holder.firstName.setText(user.getFirstName());
        holder.lastName.setText(user.getLastName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView firstName,lastName;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.searchFirstName);
            lastName = itemView.findViewById(R.id.searchLastName);
        }
    }
}
