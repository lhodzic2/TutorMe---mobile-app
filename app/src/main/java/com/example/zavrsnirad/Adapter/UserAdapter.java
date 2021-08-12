package com.example.zavrsnirad.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zavrsnirad.Chat;
import com.example.zavrsnirad.R;
import com.example.zavrsnirad.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<User> users;
    private List<User> usersOriginal;


    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
        this.usersOriginal = new ArrayList<>(users);
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.username.setText(user.getFullName());
        if (user.getPredmeti() != null) {
            String subjects = user.getPredmeti().get(0);
            for (int i = 1; i < user.getPredmeti().size(); i++) {
                if (i != user.getPredmeti().size() - 1) {
                    subjects = subjects + ", " + user.getPredmeti().get(i);
                }
            }
            holder.subjectList.setText(subjects);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat.class);
            intent.putExtra("id",user.getId());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredUsers = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredUsers.addAll(usersOriginal);
            } else {
                String filter = constraint.toString().toLowerCase().trim();
                for (User u : usersOriginal) {
                    if (u.getFullName().toLowerCase().contains(filter)) {
                        filteredUsers.add(u);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredUsers;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            users.clear();
            users.addAll((List<User>) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView imageView;
        public TextView subjectList;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameItem);
            imageView = itemView.findViewById(R.id.imageView);
            subjectList = itemView.findViewById(R.id.subjectList);
        }
    }
}
