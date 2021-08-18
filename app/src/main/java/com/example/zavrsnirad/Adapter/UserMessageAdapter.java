package com.example.zavrsnirad.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zavrsnirad.Chat;
import com.example.zavrsnirad.R;
import com.example.zavrsnirad.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserMessageAdapter extends RecyclerView.Adapter<UserMessageAdapter.ViewHolder> {
    private Context context;
    private List<User> users;


    public UserMessageAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }



    @NonNull
    @NotNull
    @Override
    public UserMessageAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_message,parent,false);
        return new UserMessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserMessageAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.username.setText(user.getFullName());


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat.class);
            intent.putExtra("id",user.getId());
            context.startActivity(intent);
        });

        if (user.getImageURI().equals("default")) {
            holder.imageView.setImageResource(R.mipmap.ikona3);
        } else {
            Glide.with(context)
                    .load(user.getImageURI())
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView imageView;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.reviewerName);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }
}
