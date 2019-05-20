package com.sahabatpnj.hellounj.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sahabatpnj.hellounj.MainActivity;
import com.sahabatpnj.hellounj.R;
import com.sahabatpnj.hellounj.category.CategoryFragment;
import com.sahabatpnj.hellounj.home.HomeFragment;

public class AccountLoginFragment extends Fragment{
    private static final String TAG = "AccountFragment";
    private Button mSignIn;
    private Button mSignup;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_login,container,false);
        Log.d(TAG, "onCreateView: start");

        mSignIn = rootView.findViewById(R.id.btnLoginFrag);
        mSignup = rootView.findViewById(R.id.btnRegister);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Akun");
        buttonNav();
        return rootView;
    }

    private void buttonNav(){
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(i);
                getActivity().finish();
            }
        });
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), RegisterActivity.class);
                getActivity().startActivity(i);
                getActivity().finish();
            }
        });
    }


}
