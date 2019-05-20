package com.sahabatpnj.hellounj.discussion;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sahabatpnj.hellounj.MainActivity;
import com.sahabatpnj.hellounj.R;
import com.sahabatpnj.hellounj.model.PostArtikel;
import com.sahabatpnj.hellounj.model.UserDetail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DiscussionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "DiscussionFragment";

    private RecyclerView mRecycler;

    private ListDiscussionAdapter adapter;

    private ProgressBar mProgress;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mText;
    private FloatingActionButton mFab;
    private UserDetail mUser;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discussion, container, false);
        mRecycler = rootView.findViewById(R.id.listHomeNews);
        mProgress = rootView.findViewById(R.id.progressbarHomeLoading);
        mFab = rootView.findViewById(R.id.fab);
        mText = rootView.findViewById(R.id.textNoData);
        mUser = new UserDetail();

        initRecycler();
        adapter.removeLastItem();
        adapter.notifyDataSetChanged();
        getArtikel();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Diskusi");

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

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mUser.isUserLogin()){
                    toast("Silahkan login terlebih dahulu !");
                } else {
                    showAddDiscussion();
                }
            }
        });

        return rootView;
    }

    public void showAddDiscussion(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.popup_add_discussion, null);
        final EditText mStatus = mView.findViewById(R.id.etAddDiscussionText);
        final Button mCancel = mView.findViewById(R.id.btnAddDiscussionBatal);
        final Button mTambah = mView.findViewById(R.id.btnAddDiscussionTambah);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        mTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mStatus.getText().toString().isEmpty()){
                    toast("Isi dulu diskusimu !");
                }else{
                    readDataUser(mStatus.getText().toString());
                    dialog.hide();
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
    }


    private void sendDiscussion(String comment, String nama, String photoUrl, String jurusan) {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = mUser.getUid();
        UserDetail mUserComment = new UserDetail();

        mUserComment.setComment(comment);
        mUserComment.setNama(nama);
        mUserComment.setPhotoUrl(photoUrl);
        mUserComment.setJurusan(jurusan);
        mUserComment.setCommentDate(getDate());
        mUserComment.setUserId(userId);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("discussion");
        String key = mRef.push().getKey();
        mUserComment.setId(key);
        mRef.child(key).setValue(mUserComment);
        toast("Diskusi berhasil ditambahkan");

    }

    private void readDataUser(final String comment) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String dataNama = dataSnapshot.child(uid).child("nama").getValue(String.class);
                String dataJurusan = dataSnapshot.child(uid).child("jurusan").getValue(String.class);
                String dataPhotoUrl = dataSnapshot.child(uid).child("photoUrl").getValue(String.class);
                sendDiscussion(comment, dataNama, dataPhotoUrl, dataJurusan);
                Log.d(TAG, "onDataChange: " + dataNama);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error " + databaseError.getMessage());
            }
        });
    }

    private String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        Log.d(TAG, "getDate: " + sdf.format(new Date()));
        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedDate=dateFormat.format(date);
        return sdf.format(new Date()) +"-"+ formattedDate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        query = FirebaseDatabase.getInstance().getReference().child("discussion").orderByKey();
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

        adapter = new ListDiscussionAdapter(getContext());
        mRecycler.setAdapter(adapter);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecycler.getContext(),
                mLayoutManager.getOrientation());
        mRecycler.addItemDecoration(dividerItemDecoration);

    }

}
