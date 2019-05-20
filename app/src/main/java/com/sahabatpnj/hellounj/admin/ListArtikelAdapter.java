package com.sahabatpnj.hellounj.admin;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sahabatpnj.hellounj.DetailInfoActivity;
import com.sahabatpnj.hellounj.R;
import com.sahabatpnj.hellounj.model.PostArtikel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ListArtikelAdapter extends RecyclerView.Adapter<ListArtikelAdapter.Viewholder>{
    private static final String TAG = "ListArtikelAdapter";
    private List<PostArtikel> mInfoList;
    private Context mContext;


    public ListArtikelAdapter( Context mContext) {
        this.mInfoList = new ArrayList<>();
        this.mContext = mContext;
    }

    public void addAll(List<PostArtikel> newInfo){
        int initSize = mInfoList.size();
//        Collections.reverse(newInfo);
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_info_delete, parent, false);


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
        holder.mDeleteHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("artikel").child(mInfoList.get(position).getId());
                mRef.removeValue();
                Log.d(TAG, "onClick: " + mInfoList.get(position).getId());
                Toast.makeText(mContext, "Berhasil Menghapus", Toast.LENGTH_SHORT).show();
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
        Button mDeleteHolder;
        RelativeLayout mLayout;

        public Viewholder(View itemView) {
            super(itemView);
            mImageHolder = itemView.findViewById(R.id.imageInfoGambar);
            mDeleteHolder = itemView.findViewById(R.id.buttonInfoDelete);
            mJudulHolder = itemView.findViewById(R.id.textInfoJudul);
            mDateHolder = itemView.findViewById(R.id.textInfoDate);
            mLayout = itemView.findViewById(R.id.layoutInfoList);
        }
    }
}
