package com.example.yttest.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.yttest.Adapter.UsersMailsAdapter;
import com.example.yttest.Adapter.UsersMailsAdapterListener;
import com.example.yttest.Model.UsersData;
import com.example.yttest.R;
import com.example.yttest.databinding.ActivityUserSelectionScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSelectionScreenActivity extends AppCompatActivity implements UsersMailsAdapterListener {

    ArrayList<UsersData> usersDataArrayList;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ActivityUserSelectionScreenBinding binding;
    RecyclerView recyclerView;

    UsersMailsAdapter usersMailsAdapter;
    private String markerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            markerId = extras.getString("id");
        }

        binding = ActivityUserSelectionScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        usersDataArrayList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        getData();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

         usersMailsAdapter = new UsersMailsAdapter(this, usersDataArrayList,this );
        recyclerView.setAdapter(usersMailsAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    private void getData() {

        firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        usersDataArrayList.add(document.toObject(UsersData.class));
                    };
                    usersMailsAdapter.notifyDataSetChanged();
                } else {
                }
            }
        });

        /*firebaseFirestore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error !=  null) {

                    Toast.makeText(UserSelectionScreenActivity.this,error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                if (value != null) {
                    for (DocumentSnapshot snapshot: value.getDocuments()){

                        Map<String, Object> data = snapshot.getData();

                        String email = (String) data.get("email");
                        String name = (String) data.get("name");

                        UsersData usersData = new UsersData(email,name);
                        usersDataArrayList.add(usersData);


                    }

                    usersMailsAdapter.notifyDataSetChanged();
                }

            }
        });*/
    }

    public void CreateUserClick (View view) {

        Intent intent = new Intent(UserSelectionScreenActivity.this, UserSignUpActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        auth.signOut();
        Intent intentToAdminLogIn = new Intent(UserSelectionScreenActivity.this,AdminLogInActivity.class);
        startActivity(intentToAdminLogIn);
        finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(int position, View view) {
        System.out.println(position);
        Map<String, Object> updateMarker = new HashMap<>();
        updateMarker.put("registeredUserEmail", usersDataArrayList.get(position).email);


        firebaseFirestore.collection("markers").document(markerId)
                .set(updateMarker)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println( "Error writing document");
                    }
                });
    }
}