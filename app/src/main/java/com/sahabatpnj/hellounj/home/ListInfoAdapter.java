package com.sahabatpnj.hellounj.home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sahabatpnj.hellounj.DetailInfoActivity;
import com.sahabatpnj.hellounj.R;
import com.sahabatpnj.hellounj.model.PostArtikel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ListInfoAdapter extends RecyclerView.Adapter<ListInfoAdapter.Viewholder>{

    private List<PostArtikel> mInfoList;
    private Context mContext;


    public ListInfoAdapter( Context mContext) {
        this.mInfoList = new ArrayList<>();
        this.mContext = mContext;
    }

    public void addAll(List<PostArtikel> newInfo){
        int initSize = mInfoList.size();
        mInfoList.addAll(newInfo);
        notifyItemRangeChanged(initSize,newInfo.size());
    }

    public String getLastItemId(){
        return mInfoList.get(mInfoList.size()-1).getId();

    }
    public void removeLastItem(){
//        mInfoList.remove(mInfoList.size()-1);
        mInfoList.clear();
    }
    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else return 2;
    }
    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_first_info, parent, false);
            // inflate your first item layout & return that viewHolder
        } else {
            // inflate your second item layout & return that viewHolder
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_info, parent, false);
        }

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, final int position) {
        Glide.with(mContext).asBitmap().load(mInfoList.get(position).getGambar())
                .apply(new RequestOptions().placeholder(R.drawable.hellounj).error(R.drawable.hellounj))
                .into(holder.mImageHolder);
        holder.mJudulHolder.setText(mInfoList.get(position).getJudul());
        holder.mDateHolder.setText(mInfoList.get(position).getWaktu());
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailInfoActivity.class);
                intent.putExtra("ID_ARTIKEL", mInfoList.get(position).getId());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ImageView mImageHolder;
        TextView mJudulHolder, mDateHolder;
        RelativeLayout mLayout;

        public Viewholder(View itemView) {
            super(itemView);
            mImageHolder = itemView.findViewById(R.id.imageInfoGambar);
            mJudulHolder = itemView.findViewById(R.id.textInfoJudul);
            mDateHolder = itemView.findViewById(R.id.textInfoDate);
            mLayout = itemView.findViewById(R.id.layoutInfoList);
        }
    }
}
