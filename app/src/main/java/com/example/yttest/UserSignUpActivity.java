package com.example.yttest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yttest.databinding.ActivityUserSignUpBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserSignUpActivity extends AppCompatActivity {

    private ActivityUserSignUpBinding binding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth= FirebaseAuth.getInstance();

    }



    public void UserSignUpClick (View view){

        String email=binding.UserEnterEmailText.getText().toString();
        String password=binding.UserEnterPasswordText.getText().toString();

        if(email.equals("") || password.equals("")){
            Toast.makeText(this,"Please enter email and password.",Toast.LENGTH_LONG).show();
        } else{
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(UserSignUpActivity.this,UserLogInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserSignUpActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });

        }


    }


    public void UserLogInSelected (View view){

        TextView UserLogInText = (TextView)findViewById(R.id.UserLogInText);

        UserLogInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignUpActivity.this, UserLogInActivity.class));
            }
        });

    }

    private void updateUI(FirebaseUser user) {
    }

}