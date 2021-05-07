package com.example.nearbyrestaurants;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.example.nearbyrestaurants.PlaceModels.PlaceDataList;
import com.example.nearbyrestaurants.PlaceModels.ListImplementation;


class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final PlaceDataList restaurants;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public RecyclerViewAdapter() {

        restaurants = ListImplementation.sharedInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list, parent, false);
        final ViewHolder holder = new ViewHolder(view, mListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            String rating = String.valueOf(restaurants.getPlaces().get(position).getRating());
            holder.RestaurantName.setText(restaurants.getPlaces().get(position).getName());
            String distance = String.format("%.2f", restaurants.getPlaces().get(position).getDistance());
            holder.RestaurantRating.setText("Rating: " + rating + "/5");
            holder.RestaurantDistance.setText("Distance: " + distance + " miles");
            int price = restaurants.getPlaces().get(position).getPrice();
            if (price == 0) {
                holder.RestaurantPrice.setText("Price level: N/A");
            } else if (price == 1) {
                holder.RestaurantPrice.setText("Price level: Inexpensive");
            } else if (price == 2) {
                holder.RestaurantPrice.setText("Price level: Moderate");
            } else if (price == 3) {
                holder.RestaurantPrice.setText("Price level: Expensive");
            } else {
                holder.RestaurantPrice.setText("Price level: Very Expensive");
            }


        } catch (NullPointerException e) {
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView RestaurantName;
        TextView RestaurantRating;
        TextView RestaurantDistance;
        TextView RestaurantPrice;


        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            RestaurantName = (TextView) itemView.findViewById(R.id.restaurantName);
            RestaurantRating = (TextView) itemView.findViewById(R.id.restaurant_rating);
            RestaurantDistance = (TextView) itemView.findViewById(R.id.restaurant_distance);
            RestaurantPrice = (TextView) itemView.findViewById(R.id.restaurant_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return restaurants.getPlaces().size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}