package com.sahabatpnj.hellounj.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sahabatpnj.hellounj.R;
import com.sahabatpnj.hellounj.model.PostArtikel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "DetailCategoryFragment";

    private RecyclerView mRecycler;

    private ListInfoAdapter adapter;

    private ProgressBar mProgress;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mRecycler = rootView.findViewById(R.id.listHomeNews);
        mProgress = rootView.findViewById(R.id.progressbarHomeLoading);

        mText = rootView.findViewById(R.id.textNoData);

        initRecycler();
        adapter.removeLastItem();
        adapter.notifyDataSetChanged();
        getArtikel();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeHomeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                adapter.removeLastItem();
                adapter.notifyDataSetChanged();
                getArtikel();
            }
        });

        return rootView;
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        adapter.removeLastItem();
        adapter.notifyDataSetChanged();
        getArtikel();

        if (mRecycler != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onPause() {
        adapter.removeLastItem();
        adapter.notifyDataSetChanged();
        getArtikel();
        super.onPause();
    }

    @Override
    public void onResume() {
        adapter.removeLastItem();
        adapter.notifyDataSetChanged();
        getArtikel();
        super.onResume();
    }


    private void getArtikel() {
        mProgress.setVisibility(View.VISIBLE);
        mText.setVisibility(View.INVISIBLE);
        Query query;
        query = FirebaseDatabase.getInstance().getReference().child("artikel").orderByKey();
        query.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mText.setVisibility(View.VISIBLE);
                    mProgress.setVisibility(View.INVISIBLE);
                } else if (dataSnapshot.hasChildren()) {
                    mProgress.setVisibility(View.INVISIBLE);
                    List<PostArtikel> newInfo = new ArrayList<>();
                    for (DataSnapshot infoSnapshot : dataSnapshot.getChildren()) {
                        newInfo.add(infoSnapshot.getValue(PostArtikel.class));
                    }
                        adapter.removeLastItem();
                        adapter.notifyDataSetChanged();
                        Collections.reverse(newInfo);
                        adapter.addAll(newInfo);
                        mText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void toast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void initRecycler() {
        adapter = new ListInfoAdapter(getContext());
        mRecycler.setAdapter(adapter);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecycler.getContext(),
                mLayoutManager.getOrientation());
        mRecycler.addItemDecoration(dividerItemDecoration);
    }
}