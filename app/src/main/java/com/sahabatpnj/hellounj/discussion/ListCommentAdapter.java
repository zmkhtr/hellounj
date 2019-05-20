package com.sahabatpnj.hellounj.discussion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sahabatpnj.hellounj.R;
import com.sahabatpnj.hellounj.model.PostArtikel;
import com.sahabatpnj.hellounj.model.UserDetail;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListCommentAdapter extends RecyclerView.Adapter<ListCommentAdapter.Viewholder>{

    private List<UserDetail> mCommentList;
    private Context mContext;


    public ListCommentAdapter(Context mContext) {
        this.mCommentList = new ArrayList<>();
        this.mContext = mContext;
    }

    public void addAll(List<PostArtikel> newInfo){
        int initSize = mCommentList.size();
//        Collections.reverse(newInfo);
        mCommentList.addAll(newInfo);
        notifyItemRangeChanged(initSize,newInfo.size());
    }

    public String getLastItemId(){
        return mCommentList.get(mCommentList.size()-1).getId();

    }
    public void removeLastItem(){
//        mInfoList.remove(mInfoList.size()-1);
        mCommentList.clear();
    }
    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_discussion, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Glide.with(mContext).asBitmap().load(mCommentList.get(position).getPhotoUrl())
                .apply(new RequestOptions().placeholder(R.drawable.hellounj).error(R.drawable.hellounj))
                .into(holder.mImageHolder);
        holder.mNameHolder.setText(mCommentList.get(position).getNama());
        holder.mJurusanHolder.setText(mCommentList.get(position).getJurusan());
        holder.mDateHolder.setText(mCommentList.get(position).getCommentDate());
        holder.mStatusHolder.setText(mCommentList.get(position).getComment());
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        CircleImageView mImageHolder;
        TextView mNameHolder, mStatusHolder, mJurusanHolder, mDateHolder;
        RelativeLayout mLayout;

        public Viewholder(View itemView) {
            super(itemView);
            mImageHolder = itemView.findViewById(R.id.imageDiscussionProfile);
            mJurusanHolder = itemView.findViewById(R.id.textDiscussionJurusan);
            mDateHolder = itemView.findViewById(R.id.textDiscussionDate);
            mNameHolder = itemView.findViewById(R.id.textDiscussionName);
            mStatusHolder = itemView.findViewById(R.id.textDiscussionIsi);
            mLayout = itemView.findViewById(R.id.layoutListDiscussion);
        }
    }
}
