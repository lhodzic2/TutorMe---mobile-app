package com.example.zavrsnirad.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zavrsnirad.R;
import com.example.zavrsnirad.model.Review;
import com.example.zavrsnirad.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private Context context;
    private List<Review> reviews;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review,parent,false);
        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ReviewAdapter.ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.reviewerName.setText(review.getReviewerName());
        if (!review.getReview().equals("")) holder.reviewText.setText("Komentar:\n" + review.getReview());
        else holder.reviewText.setText("");
        holder.rating.setText(String.format("%.2f",review.getRating()));

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView rating, reviewerName, reviewText;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            rating = itemView.findViewById(R.id.rating);
            reviewerName = itemView.findViewById(R.id.reviewerName);
            reviewText = itemView.findViewById(R.id.review);
        }
    }
}
