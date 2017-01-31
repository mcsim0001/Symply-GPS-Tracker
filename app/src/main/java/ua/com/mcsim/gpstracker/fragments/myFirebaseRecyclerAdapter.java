package ua.com.mcsim.gpstracker.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ua.com.mcsim.gpstracker.R;


/**
 * Created by mcsim on 23.01.2017.
 */

public class MyFirebaseRecyclerAdapter extends RecyclerView.Adapter <MyFirebaseRecyclerAdapter.ViewHolder> {

    public MyFirebaseRecyclerAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            textView = (TextView) v.findViewById(R.id.tv_target_name);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
