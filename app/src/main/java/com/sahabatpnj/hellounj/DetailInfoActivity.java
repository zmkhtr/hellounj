package com.sahabatpnj.hellounj;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class DetailInfoActivity extends AppCompatActivity {
    private static final String TAG = "DetailInfoActivity";

    private ImageView mImage;
    private TextView mJudul, mKategori, mIsi, mAuthor, mDate;
    private FloatingActionButton mFab;
    private String ID_ARTIKEL;
    private String uid;
    private UserDetail mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);


        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(" ");
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        mUser = new UserDetail();

        ID_ARTIKEL = intent.getStringExtra("ID_ARTIKEL");

        declareView();
        readData();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!mUser.isUserLogin()){
//                    toast("Silahkan login terlebih dahulu !");
//                } else {
//                    onShowPopup(v);
//                }
                onShowPopup(v);
            }
        });




    }




    private void declareView() {
//        mUserFb = FirebaseAuth.getInstance().getCurrentUser();
        mImage = findViewById(R.id.imageDetailActivityGambar);
        mAuthor = findViewById(R.id.textDetailActivityAuthor);
        mDate = findViewById(R.id.textDetailActivityDate);
        mJudul = findViewById(R.id.textDetailActivityJudul);
        mKategori = findViewById(R.id.textDetailActivityKategori);
        mIsi = findViewById(R.id.textDetailActivityIsi);
        mFab = findViewById(R.id.fabComment);
    }

    private void readData() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("artikel");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String image = dataSnapshot.child(ID_ARTIKEL).child("gambar").getValue(String.class);
                String author = dataSnapshot.child(ID_ARTIKEL).child("author").getValue(String.class);
                String date = dataSnapshot.child(ID_ARTIKEL).child("waktu").getValue(String.class);
                String judul = dataSnapshot.child(ID_ARTIKEL).child("judul").getValue(String.class);
                String kategori = dataSnapshot.child(ID_ARTIKEL).child("kategori").getValue(String.class);
                String isi = dataSnapshot.child(ID_ARTIKEL).child("isi").getValue(String.class);

                mAuthor.setText(author);
                mDate.setText(date);
                mJudul.setText(judul);
                mKategori.setText(kategori);
                mIsi.setText(isi);
                Glide.with(getApplicationContext()).load(image)
                        .apply(new RequestOptions().placeholder(R.drawable.hellounj).error(R.drawable.hellounj))
                        .into(mImage);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void onShowPopup(View v) {

        RecyclerView mRecycler;

        final ListCommentAdapter adapter;
        final TextView mText;
        final ProgressBar mProgress;
        final EditText mComment;
        TextView mNotLogin;
        ImageButton mSend;

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflatedView = layoutInflater.inflate(R.layout.popup_layout, null, false);

        mComment = inflatedView.findViewById(R.id.etPopUpComment);
        mSend = inflatedView.findViewById(R.id.btnPopUpSend);
        mRecycler = inflatedView.findViewById(R.id.recyclerPopUpComment);
        mProgress = inflatedView.findViewById(R.id.progressbarHomeLoading);
        mText = inflatedView.findViewById(R.id.textNoData);
        mNotLogin = inflatedView.findViewById(R.id.notLogin);

        //
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

        adapter = new ListCommentAdapter(getApplicationContext());
        mRecycler.setAdapter(adapter);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecycler.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecycler.getContext(),
                mLayoutManager.getOrientation());
        mRecycler.addItemDecoration(dividerItemDecoration);

        adapter.removeLastItem();
        adapter.notifyDataSetChanged();

        mProgress.setVisibility(View.VISIBLE);
        mText.setVisibility(View.INVISIBLE);
        Query query;
        query = FirebaseDatabase.getInstance().getReference().child("comment").child(ID_ARTIKEL).orderByKey();
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

        // inflate the custom popup layout
        // find the ListView in the popup layout
//        ListView listView = (ListView)inflatedView.findViewById(R.id.commentsListView);
//        LinearLayout headerView = (LinearLayout)inflatedView.findViewById(R.id.headerLayout);


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

        // get device size
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
//        mDeviceHeight = size.y;
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        // set height depends on the device size
        PopupWindow popWindow = new PopupWindow(inflatedView, width, height - 50, true);
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_round_white));

        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        popWindow.setAnimationStyle(R.style.PopupAnimation);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 100);
    }

    private void sendComment(String comment, String nama, String photoUrl, String jurusan) {
        UserDetail mUserComment = new UserDetail();

        mUserComment.setComment(comment);
        mUserComment.setNama(nama);
        mUserComment.setPhotoUrl(photoUrl);
        mUserComment.setJurusan(jurusan);
        mUserComment.setCommentDate(getDate());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("comment").child(ID_ARTIKEL);
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


}
