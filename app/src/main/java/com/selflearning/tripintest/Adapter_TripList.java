package com.selflearning.tripintest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tanmay.Talekar on 22-12-2016.
 */

public class Adapter_TripList extends RecyclerView.Adapter<Adapter_TripList.MyViewHolder> {

    private List<Pojo_TripDetails> myList;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlStart;
        private RelativeLayout rlDestination;
        private TextView txtStartName;
        private TextView txtStartAddress;
        private TextView txtStartContactName;
        private TextView txtStartContactNo;
        private RelativeLayout rlStops;
        private TextView txtStopsName;
        private TextView txtStopsAddress;
        private TextView txtDestinationName;
        private TextView txtDestinationAddress;

        ImageView ivStart;
        ImageView ivStart_Blur;
        ImageView ivStops;
        ImageView ivStop_Blur;
        ImageView ivDestination;
        ImageView ivDestination_Blur;

        public MyViewHolder(View view) {
            super(view);
            rlStart = (RelativeLayout) view.findViewById(R.id.rlStart);
            rlDestination = (RelativeLayout) view.findViewById(R.id.rlDestination);
            txtStartName = (TextView) view.findViewById(R.id.txtStartName);
            txtStartAddress = (TextView) view.findViewById(R.id.txtStartAddress);
            txtStartContactName = (TextView) view.findViewById(R.id.txtStartContactName);
            txtStartContactNo = (TextView) view.findViewById(R.id.txtStartContactNo);
            rlStops = (RelativeLayout) view.findViewById(R.id.rlStops);
            txtStopsName = (TextView) view.findViewById(R.id.txtStopsName);
            txtStopsAddress = (TextView) view.findViewById(R.id.txtStopsAddress);
            txtDestinationName = (TextView) view.findViewById(R.id.txtDestinationName);
            txtDestinationAddress = (TextView) view.findViewById(R.id.txtDestinationAddress);

            ivStart = (ImageView) view.findViewById(R.id.ivStart);
            ivStart_Blur = (ImageView) view.findViewById(R.id.ivStart_Blur);
            ivStops = (ImageView) view.findViewById(R.id.ivStops);
            ivStop_Blur = (ImageView) view.findViewById(R.id.ivStop_Blur);
            ivDestination = (ImageView) view.findViewById(R.id.ivDestination);
            ivDestination_Blur = (ImageView) view.findViewById(R.id.ivDestination_Blur);
        }
    }


    public Adapter_TripList(List<Pojo_TripDetails> moviesList, Context context) {
        this.myList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.holder_trip_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Pojo_TripDetails pojo_tripDetails = myList.get(position);

        if (pojo_tripDetails.is_Start) {
            holder.rlStart.setVisibility(View.VISIBLE);

            holder.txtStartName.setText(pojo_tripDetails.getName());
            holder.txtStartAddress.setText(pojo_tripDetails.getAddress());
            holder.txtStartContactName.setText(pojo_tripDetails.getContactName());
            holder.txtStartContactNo.setText(pojo_tripDetails.getContactNo());

            holder.rlDestination.setVisibility(View.GONE);
            holder.rlStops.setVisibility(View.GONE);
            holder.ivStart_Blur.setVisibility(View.GONE);
            if (pojo_tripDetails.getIs_visited()) {
                holder.ivStart.setImageResource(R.drawable.ic_check_green);
                setBackground(holder.ivStart, R.drawable.ic_white_circle);
            } else if (pojo_tripDetails.getIs_visiting()) {
                setBackground(holder.ivStart, R.drawable.ic_white_circle);
                holder.ivStart_Blur.setVisibility(View.VISIBLE);
            } else {
                setBackground(holder.ivStart, R.drawable.ic_white_ring);
            }
        } else if (pojo_tripDetails.is_Stops) {
            holder.rlStart.setVisibility(View.GONE);
            holder.rlDestination.setVisibility(View.GONE);
            holder.rlStops.setVisibility(View.VISIBLE);

            holder.txtStopsName.setText(pojo_tripDetails.getName());
            holder.txtStopsAddress.setText(pojo_tripDetails.getAddress());
            holder.ivStop_Blur.setVisibility(View.GONE);
            if (pojo_tripDetails.getIs_visited()) {
                holder.ivStops.setImageResource(R.drawable.ic_check_green);
                setBackground(holder.ivStops, R.drawable.ic_white_circle);
            } else if (pojo_tripDetails.getIs_visiting()) {
                setBackground(holder.ivStops, R.drawable.ic_white_circle);
                holder.ivStop_Blur.setVisibility(View.VISIBLE);
            } else {
                setBackground(holder.ivStops, R.drawable.ic_white_ring);
            }
        } else if (pojo_tripDetails.is_Destination) {
            holder.rlStart.setVisibility(View.GONE);
            holder.rlDestination.setVisibility(View.VISIBLE);
            holder.rlStops.setVisibility(View.GONE);
            holder.ivDestination_Blur.setVisibility(View.GONE);

            holder.txtDestinationName.setText(pojo_tripDetails.getName());
            holder.txtDestinationAddress.setText(pojo_tripDetails.getAddress());

            if (pojo_tripDetails.getIs_visited()) {
                holder.ivDestination.setImageResource(R.drawable.ic_check_green);
                setBackground(holder.ivDestination, R.drawable.ic_white_circle);
            } else if (pojo_tripDetails.getIs_visiting()) {
                setBackground(holder.ivDestination, R.drawable.ic_white_circle);
                holder.ivDestination_Blur.setVisibility(View.VISIBLE);
            } else {
                setBackground(holder.ivDestination, R.drawable.ic_white_ring);
            }
        }
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    private void setBackground(ImageView imageView, int drawable) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackgroundDrawable(context.getDrawable(drawable));
        } else {
            imageView.setBackground(context.getDrawable(drawable));
        }
    }
}
