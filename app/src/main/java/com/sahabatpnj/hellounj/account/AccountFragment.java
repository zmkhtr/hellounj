package com.sahabatpnj.hellounj.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.sahabatpnj.hellounj.R;
import com.sahabatpnj.hellounj.admin.AddPostActivity;
import com.sahabatpnj.hellounj.admin.ListArtikelAdapter;
import com.sahabatpnj.hellounj.model.PostArtikel;
import com.sahabatpnj.hellounj.model.UserDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "AccountFragment";
    private CircleImageView mPhoto;
    private TextView mNama, mEmail, mJurusan, mManage;
    private UserDetail mUser;
    private FirebaseUser mUserFb;
    private String uid;
    private FloatingActionButton mAdd;
    private View rootView;

    private RecyclerView mRecycler;

    private ListArtikelAdapter adapter;

    private ProgressBar mProgress;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_account, container, false);
        Log.d(TAG, "onCreateView: start");

        mUserFb = FirebaseAuth.getInstance().getCurrentUser();
        uid = mUserFb.getUid();
        mUser = new UserDetail();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Akun");
        setHasOptionsMenu(true);

        mNama = rootView.findViewById(R.id.textAccountNama);
        mEmail = rootView.findViewById(R.id.textAccountEmail);
        mJurusan = rootView.findViewById(R.id.textAccountJurusan);
        mPhoto = rootView.findViewById(R.id.imageAccountProfilePhoto);
        mAdd = rootView.findViewById(R.id.fabAccountAddpost);
        mManage = rootView.findViewById(R.id.textAccountManagePost);
        mRecycler = rootView.findViewById(R.id.recyclerAccountManagePost);
        mProgress = rootView.findViewById(R.id.progressbarHomeLoading);
        mText = rootView.findViewById(R.id.textNoDataAccount);

        nonAdmin();
        readData();
        initRecycler();
        adapter.removeLastItem();
        adapter.notifyDataSetChanged();
        getArtikel();
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
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

        return rootView;
    }

    public void nonAdmin(){
        UserDetail mUser = new UserDetail();
        FirebaseUser mFbUser= FirebaseAuth.getInstance().getCurrentUser();
        if(mUser.isUserLogin() && !mFbUser.getUid().equals("RJqp70YRBuN1eaOPQ6cQ6s8flvO2")){
            mRecycler.setEnabled(false);
            mRecycler.setVisibility(View.INVISIBLE);
            mText.setText("");
            mManage.setVisibility(View.INVISIBLE);
            mAdd.setEnabled(false);
            mAdd.hide();
//            mProgress.setVisibility(View.INVISIBLE);
//            mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
//            mSwipeRefreshLayout.setEnabled(false);
        }

    }


    private void readData() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String dataNama = dataSnapshot.child(uid).child("nama").getValue(String.class);
                String dataEmail = dataSnapshot.child(uid).child("email").getValue(String.class);
                String dataJurusan = dataSnapshot.child(uid).child("jurusan").getValue(String.class);
                String dataPhotoUrl = dataSnapshot.child(uid).child("photoUrl").getValue(String.class);
                Log.d(TAG, "onDataChange: " + dataNama);
                mNama.setText(dataNama);
                mEmail.setText(dataEmail);
                mJurusan.setText(dataJurusan);
                Glide.with(rootView.getContext()).load(dataPhotoUrl)
                        .apply(new RequestOptions().placeholder(R.drawable.hellounj).error(R.drawable.hellounj))
                        .into(mPhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.corner_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        UserDetail mUser = new UserDetail();
        switch (item.getItemId()) {
//            case R.id.setting:
//                Log.d(TAG, "onOptionsItemSelected: setting");
//                break;
            case R.id.logout:
                Log.d(TAG, "onOptionsItemSelected: logout");
                mUser.logoutUser();
                Toast.makeText(getContext(), "Berhasil Keluar", Toast.LENGTH_SHORT).show();
                getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AccountLoginFragment()).commit();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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

        adapter = new ListArtikelAdapter(getContext());
        mRecycler.setAdapter(adapter);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecycler.getContext(),
                mLayoutManager.getOrientation());
        mRecycler.addItemDecoration(dividerItemDecoration);

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


}