package com.example.runapp.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.runapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Run-Photo adapter to display photos for a specific run in a list
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class RunPhotosAdapter extends RecyclerView.Adapter<RunPhotosAdapter.RunPhotosViewHolder> {

    private List<Bitmap> data;
    private Context context;
    private LayoutInflater layoutInflater;

    public RunPhotosAdapter(Context context) {
        this.data = new ArrayList<>();
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RunPhotosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.photo_item, parent, false);
        return new RunPhotosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RunPhotosViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Add new data to the dataset
     * @param newData
     */
    public void setData(List<Bitmap> newData) {
        if (data != null) {
            data.clear();
            data.addAll(newData);
            notifyDataSetChanged();
        } else {
            data = newData;
        }
    }

    /**
     * Represents the view holder for the view
     */
    class RunPhotosViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        RunPhotosViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.photoImage);
        }

        void bind(final Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

    }
}
