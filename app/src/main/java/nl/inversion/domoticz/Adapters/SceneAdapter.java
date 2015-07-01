package nl.inversion.domoticz.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.inversion.domoticz.Containers.SceneInfo;
import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.ScenesClickListener;
import nl.inversion.domoticz.R;

// Example used: http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
// And: http://www.survivingwithandroid.com/2013/02/android-listview-adapter-checkbox-item_7.html
public class SceneAdapter extends BaseAdapter {

    private static final String TAG = SceneAdapter.class.getSimpleName();

    private final ScenesClickListener listener;
    Context context;
    ArrayList<SceneInfo> data = null;


    public SceneAdapter(Context context,
                        ArrayList<SceneInfo> data,
                        ScenesClickListener listener) {
        super();

        this.context = context;
        Collections.sort(data, new Comparator<SceneInfo>() {
            @Override
            public int compare(SceneInfo left, SceneInfo right) {
                return left.getName().compareTo(right.getName());
            }
        });
        this.data = data;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int layoutResourceId;

        SceneInfo mSceneInfo = data.get(position);

        //if (convertView == null) {
            holder = new ViewHolder();

            if (Domoticz.Scene.Type.SCENE.equalsIgnoreCase(mSceneInfo.getType())) {

                holder.isProtected = mSceneInfo.isProtected();

                layoutResourceId = R.layout.scene_row_scene;
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);

                holder.sceneButton = (Button) convertView.findViewById(R.id.scene_button);
                if (holder.isProtected) holder.sceneButton.setEnabled(false);
                holder.sceneButton.setId(mSceneInfo.getIdx());
                holder.sceneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleClick(view.getId(), true);
                    }
                });

                holder.sceneName = (TextView) convertView.findViewById(R.id.scene_name);
                holder.sceneName.setText(mSceneInfo.getName());

            } else if (mSceneInfo.getType().equalsIgnoreCase(Domoticz.Scene.Type.GROUP)) {

                holder.isProtected = mSceneInfo.isProtected();

                layoutResourceId = R.layout.scene_row_group;
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);

                holder.groupButton = (ToggleButton) convertView.findViewById(R.id.group_button);
                if (holder.isProtected) holder.groupButton.setEnabled(false);
                holder.groupButton.setId(mSceneInfo.getIdx());
                holder.groupButton.setChecked(mSceneInfo.getStatusInBoolean());
                holder.groupButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        handleClick(compoundButton.getId(), checked);
                    }
                });

                holder.groupName = (TextView) convertView.findViewById(R.id.group_name);
                holder.groupName.setText(mSceneInfo.getName());

            } else throw new NullPointerException("Scene type not supported in the adapter for:\n"
                    + mSceneInfo.toString());
            convertView.setTag(holder);

        //} else holder = (ViewHolder) convertView.getTag();

        return convertView;
    }

    public void handleClick(int idx, boolean action) {
            listener.onSceneClick(idx, action);
    }

    static class ViewHolder {
        Boolean isProtected;
        Button sceneButton;
        TextView sceneName;

        ToggleButton groupButton;
        TextView groupName;
    }
}