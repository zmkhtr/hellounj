package com.sahabatpnj.hellounj;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sahabatpnj.hellounj.discussion.ListCommentAdapter;
import com.sahabatpnj.hellounj.home.ListInfoAdapter;
import com.sahabatpnj.hellounj.model.PostArtikel;
import com.sahabatpnj.hellounj.model.UserDetail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailDiscussionActivity extends AppCompatActivity  {
    private static final String TAG = "DetailDiscussionActivit";

    private CircleImageView mImageHolder;
    private TextView mNameHolder, mStatusHolder, mJurusanHolder, mDateHolder;
    private String ID_DISCUSSION;
    private RecyclerView mRecycler;

    private ListCommentAdapter adapter;

    private ProgressBar mProgress;

    private TextView mText;
    private ImageButton mSend;
    private EditText mComment;
    private String uid;
    private UserDetail mUser;
    private TextView mNotLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_discussion);

        Intent intent = getIntent();
        mUser = new UserDetail();
        ID_DISCUSSION = intent.getStringExtra("ID_DISCUSSION");

        mComment = findViewById(R.id.etPopUpComment);
        mNotLogin = findViewById(R.id.notLogin);
        mSend = findViewById(R.id.btnPopUpSend);
        mRecycler = findViewById(R.id.recyclerPopUpComment);
        mProgress = findViewById(R.id.progressbarHomeLoading);
        mText = findViewById(R.id.textNoData);
        mImageHolder = findViewById(R.id.imageDiscussionProfile);
        mJurusanHolder = findViewById(R.id.textDiscussionJurusan);
        mDateHolder = findViewById(R.id.textDiscussionDate);
        mNameHolder = findViewById(R.id.textDiscussionName);
        mStatusHolder = findViewById(R.id.textDiscussionIsi);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Diskusi");

        readData();
        showComment();
        initRecycler();
        adapter.removeLastItem();
        adapter.notifyDataSetChanged();
        getArtikel();

        if (!mUser.isUserLogin()){
            mComment.setVisibility(View.INVISIBLE);
            mComment.setEnabled(false);
            mSend.setVisibility(View.INVISIBLE);
            mSend.setEnabled(false);
            mNotLogin.setVisibility(View.VISIBLE);
        } else {
            mNotLogin.setVisibility(View.INVISIBLE);
            mComment.setVisibility(View.VISIBLE);
            mComment.setEnabled(true);
            mSend.setVisibility(View.VISIBLE);
            mSend.setEnabled(true);
        }



    }

    private void readData() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("discussion");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String image = dataSnapshot.child(ID_DISCUSSION).child("photoUrl").getValue(String.class);
                String nama = dataSnapshot.child(ID_DISCUSSION).child("nama").getValue(String.class);
                String jurusan = dataSnapshot.child(ID_DISCUSSION).child("jurusan").getValue(String.class);
                String date = dataSnapshot.child(ID_DISCUSSION).child("commentDate").getValue(String.class);
                String isi = dataSnapshot.child(ID_DISCUSSION).child("comment").getValue(String.class);

                mNameHolder.setText(nama);
                mDateHolder.setText(date);
                mJurusanHolder.setText(jurusan);
                mStatusHolder.setText(isi);
                Glide.with(getApplicationContext()).load(image)
                        .apply(new RequestOptions().placeholder(R.drawable.hellounj).error(R.drawable.hellounj))
                        .into(mImageHolder);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
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
        query = FirebaseDatabase.getInstance().getReference().child("commentDiscussion").child(ID_DISCUSSION).orderByKey();
        query.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
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

    private void initRecycler() {
        adapter = new ListCommentAdapter(getApplicationContext());
        mRecycler.setAdapter(adapter);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecycler.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecycler.getContext(),
                mLayoutManager.getOrientation());
        mRecycler.addItemDecoration(dividerItemDecoration);

    }
    public void showComment(){
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mComment.getText().toString().isEmpty()) {
                    toast("Mohon tambahkan comment terlebih dahulu");
                } else {
                    readDataUser(mComment.getText().toString());
                    mComment.setText("");
                }
            }
        });
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
                sendComment(comment, dataNama, dataPhotoUrl, dataJurusan);
                Log.d(TAG, "onDataChange: " + dataNama);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error " + databaseError.getMessage());
            }
        });
    }

    public void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendComment(String comment, String nama, String photoUrl, String jurusan) {
        UserDetail mUserComment = new UserDetail();

        mUserComment.setComment(comment);
        mUserComment.setNama(nama);
        mUserComment.setPhotoUrl(photoUrl);
        mUserComment.setJurusan(jurusan);
        mUserComment.setCommentDate(getDate());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("commentDiscussion").child(ID_DISCUSSION);
        String key = mRef.push().getKey();
        mUserComment.setId(key);
        mRef.child(key).setValue(mUserComment);
        toast("Komentar berhasil ditambahkan");

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
}
