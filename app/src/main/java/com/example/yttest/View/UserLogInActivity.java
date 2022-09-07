package com.example.yttest.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yttest.R;
import com.example.yttest.databinding.ActivityUserLogInBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserLogInActivity extends AppCompatActivity {

    private ActivityUserLogInBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_log_in);
        binding = ActivityUserLogInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth= FirebaseAuth.getInstance();

    }



    public void UserSignUpClick (View view){

        String email = binding.UserEnterLogInEmail.getText().toString();
        String password = binding.UserEnterLogInPassword.getText().toString();


        if(email.equals("") || password.equals("")){
            Toast.makeText(this,"Please enter email and password.",Toast.LENGTH_LONG).show();
        } else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {


                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(UserLogInActivity.this,MapsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {


                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserLogInActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });



        }

    }


    public void UserSignInSelected (View view){

        TextView logInText = (TextView)findViewById(R.id.userSignInText);

        logInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLogInActivity.this, UserSignUpActivity.class));
            }
        });

    }


}