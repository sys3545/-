package com.example.mysimulationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private GameView myView;
    private Button onBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onBtn = (Button)findViewById(R.id.onButton);
        myView = (GameView)findViewById(R.id.GameView);
    }

    public void launch(View view){
        myView.launch();
    }

    public void fast(View view){
        myView.faster();
    }

    public void slow(View view){
        myView.slower();
    }
}