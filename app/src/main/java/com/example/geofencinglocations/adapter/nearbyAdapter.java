package com.example.geofencinglocations.adapter;

import android.app.Activity;
import android.os.AsyncTask;
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
import com.masterandroid.backgroundservice.nearbyPlace;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class nearbyAdapter  extends RecyclerView.Adapter<nearbyAdapter.ViewHolder> {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private List<nearbyPlace> nearbyList;
    private Activity activity;

    public nearbyAdapter(List<nearbyPlace> nearbyList, Activity activity) {
        this.nearbyList = nearbyList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        nearbyPlace data=nearbyList.get(position);
        holder.name.setText(data.getPlaceName());
        holder.latLng.setText(data.getTypes().toString());

        holder.yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "You have visited.", Toast.LENGTH_SHORT).show();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String time=formatter.format(date).toString();
                storeData(
                        "GuzFS0EjtBSwuRXBuRfhFN8ZSfm1",
                        data.getPlaceName(),
                        data.getMainPlaceAddress(),
                        data.getTypes().toString(),
                        data.getPlaceLatitude(),
                        data.getPlaceLongitude(),
                        "yes",
                        time
                );

            }
        });

    }

    @Override
    public int getItemCount() {
        return nearbyList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView name,latLng;
        Button yesBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            latLng=itemView.findViewById(R.id.latlng);
            yesBtn=itemView.findViewById(R.id.yesBtn);
        }
    }
    public void storeData(String UserId, String name, String address,String type,
                          double latitude, double longitude, String VisitStatus,String time
    )
    {

        HashMap<String, String> params = new HashMap<>();

        params.put("userId",UserId);
        params.put("placeLatitude",String.valueOf(latitude));
        params.put("placeLongitude",String.valueOf(longitude));
        params.put("placeAddress",address);
        params.put("placeName",name);
        params.put("placeType",type);
        params.put("visitStatus",VisitStatus);
        params.put("placeTime",time);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_LIST, params, CODE_POST_REQUEST);
        request.execute();

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
