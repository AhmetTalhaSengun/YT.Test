package com.example.yttest.View;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yttest.R;
import com.example.yttest.databinding.ActivityUserSignUpBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserSignUpActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;

    private ActivityUserSignUpBinding binding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();



    }




    public void UserSignUpClick (View view) {

        String name = binding.editTextTextPersonName.getText().toString();
        String email = binding.UserEnterEmailText.getText().toString();
        String password = binding.UserEnterPasswordText.getText().toString();


        if (email.equals("") || password.equals("") || name.equals("")) {
            Toast.makeText(this, "Please enter email, password and name.", Toast.LENGTH_LONG).show();
        } else {
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("name", name);
                    data.put("email", email);
                    data.put("isAdmin", false);

                    firebaseFirestore.collection("users").add(data)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(UserSignUpActivity.this, "User Created successfully.", Toast.LENGTH_LONG).show();

                            })
                            .addOnFailureListener(e -> Toast.makeText(UserSignUpActivity.this, "User create operation failed.", Toast.LENGTH_LONG).show());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserSignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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