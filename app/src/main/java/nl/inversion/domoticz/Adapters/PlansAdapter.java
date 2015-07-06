package nl.inversion.domoticz.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.PlanInfo;
import nl.inversion.domoticz.R;

@SuppressWarnings("unused")
public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.DataObjectHolder> {
    private final Context mContext;
    private ArrayList<PlanInfo> mDataset;
    private static onClickListener onClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
                                         implements View.OnClickListener {
        TextView name;
        TextView devices;

        public DataObjectHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            devices = (TextView) itemView.findViewById(R.id.devices);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onItemClick(getLayoutPosition(), v);
        }
    }

    public void setOnItemClickListener(onClickListener onClickListener) {
        PlansAdapter.onClickListener = onClickListener;
    }

    public PlansAdapter(ArrayList<PlanInfo> data, Context mContext) {
        this.mDataset = data;
        this.mContext = mContext;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plan_row, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        if (mDataset != null && mDataset.size() > 0) {
            String name = mDataset.get(position).getName();
            int numberOfDevices = mDataset.get(position).getDevices();
            String text = mContext.getResources().getQuantityString(R.plurals.devices, numberOfDevices, numberOfDevices);

            holder.name.setText(name);
            holder.devices.setText(String.valueOf(text));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface onClickListener {
        void onItemClick(int position, View v);
    }
}