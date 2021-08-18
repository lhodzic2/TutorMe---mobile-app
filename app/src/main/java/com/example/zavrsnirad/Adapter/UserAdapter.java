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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.zavrsnirad.ProfilePreview;
import com.example.zavrsnirad.R;
import com.example.zavrsnirad.model.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.ArrayList;
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

        String subjects = arrayToString(user.getPredmeti());
        holder.subjectList.setText(subjects);

        if (user.getRating() != 0) holder.rating.setText("Ocjena:\n" + Float.toString(user.getRating()));

        holder.itemView.setOnClickListener(v -> {
           /// Intent intent = new Intent(context, Chat.class);
            Intent intent = new Intent(context, ProfilePreview.class);
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

    @GlideModule
    public class MyAppGlideModule extends AppGlideModule {

        @Override
        public void registerComponents(Context context, Glide glide, Registry registry) {
            // Register FirebaseImageLoader to handle StorageReference
            registry.append(StorageReference.class, InputStream.class,
                    new FirebaseImageLoader.Factory());
        }
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
                    if (u.getFullName().toLowerCase().contains(filter) || arrayToString(u.getPredmeti()).toLowerCase().contains(filter)) {
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
        public TextView username,rating;
        public ImageView imageView;
        public TextView subjectList;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.reviewerName);
            imageView = itemView.findViewById(R.id.imageView);
            subjectList = itemView.findViewById(R.id.review);
            rating = itemView.findViewById(R.id.rating);
        }
    }

    private String arrayToString (ArrayList<String> subjects) {
            String arrayString = subjects.get(0);
            for (int i = 1; i < subjects.size(); i++) {
                    arrayString = arrayString + "\n" + subjects.get(i);
            }
        return arrayString;
    }
}
