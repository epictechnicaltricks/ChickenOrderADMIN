package gvit.chicken.adminapp.All_settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import gvit.chicken.adminapp.MainActivity;
import gvit.chicken.adminapp.R;
import gvit.chicken.adminapp.RequestNetwork;
import gvit.chicken.adminapp.RequestNetworkController;

public class Product extends AppCompatActivity {

    RecyclerView recyclerview1;
    private RequestNetwork get_all_products_api;
    private RequestNetwork.RequestListener _get_all_products_api_listener;

    private RequestNetwork delete_products_api;
    private RequestNetwork.RequestListener _delete_api_listener;

    private ArrayList<HashMap<String, Object>> product_map = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);


        recyclerview1 = findViewById(R.id.recyclerview1);

        get_all_products_api = new RequestNetwork(this);
        _get_all_products_api_listener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                try {

                    if (response.contains("200")) {
                        HashMap<String, Objects> map;
                        map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                        String values = (new Gson()).toJson(map.get("user"), new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
                        product_map = new Gson().fromJson(values, new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());


                        Collections.reverse(product_map);
                        recyclerview1.setAdapter(new Recyclerview1Adapter(product_map));
                        recyclerview1.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerview1.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


                    } else {

                        product_map.clear();
                        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {

                    Log.d("api_error", e.toString());
                    Toast.makeText(getApplicationContext(), "Error on API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(String tag, String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            }
        };

        delete_products_api = new RequestNetwork(this);
        _delete_api_listener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                if(response.contains("200"))
                {
                    try {
                        Toast.makeText(Product.this, "Deleted", Toast.LENGTH_SHORT).show();
                        get_all_products_request();

                    }catch (Exception e)
                    {
                        Toast.makeText(Product.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onErrorResponse(String tag, String message) {

            }
        };


    }


    @Override
    protected void onResume() {
        super.onResume();
        get_all_products_request();
    }

    
    private void get_all_products_request() {
       // Toast.makeText(this, "LOADING ALL PRODUCTS", Toast.LENGTH_LONG).show();
        get_all_products_api.startRequestNetwork(RequestNetworkController.GET,
                "https://cityneedzapi.000webhostapp.com/chicken-api/product_show.php",
                "no tag", _get_all_products_api_listener);
    }

    private void delete_product_request(String _product_id) {

       Toast.makeText(this, "Deleting..", Toast.LENGTH_SHORT).show();
        delete_products_api.startRequestNetwork(RequestNetworkController.GET,
                "https://cityneedzapi.000webhostapp.com/chicken-api/DeleteProduct.php?product_id="+_product_id,
                "no tag", _delete_api_listener);
    }




    public void add_product(View view)
    {
        Intent i = new Intent();
        i.setClass(getApplicationContext(), EditAddProduct.class);
        startActivity(i);
    }


    public class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;
        public Recyclerview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @NonNull
        @Override
        public Recyclerview1Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.custom_product_layout, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new Recyclerview1Adapter.ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(Recyclerview1Adapter.ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;



            final TextView name = _view.findViewById(R.id.name);
            final TextView date = _view.findViewById(R.id.date);
            final TextView desc = _view.findViewById(R.id.desc);
            final TextView price = _view.findViewById(R.id.amount);

            final TextView edit = _view.findViewById(R.id.edit);
            final TextView delete = _view.findViewById(R.id.delete);






            try{

                name.setText(Objects.requireNonNull(product_map.get(_position).get("product_name")).toString().toUpperCase());
                date.setText("UPDATED DATE: "+Objects.requireNonNull(product_map.get(_position).get("product_date")));
                desc.setText("Description: "+ Objects.requireNonNull(product_map.get(_position).get("product_desc")));
                price.setText("Price: ₹"+ Objects.requireNonNull(product_map.get(_position).get("product_price")));

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent();
                        i.setClass(getApplicationContext(), EditAddProduct.class);
                        i.putExtra("edit","true");
                        i.putExtra("name",Objects.requireNonNull(product_map.get(_position).get("product_name")).toString());
                        i.putExtra("desc",Objects.requireNonNull(product_map.get(_position).get("product_desc")).toString());
                        i.putExtra("price",Objects.requireNonNull(product_map.get(_position).get("product_price")).toString());
                        i.putExtra("id",Objects.requireNonNull(product_map.get(_position).get("product_id")).toString());
                        startActivity(i);
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delete_product_request(Objects.requireNonNull(product_map.get(_position).get("product_id")).toString());
                    }
                });

            }catch (Exception e)
            {
                //showMessage("887 line "+e.toString());
            }





        }

        @Override
        public int getItemCount() {
            return _data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public ViewHolder(View v){
                super(v);
            }
        }

    }






/*

 */

}
