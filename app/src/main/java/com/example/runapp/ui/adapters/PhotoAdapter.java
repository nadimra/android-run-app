package com.example.runapp.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.runapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Photo adapter to be displayed on the grid view for a summary of a run
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class PhotoAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Uri> arrayList;
    private LayoutInflater inflater;
    private ImageView ivPhoto;
    private Button close;

    public PhotoAdapter(Context context, ArrayList<Uri> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Returns the view with the images
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.grid_item, null);

        // Loads image into imageview
        final Uri model = (Uri) this.getItem(i);
        ivPhoto = (ImageView) view.findViewById(R.id.gridPhoto);
        close = (Button) view.findViewById(R.id.closeButton);

        Picasso.with(context).load(model).into(ivPhoto);

        // Removes the photo from the list if the delete button is clicked
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayList.remove(i);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}