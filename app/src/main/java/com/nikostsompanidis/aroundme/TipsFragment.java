package com.nikostsompanidis.aroundme;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class TipsFragment extends Fragment {

    private ArrayList<Tip> tips = new ArrayList<>();
    private RecyclerView recyclerView;
    private TipsAdapter mAdapter;
    private TextView noTips;

    public TipsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static TipsFragment newInstance() {
        TipsFragment fragment = new TipsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tips = getArguments().getParcelableArrayList("tips");
        }
      //  Collections.sort(tips);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tips, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        recyclerView = (RecyclerView) view.findViewById(R.id.tipsRecyclerView);

        mAdapter = new TipsAdapter(tips);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


    }


    public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.MyViewHolder> {

        private List<Tip> list;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public ImageView userImage, tipImage;
            public TextView name, createdAt, upvotes, downvotes,text;


            public MyViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.nameTextView);
                createdAt = (TextView) view.findViewById(R.id.createdAtTextView);
                upvotes = (TextView) view.findViewById(R.id.upvoesTextView);
                downvotes = (TextView) view.findViewById(R.id.downvotesTextView);
                userImage = (CircleImageView) view.findViewById(R.id.profile_image);
                tipImage = (ImageView) view.findViewById(R.id.tipImage);
                text=(TextView)view.findViewById(R.id.textTextView);
            }
        }


        public TipsAdapter(List<Tip> tips) {
            this.list = tips;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tips, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Tip tip = list.get(position);
            String name = tip.getUser().getName() + " " + tip.getUser().getSurname();
            holder.name.setText(name);

            Date date = new Date(tip.getCreatedAt().getTime()*1000);
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String createdat = df.format(date);
            holder.createdAt.setText(createdat);
            holder.upvotes.setText("" + tip.getUpvotes());
            holder.downvotes.setText("" + tip.getDownvotes());
            holder.text.setText(tip.getText());
            Glide.with(getActivity())
                    .load(tip.getUser().getImageUrl())
                    .into(holder.userImage);
            if(!tip.getImageUrl().equals(""))
                Glide.with(getActivity())
                        .load(tip.getImageUrl())
                        .into(holder.tipImage);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void clear() {
            // TODO Auto-generated method stub
            list.clear();

        }

        private void addItem(Tip tip) {
            list.add(tip);
            this.notifyDataSetChanged();
        }


    }


}
