package com.example.yttest.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yttest.Model.UsersData;
import com.example.yttest.R;
import com.example.yttest.databinding.ActivityAdminLogInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminLogInActivity extends AppCompatActivity {

    private ActivityAdminLogInBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_log_in);
        binding = ActivityAdminLogInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth= FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (user != null) {
            Intent intent = new Intent(AdminLogInActivity.this,MapsActivity.class);
            startActivity(intent);
            finish();
        }

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
                    CollectionReference userRef = firebaseFirestore.collection("users");

// Create a query against the collection.
                    Query query = userRef.whereEqualTo("email", email);
                    query.get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                           UsersData data = document.toObject( UsersData.class);
                                           if(data.isAdmin){
                                               // TODO: Admin
                                           } else {
                                               // TODO: Users
                                           }
                                        }
                                    } else {

                                    }
                                }
                            });
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


    public void AdminSignInSelected (View view){

        TextView logInText = (TextView)findViewById(R.id.logInText);

        logInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLogInActivity.this, AdminSignUpActivity.class));
            }
        });

    }


}