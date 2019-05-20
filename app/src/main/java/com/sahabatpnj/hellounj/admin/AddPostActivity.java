package com.sahabatpnj.hellounj.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sahabatpnj.hellounj.MainActivity;
import com.sahabatpnj.hellounj.R;
import com.sahabatpnj.hellounj.account.LoginActivity;
import com.sahabatpnj.hellounj.account.RegisterActivity;
import com.sahabatpnj.hellounj.model.PostArtikel;
import com.sahabatpnj.hellounj.model.UserDetail;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class AddPostActivity extends AppCompatActivity implements IPickResult {
    private static final String TAG = "AddPostActivity";

    private Spinner spinner;
    private String[] category;
    private PostArtikel mArtikel;
    private Button mAddArtikel;
    private EditText mJudul, mIsi, mAuthor;
    private ProgressBar mLoading;
    private ImageView mImage;

    private Uri filepath;
    private File mActualImage;
    private File mCompressedImage;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);


        declareViewId();
        arraySpinner();
        addArtikel();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tambah Artikel");
    }

    private void declareViewId() {
        spinner = findViewById(R.id.spinnerAddPostCategory);
        mArtikel = new PostArtikel();
        mAddArtikel = findViewById(R.id.btnAddPost);
        mJudul = findViewById(R.id.etAddPostJudul);
        mIsi = findViewById(R.id.etAddPostIsi);
        mLoading = findViewById(R.id.progressbarAddPostLoading);
        mImage = findViewById(R.id.imageAddPostAdd);
        mAuthor = findViewById(R.id.etAddPostAuthor);
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void addArtikel() {
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup().setWidth(100).setHeight(100)).show(getSupportFragmentManager());
            }
        });
        mAddArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String judul, isi, author;
                judul = mJudul.getText().toString();
                isi = mIsi.getText().toString();
                author = mAuthor.getText().toString();
                mJudul.getText().toString();

                if (judul.isEmpty()) {
                    mJudul.setError("Judul tidak boleh kosong");
                } else if (author.isEmpty()) {
                    mAuthor.setError("Author tidak boleh kosong");
                } else if (isi.isEmpty()) {
                    mIsi.setError("Isi tidak boleh kosong");
                } else if(spinner.getSelectedItem().toString().equals("Pilih kategori....")){
                    toast("Mohon pilih kategori");
                } else {
                    uploadImage(judul,isi,getDate(),author,spinner.getSelectedItem().toString());
                }
            }
        });
    }

    private void selectKategori() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text
                    toast("Selected : " + selectedItemText);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                toast("Mohon pilih kategori !");

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {

            mImage.setImageBitmap(r.getBitmap());
            mImage.setImageURI(filepath);

            filepath = r.getUri();
            Log.d(TAG, "onPickResult: afile " + mActualImage);
            Log.d(TAG, "onPickResult: filepath " + filepath);
        } else {
            Log.d(TAG, "onPickResult: error image picker " + r.getError().getMessage());
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void uploadImage(final String judul, final String isi, final String waktu, final String author, final String kategori) {
        if (filepath != null) {
            mLoading.setVisibility(View.VISIBLE);

            final StorageReference ref = mStorageRef.child("images/" + UUID.randomUUID().toString() + ".jpg");


            ref.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {


                                    Log.d(TAG, "MyDownloadLink:  " + uri.toString());
                                    inputDataToDatabase(judul,uri.toString(),isi,waktu,author,kategori);
                                }
                            });
                            mLoading.setVisibility(View.INVISIBLE);
                            toast("Berhasil menambahkan artikel");
                            onBackPressed();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            Log.d(TAG, "onFailure: " + exception);
                            toast("upload gagal");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });
        } else {
            toast("Mohon tambahkan foto");
        }
    }

    private void inputDataToDatabase(String judul, String gambar, String isi, String waktu, String author, String kategori) {
        PostArtikel mArtikel = new PostArtikel();
        mArtikel.setGambar(gambar);
        mArtikel.setKategori(kategori);
        mArtikel.setWaktu(waktu);
        mArtikel.setIsi(isi);
        mArtikel.setJudul(judul);
        mArtikel.setAuthor(author);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("artikel");
        String key = mRef.push().getKey();
        mArtikel.setId(key);
        mRef.child(key).setValue(mArtikel);
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

    private void arraySpinner() {

        category = new String[]{
                "Pilih kategori....",
                "Jurusan",
                "Perkuliahan",
                "Uang Kuliah Tunggal",
                "Info Seminar",
                "Beasiswa"
        };


        List<String> categoryList = new ArrayList<>(Arrays.asList(category));

        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.support_simple_spinner_dropdown_item, categoryList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }
}