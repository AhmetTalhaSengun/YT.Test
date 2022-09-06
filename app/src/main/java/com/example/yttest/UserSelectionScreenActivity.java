package com.example.yttest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UserSelectionScreenActivity extends AppCompatActivity {

    private RecyclerView Users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection_screen);

        Users = findViewById(R.id.recyclerView);
    }

    public void CreateUserClick (View view) {

        Intent intent = new Intent(UserSelectionScreenActivity.this, UserSignUpActivity.class);
        startActivity(intent);
        finish();
    }


}