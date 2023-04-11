package gvit.chicken.adminapp.All_settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import gvit.chicken.adminapp.R;
import gvit.chicken.adminapp.RequestNetwork;
import gvit.chicken.adminapp.RequestNetworkController;

public class EditAddProduct extends AppCompatActivity {

    EditText name, price, desc;
    Button add, update, close;

    private RequestNetwork product_api;
    private RequestNetwork.RequestListener _api_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add_product);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        desc=findViewById(R.id.desc);

        add = findViewById(R.id.add);
        update  = findViewById(R.id.update);
        close = findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add.setOnClickListener(view -> {
            if(!name.getText().toString().trim().equals("") && !desc.getText().toString().trim().equals("")
                    && price.getText().toString().trim().length() >= 2)
            {
                product_request(name.getText().toString(),price.getText().toString(),desc.getText().toString());

            } else {

                Toast.makeText(EditAddProduct.this, "invalid data entered", Toast.LENGTH_LONG).show();
            }
                 });


        update.setOnClickListener(view -> {
            if(!name.getText().toString().trim().equals("") && !desc.getText().toString().trim().equals("")
                    && price.getText().toString().trim().length() >= 2)
            {
            product_request(name.getText().toString(),price.getText().toString(),desc.getText().toString());
            } else {

                Toast.makeText(EditAddProduct.this, "invalid data entered", Toast.LENGTH_LONG).show();
            }


            });


        product_api = new RequestNetwork(this);

        _api_listener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                if(response.contains("200"))
                {
                    if(getIntent().hasExtra("edit"))
                    {

                        Toast.makeText(EditAddProduct.this, "UPDATED", Toast.LENGTH_LONG).show();
                        finish();

                    }else {

                         Toast.makeText(EditAddProduct.this, "ADDED", Toast.LENGTH_LONG).show();
                         startActivity(new Intent(getApplicationContext(), EditAddProduct.class));
                         finish();
                    }
                }
            }

            @Override
            public void onErrorResponse(String tag, String message) {

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().hasExtra("edit"))
        {
            name.setText(getIntent().getStringExtra("name"));
            price.setText(getIntent().getStringExtra("price"));
            desc.setText(getIntent().getStringExtra("desc"));
            add.setVisibility(View.GONE);

        } else {

            update.setVisibility(View.GONE);
        }
    }

    private void product_request(String _name, String _price, String _desc) {


        if(getIntent().hasExtra("edit"))
        {
            //UPDATE
            product_api.startRequestNetwork(RequestNetworkController.GET,
                    "https://cityneedzapi.000webhostapp.com/chicken-api/product_update.php?" +
                            "product_id=" + getIntent().getStringExtra("id") +
                            "&product_name=" + _name.trim() +
                            "&product_price=" + _price.trim() +
                            "&product_desc="+_desc.trim(),
                    "no tag", _api_listener);

            Log.d("api_call","https://cityneedzapi.000webhostapp.com/chicken-api/product_update.php?" +
            "product_id=" + getIntent().getStringExtra("id") +
                    "&product_name=" + _name.trim() +
                    "&product_price=" + _price.trim() +
                    "&product_desc="+_desc.trim());


        } else
        {
            //NEW ADD
            product_api.startRequestNetwork(RequestNetworkController.GET,
                    "https://cityneedzapi.000webhostapp.com/chicken-api/product_add.php?" +
                            "product_name=" + _name.trim() +
                            "&product_price=" + _price.trim() +
                            "&product_desc="+_desc.trim(),
                    "no tag", _api_listener);
        }


    }



}