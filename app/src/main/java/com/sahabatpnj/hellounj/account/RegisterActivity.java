package com.sahabatpnj.hellounj.account;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sahabatpnj.hellounj.MainActivity;
import com.sahabatpnj.hellounj.R;
import com.sahabatpnj.hellounj.model.UserDetail;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import id.zelory.compressor.Compressor;


public class RegisterActivity extends AppCompatActivity implements IPickResult {
    private static final String TAG = "RegisterActivity";

    private EditText mEmail, mPassword, mRePasword, mNama;
    private AutoCompleteTextView mJurusan;
    private Button mRegister;
    private UserDetail mUser;
    private ProgressBar mLoading;
    private TextView mLogin;
    private ImageView mImage;

    private Uri filepath;
    private File mActualImage;
    private File mCompressedImage;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        declareViewId();
        autoCompleteJurusan();
        onClickAction();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Daftar");
    }

    private void declareViewId() {
        mEmail = findViewById(R.id.etRegisterEmail);
        mPassword = findViewById(R.id.etRegisterPassword);
        mRePasword = findViewById(R.id.etRegisterRePassword);
        mNama = findViewById(R.id.etRegisterName);
        mJurusan = findViewById(R.id.etRegisterJurusan);
        mRegister = findViewById(R.id.btnRegister);
        mLoading = findViewById(R.id.progressbarRegisterLoading);
        mImage = findViewById(R.id.imageRegisterAdd);
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        mLogin = findViewById(R.id.textRegisterToSignIn);
        mUser = new UserDetail();
    }

    private void autoCompleteJurusan() {
        String[] mJurusanList;
        mJurusanList = getResources().getStringArray(R.array.jurusan);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, mJurusanList
        );
        mJurusan.setAdapter(adapter);
    }

    private void onClickAction() {
        registeUser();
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup().setWidth(100).setHeight(100)).show(getSupportFragmentManager());
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {

            mImage.setImageBitmap(r.getBitmap());
            mImage.setImageURI(filepath);

            filepath = r.getUri();
            Log.d(TAG, "onPickResult: afile " + mActualImage);
            Log.d(TAG, "onPickResult: filepath " + filepath);
//            mActualImage = new File(filepath.getPath());
        } else {
            Log.d(TAG, "onPickResult: error image picker " + r.getError().getMessage());
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void registeUser() {
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, rePassword, jurusan, nama, date, photoUrl;
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                rePassword = mRePasword.getText().toString();
                nama = mNama.getText().toString();
                jurusan = mJurusan.getText().toString();
                date = getDate();
                photoUrl = "sample";

                if (email.isEmpty()) {
                    mEmail.setError("email tidak boleh kosong");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError("mohon gunakan email yang benar, co: mail@email.com");
                } else if (nama.isEmpty()) {
                    mNama.setError("Nama tidak boleh kosong");
                } else if (password.isEmpty()) {
                    mPassword.setError("Kata sandi tidak boleh kosong");
                } else if (password.length() <= 5) {
                    mPassword.setError("Kata sandi minimal 6 karakter");
                } else if (!rePassword.equals(password)) {
                    mRePasword.setError("Kata sandi tidak sama");
                } else if (jurusan.isEmpty()) {
                    mJurusan.setError("Jurusan tidak boleh kosong");
                } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && rePassword.equals(password)) {
                    uploadImage(email, password, nama, jurusan, date);
                }

            }
        });
    }

    private void proceedRegitser(final String email, final String password, final String nama, final String photoUrl,
                                 final String jurusan, final String date) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user.getUid() != null){
                                inputDataToDatabase(email, password, nama, photoUrl,user.getUid(), jurusan, date);
                            }
                            mUser.logoutUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            toast("Gagal Mendaftar, pastikan anda terhubung dengan internet dan coba lagi");
                            toast("Gagal mendaftar : " + task.getException());
                        }
                        // ...
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
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void inputDataToDatabase(String email, String password, String nama, String photoUrl,String userId,
                                     String jurusan, String date) {
        mUser.setEmail(email);
        mUser.setNama(nama);
        mUser.setPassword(password);
        mUser.setPhotoUrl(photoUrl);
        mUser.setUserId(userId);
        mUser.setJurusan(jurusan);
        mUser.setRegisterDate(date);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("users");
        mRef.child(userId).setValue(mUser);

    }

    private void uploadImage(final String email, final String password, final String nama,
                             final String jurusan, final String date) {
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

                                    proceedRegitser(email, password, nama, uri.toString(), jurusan, date);

                                    Log.d(TAG, "MyDownloadLink:  " + uri.toString());
                                }
                            });
                            mLoading.setVisibility(View.INVISIBLE);
                            toast("Daftar berhasil silahkan masuk untuk melanjutkan");
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
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


    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
