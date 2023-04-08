package gvit.chicken.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerview1;
    private RequestNetwork get_orders;
    private RequestNetwork.RequestListener _get_orders_api_listener;

    private RequestNetwork filter_date_api;


    private RequestNetwork update_status_api;
    private RequestNetwork.RequestListener _update_status_api_listener;

    private  ArrayList<HashMap<String, Object>> listmap2 = new ArrayList<>();
    TextView date_calender,  filter, filter_user_date, filter_order_date;
    String date_selected="", order_status="process", order_type="HOME";

    String[] order_status_spinner_data = {"PROCESS", "READY", "DELIVERED"};
    String[] order_type_spinner_data = {"HOME", "PICKUP"};



    Spinner order_type_spinner, order_status_spinner;
    int y=0;
     SwipeRefreshLayout sw;


      int  page=1, per_page=10;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        date_calender = findViewById(R.id.date_calender);
       filter_order_date = findViewById(R.id.filter_order_date);
       filter_user_date = findViewById(R.id.filter_user_date);


       filter_user_date.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(!date_selected.equals(""))
               {
                   filter_by_date("user_date",date_selected,page,per_page);
               } else {

                   Toast.makeText(MainActivity.this, "Please select date.", Toast.LENGTH_SHORT).show();
               }


           }
       });

       filter_order_date.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(!date_selected.equals(""))
               {
               filter_by_date("order_date",date_selected,page,per_page);
               } else {

                   Toast.makeText(MainActivity.this, "Please select date.", Toast.LENGTH_SHORT).show();
               }

           }
       });

        filter = findViewById(R.id.filter);

        order_status_spinner = findViewById(R.id.spinner_order_status);
        order_type_spinner = findViewById(R.id.spinner_order_type);





        ArrayAdapter status
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                order_status_spinner_data);

        ArrayAdapter type
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
               order_type_spinner_data);

        // set simple layout resource file
        // for each item of spinner
        status.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        type.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        order_status_spinner.setAdapter(status);
        order_type_spinner.setAdapter(type);

        order_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                order_type = order_type_spinner_data[i];


                if(y>=1) {
                    get_all_order_request(order_status,order_type,date_selected);
                    Toast.makeText(MainActivity.this, order_type, Toast.LENGTH_SHORT).show();
                }
                y++;


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        order_status_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                order_status = order_status_spinner_data[i];
                Toast.makeText(MainActivity.this, order_status, Toast.LENGTH_SHORT).show();

                get_all_order_request(order_status,order_type,date_selected);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Settings.class));
            }
        });

sw = findViewById(R.id.refreshLayout);

sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
    @Override
    public void onRefresh() {
        get_all_order_request(order_status,order_type,date_selected);
    }
});



        date_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             _DateDialog(date_calender);

            }
        });

        recyclerview1 = findViewById(R.id.recyclerview1);
        get_orders = new RequestNetwork(this);
        _get_orders_api_listener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                sw.setRefreshing(false);
                try {

                    if(response.contains("200"))
                    {
                        HashMap<String , Objects> map;
                        map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>(){}.getType());
                        String values = (new Gson()).toJson(map.get("user"), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
                        listmap2 = new Gson().fromJson(values, new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());

                       // Collections.reverse(listmap2);


                    } else {

                        listmap2.clear();
                        Toast.makeText(MainActivity.this, "No data found", Toast.LENGTH_LONG).show();
                    }

                    recyclerview1.setAdapter(new Recyclerview1Adapter(listmap2));
                    recyclerview1.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerview1.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

                }catch (Exception e)
                {
                    sw.setRefreshing(false);

                    Toast.makeText(MainActivity.this, "Error on API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(String tag, String message) {
                sw.setRefreshing(false);
            }
        };


        filter_date_api = new RequestNetwork(this);

        update_status_api = new RequestNetwork(this);
        _update_status_api_listener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                if(response.contains("200"))
                {
                    Toast.makeText(MainActivity.this, "Order Status Updated", Toast.LENGTH_SHORT).show();
                    get_all_order_request(order_status,order_type,date_selected);
                }
            }

            @Override
            public void onErrorResponse(String tag, String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        };


    }




    public void _DateDialog (TextView _textview) {
        DatePickerDialog.OnDateSetListener datePickerListener = (view, year, month, day) -> {

            month++;

            String final_date ="";
            if(month<10)
            {
                String new_month="0"+month;

                if(day<10)
                {
                    String new_day="0"+day;
                    final_date = year + "-" + new_month + "-" + new_day;

                } else {

                    final_date = year + "-" + new_month + "-" + day;
                }


            } else {

                if(day<10)
                {
                    String new_day="0"+day;
                    final_date = year + "-" + month + "-" + new_day;

                } else {

                    final_date = year + "-" + month + "-" + day;
                }
            }



            _textview.setText(final_date);
            date_selected = final_date;
            Toast.makeText(this, "Selected", Toast.LENGTH_SHORT).show();

            // sd = day;
        };
        showDatePicker(datePickerListener);
    }

    public void showDatePicker(DatePickerDialog.OnDateSetListener listener) {

        /*Calendar c = Calendar.getInstance();
        c.set(nowyear-21, nowmonth,nowday); */



        // this will provide the 2001 =  now year(2022) - 21 years
        // it will change by time automatic


        DatePickerDialog datePickerDialog= new DatePickerDialog(MainActivity.this);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        datePickerDialog.setOnDateSetListener(listener);
        datePickerDialog.show();

       /*   DatePickerDialog datePicker = new DatePickerDialog(context);
        // datePicker.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(0));
        datePicker.setOnDateSetListener(listener);
        datePicker.show();*/
    }




    private void get_all_order_request(String _order_status, String _order_type, String _date) {
        Toast.makeText(this, "Loading..", Toast.LENGTH_SHORT).show();
        sw.setRefreshing(true);
        get_orders.startRequestNetwork(RequestNetworkController.GET,
                "https://cityneedzapi.000webhostapp.com/chicken-api/getAllBooking_of_all_user.php?" +
                        "order_status=" + _order_status.toLowerCase().trim() +
                        "&order_type="  +  _order_type.toUpperCase().trim() +
                        "&date="+_date,
                "no tag", _get_orders_api_listener);
    }

    private void filter_by_date(String _filter_type, String _date, int _page, int _per_page) {
        Toast.makeText(this, "Filtering..", Toast.LENGTH_SHORT).show();
        sw.setRefreshing(true);
        filter_date_api.startRequestNetwork(RequestNetworkController.GET,
                "https://cityneedzapi.000webhostapp.com/chicken-api/Datefilter.php?" +
                        "filter_type=" +  _filter_type.trim()+
                        "&date="  +  _date.trim() +
                        "&page="+ _page +
                        "&per_page="+_per_page,
                "no tag", _get_orders_api_listener);
    }


/*
    protected function date_filter_fun($filter_type, $date, $page, $per_page){

        try {


	*/
/*	$page_no = 1;

    $total_records_per_page = 10;
    $offset = ($page_no-1) * $total_records_per_page;
	$previous_page = $page_no - 1;
	$next_page = $page_no + 1;

	https://myapi.com/data?page=2&per_page=10

	*//*


            // Set default values for pagination parameters
            $page = isset($page) ? $page : 1;
            $perPage = isset($per_page) ? $per_page : 10;

// Calculate the offset
            $offset = ($page - 1) * $perPage;

            switch ($filter_type) {
                case "user_date":

                    $fetch_result = "SELECT * FROM `cnz_bookservice` WHERE `booked_user_date` = '$date' ORDER BY bookservice_date DESC LIMIT $per_page OFFSET $offset";

                    break;
                case "order_date":
                    $fetch_result = "SELECT * FROM `cnz_bookservice` WHERE `bookservice_date` = '$date' ORDER BY bookservice_date DESC LIMIT $per_page OFFSET $offset";

                    break;

                default:
                    echo "Please select order type..";
            }
?>
*/


    private void update_status_request(String _user_id, String _value) {


        update_status_api.startRequestNetwork(RequestNetworkController.GET,
                "https://cityneedzapi.000webhostapp.com/chicken-api/updateStatus.php?user_id="+_user_id+"&value="+_value,
                "no tag", _update_status_api_listener);

        //THIS IS DEMO UPDATE
//
    }


    public class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;
        public Recyclerview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.custom_layout, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;




            final TextView order_type = _view.findViewById(R.id.order_type);
            final TextView order_no = _view.findViewById(R.id.order_no);
            final TextView customer_date_selected = _view.findViewById(R.id.date);
            final TextView order_date = _view.findViewById(R.id.order_date);
            final TextView order_name_qty = _view.findViewById(R.id.name_qty);
            final TextView amount = _view.findViewById(R.id.amount);

            final TextView ready = _view.findViewById(R.id.ready);
            final TextView delivered = _view.findViewById(R.id.delivered);
            final TextView info = _view.findViewById(R.id.info);




            try{

                amount.setText(Objects.requireNonNull(listmap2.get(_position).get("bookservice_defaultdescription")).toString());
                order_no.setText("#"+Objects.requireNonNull(listmap2.get(_position).get("booked_order_id")));
                order_type.setText("Order Type - "+Objects.requireNonNull(listmap2.get(_position).get("bookservice_order_type")).toString().toUpperCase());
if(Objects.requireNonNull(listmap2.get(_position).get("bookservice_order_type")).toString().equals("HOME"))
{
    order_type.setBackgroundColor(0xFFf54242);
} else
{
    order_type.setBackgroundColor(0xFFE49622);
}

                if(Objects.requireNonNull(listmap2.get(_position).get("bookservice_order_type")).toString().equals("HOME")) {
                    info.setVisibility(View.VISIBLE);
                } else {
                    info.setVisibility(View.GONE);
                }

                if(Objects.requireNonNull(listmap2.get(_position).get("book_status")).toString().equals("process")) {
                    delivered.setVisibility(View.GONE);
                    ready.setVisibility(View.VISIBLE);
                } else if(Objects.requireNonNull(listmap2.get(_position).get("book_status")).toString().equals("ready")) {
                    ready.setVisibility(View.GONE);
                    delivered.setVisibility(View.VISIBLE);
                } else
                {
                    ready.setVisibility(View.GONE);
                    delivered.setVisibility(View.VISIBLE);
                    delivered.setText("Order Delivered");
                    delivered.setBackgroundColor(0xFF0ca823);
                    delivered.setEnabled(false);

                }



               String date = Objects.requireNonNull(listmap2.get(_position).get("booked_user_date")).toString();
               String time =  Objects.requireNonNull(listmap2.get(_position).get("booked_user_time")).toString();

                customer_date_selected.setText("User Selected : "+date +" "+time);

                order_date.setText("Ordered Date : "+Objects.requireNonNull(listmap2.get(_position).get("bookservice_date")).toString());


                String order_name = Objects.requireNonNull(listmap2.get(_position).get("bookservice_categoryname")).toString();
                String order_qty = Objects.requireNonNull(listmap2.get(_position).get("bookservice_qty")).toString();
                order_name_qty.setText(order_name+": "+order_qty);


                String order_details =
                        "\nBook Status : "+Objects.requireNonNull(listmap2.get(_position).get("book_status")).toString() +
                                "\n\n Order by "+Objects.requireNonNull(listmap2.get(_position).get("bookservice_contactperson")).toString()+
                                        "\n Phone no : "+Objects.requireNonNull(listmap2.get(_position).get("bookservice_mobileno")).toString()+
                                        "\n Location :"+Objects.requireNonNull(listmap2.get(_position).get("bookservice_location")).toString()+
                                        "\nPincode : "+Objects.requireNonNull(listmap2.get(_position).get("bookservice_areapincode")).toString()+
                                        "\n\n Payment mode : "+Objects.requireNonNull(listmap2.get(_position).get("bookservice_paymentmode")).toString()+
                                        "\n\n Email id : "+Objects.requireNonNull(listmap2.get(_position).get("bookservice_emailid")).toString();

                ready.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        update_status_request(Objects.requireNonNull(listmap2.get(_position).get("id")).toString(),"ready");
                    }
                });

                delivered.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        update_status_request(Objects.requireNonNull(listmap2.get(_position).get("id")).toString(),"delivered");

                    }
                });

              info.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);



                      builder.setTitle("Order Details");
                      builder.setMessage(order_details);


                      // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                      builder.setCancelable(false);

                      // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                      builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {



           });

                      builder.create().show();
                  }
              });

                // api_map3 = new Gson().fromJson(, new TypeToken<HashMap<String, Object>>(){}.getType());


              /*  String img_url = Objects.requireNonNull(api_map3.get("img_url")).toString();

                Glide.with(getApplicationContext())
                        .load(Uri.parse(img_url))
                        .error(R.drawable.school2)
                        .placeholder(R.drawable.school2)
                        .thumbnail(0.01f)
                        .into(imageview2);*/


				/*ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("Copied Text", img_url );
				clipboard.setPrimaryClip(clip);
				Log.d("img_obj", img_url);*/



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




}