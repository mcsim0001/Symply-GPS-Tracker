package ua.com.mcsim.gpstracker.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import ua.com.mcsim.gpstracker.R;
import ua.com.mcsim.gpstracker.forms.TrackingPermission;

import static ua.com.mcsim.gpstracker.MainActivity.CHILD_FROM;
import static ua.com.mcsim.gpstracker.MainActivity.CHILD_PERMISSIONS;


public class TargetsFragment extends Fragment {

    private RecyclerView targetsRecycler;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private DatabaseReference databaseReference;
    private String myID;


    public static class TargetViewHolder extends RecyclerView.ViewHolder {
        public TextView targetName;
        public TextView targetPhone;
        public TextView trackingStatus;
        public SwitchCompat trackingSwitch;

        public TargetViewHolder(View v) {
            super(v);
            targetName = (TextView) itemView.findViewById(R.id.tv_target_name);
            targetPhone = (TextView) itemView.findViewById(R.id.tv_target_phone);
            trackingStatus = (TextView) itemView.findViewById(R.id.tv_tracking_status);
            trackingSwitch = (SwitchCompat) itemView.findViewById(R.id.switch_tracking);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            myID = bundle.getString("id");
        }

        targetsRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_targets);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<
                TrackingPermission,
                TargetViewHolder>(
                TrackingPermission.class,
                R.layout.target_item,
                TargetViewHolder.class,
                databaseReference.child(CHILD_PERMISSIONS).orderByChild(CHILD_FROM).equalTo(myID)) {

            @Override
            protected void populateViewHolder(TargetViewHolder viewHolder, TrackingPermission model, int position) {
                //User targetUser = (User) databaseReference.child(CHILD_USERS).child(model.getTo())
                viewHolder.targetName.setText("Target Name");
                viewHolder.targetPhone.setText("Target phone");
                viewHolder.trackingStatus.setText("some status");
            }
        };
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                targetsRecycler.scrollToPosition(positionStart);

            }
        });

        //targetsRecycler.setAdapter(firebaseRecyclerAdapter);

        return rootView;

    }


}
