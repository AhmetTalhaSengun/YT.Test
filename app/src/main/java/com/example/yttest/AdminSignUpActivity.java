package com.example.yttest;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.yttest.databinding.ActivityAdminSignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;




public class AdminSignUpActivity extends AppCompatActivity {
    private ActivityAdminSignUpBinding binding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminSignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth= FirebaseAuth.getInstance();

    }



    public void SignUpClick (View view){

        String email=binding.EnterEmailText.getText().toString();
        String password=binding.PasswordText.getText().toString();

        if(email.equals("") || password.equals("")){
            Toast.makeText(this,"Email ve şifrenizi giriniz.",Toast.LENGTH_LONG).show();
        } else{
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(AdminSignUpActivity.this,AdminLogInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminSignUpActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });

        }


    }


    public void LogInSeleckted (View view){

        TextView LogInText = (TextView)findViewById(R.id.LogInText);

        LogInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminSignUpActivity.this, AdminLogInActivity.class));
            }
        });

    }

    private void updateUI(FirebaseUser user) {
    }

}
