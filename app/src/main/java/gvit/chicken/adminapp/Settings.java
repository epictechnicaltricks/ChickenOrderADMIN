package gvit.chicken.adminapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import gvit.chicken.adminapp.All_settings.Product;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void open_add_update(View view)
    {
        startActivity(new Intent(getApplicationContext(), Product.class));
    }
}