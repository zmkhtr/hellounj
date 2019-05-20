package com.sahabatpnj.hellounj.category;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahabatpnj.hellounj.R;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {
    private static final String TAG = "CategoryFragment";

    private ArrayList<String> mJudul = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_category, container, false);
        initList();


        //recyclerView
        RecyclerView mRecycler = rootView.findViewById(R.id.listCategory);
//        mJudul.clear();
        ListCategoryAdapter adapter = new ListCategoryAdapter(getContext(),mJudul,mImageUrls);

        mRecycler.setAdapter(adapter);
        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));

        adapter.notifyDataSetChanged();
        //endRecyclerView
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Kategori");
        return rootView;
    }

    @Override
    public void onPause() {
        mJudul.clear();
        super.onPause();
    }



    private void initList(){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hellounj-676a5.appspot.com/o/kategori%2Finfo%20jurusan.png?alt=media&token=ef96ec31-0d1e-4715-b8d3-14107ae915a8");
        mJudul.add("Jurusan");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hellounj-676a5.appspot.com/o/kategori%2Fmatakuliah.png?alt=media&token=f6a73b21-a457-4768-a76b-e8efeac6807d");
        mJudul.add("Perkuliahan");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hellounj-676a5.appspot.com/o/kategori%2Fuang%20kuliah%20tahunan.png?alt=media&token=8ea0912b-ce98-4055-aaea-b0ed7a2a1274");
        mJudul.add("Uang Kuliah Tunggal");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hellounj-676a5.appspot.com/o/kategori%2Finfo%20seminar.png?alt=media&token=cb68c222-a149-42dd-a98e-fd0cfcc6fdd2");
        mJudul.add("Info Seminar");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hellounj-676a5.appspot.com/o/kategori%2Finfo%20beasiswa.png?alt=media&token=6edc6af4-2e7d-404e-8212-3b577c802dcc");
        mJudul.add("Beasiswa");
    }
}
