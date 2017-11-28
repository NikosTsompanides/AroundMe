package com.nikostsompanidis.aroundme;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Created by Nikos Tsompanidis on 6/11/2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private final GalleryItemClickListener galleryItemClickListener;
    private ArrayList<String> galleryList;

    public GalleryAdapter(ArrayList<String> galleryList, GalleryItemClickListener galleryItemClickListener) {
        this.galleryList = galleryList;
        this.galleryItemClickListener = galleryItemClickListener;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GalleryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final GalleryViewHolder holder, int position) {
        final String imageModel = galleryList.get(position);

        Glide.with(holder.galleryImageView.getContext()).
                load(imageModel)
                .thumbnail(0.5f)
                .into(holder.galleryImageView);

        holder.galleryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // galleryItemClickListener.onGalleryItemClickListener(holder.getAdapterPosition(), imageModel, holder.galleryImageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        private ImageView galleryImageView;

        GalleryViewHolder(View view) {
            super(view);
            galleryImageView = (ImageView) view.findViewById(R.id.galleryImage) ;
        }
    }
}