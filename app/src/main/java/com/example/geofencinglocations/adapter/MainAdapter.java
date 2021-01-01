package com.example.geofencinglocations.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.masterandroid.backgroundservice.Api;
import com.masterandroid.backgroundservice.R;
import com.masterandroid.backgroundservice.RequestHandler;
import com.masterandroid.backgroundservice.place;
import com.masterandroid.backgroundservice.showPlaceNearby;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private List<place> historyList;
    private Activity activity;

    public MainAdapter(List<place> historyList, Activity activity) {
        this.historyList = historyList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        place data=historyList.get(position);
        holder.name.setText(data.getPlaceName());
        holder.address.setText(data.getPlaceAddress());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date d = sdf.parse(data.getPlaceTime());
            holder.time.setText(d.toString());
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }
        if(data.getPlaceType()==null)
        {

        }else
            {
                holder.type.setText(data.getPlaceType().toString());
            }

        holder.yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Toast.makeText(activity, "Yes for "+data.getPlaceName(), Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, "place id "+data.getPlaceId(), Toast.LENGTH_SHORT).show();
                updateStatus(Integer.toString(data.getPlaceId()),"Yes");
              //  notifyDataSetChanged();
            }
        });

        holder.noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "No for "+data.getPlaceName(), Toast.LENGTH_SHORT).show();
                holder.editBtn.setVisibility(View.VISIBLE);
                holder.noBtn.setVisibility(View.GONE);
            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "Nearby for "+data.getPlaceName(), Toast.LENGTH_SHORT).show();
                holder.editBtn.setVisibility(View.GONE);
                holder.noBtn.setVisibility(View.VISIBLE);
                Intent showNearby=new Intent(activity, showPlaceNearby.class);
                showNearby.putExtra("placeLat",data.getPlaceLatitude());
                showNearby.putExtra("placeLon",data.getPlaceLongitude());
                showNearby.putExtra("placeAddr",data.getPlaceAddress());
                showNearby.putExtra("placeId",data.getPlaceId());
                activity.startActivity(showNearby);
            }
        });
    }
    private void updateStatus(String placeid,String status)
    {

        HashMap<String, String> params = new HashMap<>();
        params.put("placeId", placeid);
        params.put("visitStatus", status);

       PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_LIST, params, CODE_POST_REQUEST);
       request.execute();
    }
    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView name,address,type,time;
        Button yesBtn,noBtn,editBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            address=itemView.findViewById(R.id.address);
            type=itemView.findViewById(R.id.type);
            time=itemView.findViewById(R.id.time);
            yesBtn=itemView.findViewById(R.id.yesBtn);
            noBtn=itemView.findViewById(R.id.noBtn);
            editBtn=itemView.findViewById(R.id.editBtn);
        }
    }
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;
        //the parameters
        HashMap<String, String> params;
        //the request code to define whether it is a GET or POST
        int requestCode;

        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        //when the task started displaying a progressbar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        //this method will give the response from the request
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(activity, object.getString("message"), Toast.LENGTH_SHORT).show();
                    //refreshing the herolist after every operation
                    //so we get an updated list
                    //we will create this method right now it is commented
                    //because we haven't created it yet
                   // refreshHistoryList(object.getJSONArray("lists"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
}
