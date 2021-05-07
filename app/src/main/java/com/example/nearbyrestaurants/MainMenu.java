package com.example.nearbyrestaurants;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity {
    public Spinner spinner1, spinner2, spinner3;
    private Button btnMap;
    private int x, y;
    private String z;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectListener());

        spinner2 = findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new CustomOnItemSelectListener());

        spinner3 = findViewById(R.id.spinner3);
        spinner3.setOnItemSelectedListener(new CustomOnItemSelectListener());
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {

        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        spinner3 = findViewById(R.id.spinner3);

        btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MainMenu.this, "OnClickListener : " +
                                "\nSpinner 1 : " +
                                String.valueOf(spinner1.getSelectedItem()) +
                                "\nSpinner 2 : " +
                                String.valueOf(spinner2.getSelectedItem()) +
                                "\nSpinner 3 : " +
                                String.valueOf(spinner3.getSelectedItem())
                        , Toast.LENGTH_SHORT).show();
                if (v.getId() == btnMap.getId()) {
                    Intent intent = new Intent(MainMenu.this, MainActivity.class);
                    x = Integer.parseInt(spinner2.getSelectedItem().toString().substring(0, 1));
                    y = Integer.parseInt(spinner3.getSelectedItem().toString().substring(0, 1));
                    z = spinner1.getSelectedItem().toString();
                    intent.putExtra("price", x);
                    intent.putExtra("distance", y);
                    intent.putExtra("cuisine", z);
                    startActivity(intent);
                }
            }
        });
    }

    private class CustomOnItemSelectListener implements android.widget.AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }
}