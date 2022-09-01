package com.example.yttest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yttest.databinding.ActivityAdminLogInBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminLogInActivity extends AppCompatActivity {

    private ActivityAdminLogInBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_log_in);
        binding = ActivityAdminLogInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth= FirebaseAuth.getInstance();

    }



        public void LogInClick (View view){

        String email = binding.EmailLogInText.getText().toString();
        String password = binding.PasswordLogInText.getText().toString();


        if(email.equals("") || password.equals("")){
            Toast.makeText(this,"Please enter email and password.",Toast.LENGTH_LONG).show();
        } else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {


                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(AdminLogInActivity.this,MapsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {


                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminLogInActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });



        }

    }


    public void SignInSeleckted (View view){

        TextView logInText = (TextView)findViewById(R.id.logInText);

        logInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLogInActivity.this, AdminSignUpActivity.class));
            }
        });

    }


}