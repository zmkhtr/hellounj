package com.sahabatpnj.hellounj.category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sahabatpnj.hellounj.DetailInfoActivity;
import com.sahabatpnj.hellounj.MainActivity;
import com.sahabatpnj.hellounj.R;
import com.sahabatpnj.hellounj.account.AccountFragment;
import com.sahabatpnj.hellounj.home.HomeFragment;

import java.util.ArrayList;

public class ListCategoryAdapter extends RecyclerView.Adapter<ListCategoryAdapter.Viewholder>{

    private ArrayList<String> mJudul;
    private ArrayList<String> mImage;
    private Context mContext;

    public ListCategoryAdapter(Context mContext, ArrayList<String> mJudul, ArrayList<String> mImage) {
        this.mJudul = mJudul;
        this.mImage = mImage;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_category, parent, false);
        return new Viewholder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, final int position) {
        Glide.with(mContext).load(mImage.get(position))
                .apply(new RequestOptions().placeholder(R.drawable.hellounj).error(R.drawable.hellounj))
                .into(holder.mImageHolder);
        holder.mJudulHolder.setText(mJudul.get(position));
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("ID_CATEGORY", mJudul.get(position));
                DetailCategoryFragment mFragment = new DetailCategoryFragment();
                mFragment.setArguments(bundle);
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        mFragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mJudul.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ImageView mImageHolder;
        TextView mJudulHolder;
        RelativeLayout mLayout;

        public Viewholder(View itemView) {
            super(itemView);
            mImageHolder = itemView.findViewById(R.id.imageHomeGambar);
            mJudulHolder = itemView.findViewById(R.id.textHomeJudul);
            mLayout = itemView.findViewById(R.id.layoutListCategory);
        }
    }
}
