package com.example.geofencinglocations.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geofencinglocations.R;
import com.example.geofencinglocations.models.place;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VisitedAdapter extends RecyclerView.Adapter<VisitedAdapter.ViewHolder> {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private List<place> historyList;
    private Activity activity;

    public VisitedAdapter(List<place> historyList, Activity activity) {
        this.historyList = historyList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.visited_item,parent,false);
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

    }
    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView name,address,type,time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            address=itemView.findViewById(R.id.address);
            type=itemView.findViewById(R.id.type);
            time=itemView.findViewById(R.id.time);
        }
    }

}
