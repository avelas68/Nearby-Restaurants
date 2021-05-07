package com.example.nearbyrestaurants;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartMenu extends AppCompatActivity implements View.OnClickListener {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        button = findViewById(R.id.Start);

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == button.getId()) {
            Intent intent = new Intent(this, MainMenu.class);
            startActivity(intent);
        }
    }
}