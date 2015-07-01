package nl.inversion.domoticz.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import nl.inversion.domoticz.Adapters.SceneAdapter;
import nl.inversion.domoticz.Containers.SceneInfo;
import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.DomoticzFragmentListener;
import nl.inversion.domoticz.Interfaces.ScenesClickListener;
import nl.inversion.domoticz.Interfaces.ScenesReceiver;
import nl.inversion.domoticz.Interfaces.setCommandReceiver;
import nl.inversion.domoticz.R;
import nl.inversion.domoticz.UI.sceneInfoDialog;
import nl.inversion.domoticz.app.DomoticzFragment;

public class Scenes extends DomoticzFragment implements DomoticzFragmentListener,
        ScenesClickListener {

    private static final String TAG = Scenes.class.getSimpleName();
    private ProgressDialog progressDialog;
    private Activity mActivity;
    private Domoticz mDomoticz;

    @Override
    public void onConnectionOk() {
        showProgressDialog();

        mDomoticz = new Domoticz(mActivity);
        mDomoticz.getScenes(new ScenesReceiver() {

            @Override
            public void onReceiveScenes(ArrayList<SceneInfo> scenes) {
                successHandling(scenes.toString(), false);
                createListView(scenes);
            }

            @Override
            public void onError(Exception error) {
                errorHandling(error);
            }
        });
    }

    public void createListView(final ArrayList<SceneInfo> scenes) {

        final ScenesClickListener listener = this;

        SceneAdapter adapter = new SceneAdapter(mActivity, scenes, listener);
        ListView listView = (ListView) getView().findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,
                                           int index, long id) {
                showInfoDialog(scenes.get(index));
                return true;
            }
        });

        hideProgressDialog();
    }

    private void showInfoDialog(final SceneInfo mSceneInfo) {

        sceneInfoDialog infoDialog = new sceneInfoDialog(
                getActivity(),
                mSceneInfo,
                R.layout.dialog_scene_info);
        infoDialog.setIdx(String.valueOf(mSceneInfo.getIdx()));
        infoDialog.setLastUpdate(mSceneInfo.getLastUpdate());
        infoDialog.setIsFavorite(mSceneInfo.getFavoriteBoolean());
        infoDialog.show();
        infoDialog.onDismissListener(new sceneInfoDialog.DismissListener() {

            @Override
            public void onDismiss(boolean isChanged, boolean isFavorite) {
                if (isChanged) changeFavorite(mSceneInfo, isFavorite);
            }
        });
    }

    private void changeFavorite(final SceneInfo mSceneInfo, final boolean isFavorite) {
        addDebugText("changeFavorite");
        addDebugText("Set idx " + mSceneInfo.getIdx() + " favorite to " + isFavorite);

        int jsonAction;
        int jsonUrl = Domoticz.Json.Url.Set.FAVORITE;

        if (isFavorite) jsonAction = Domoticz.Device.Favorite.ON;
        else jsonAction = Domoticz.Device.Favorite.OFF;

        mDomoticz.setAction(mSceneInfo.getIdx(), jsonUrl, jsonAction, 0, new setCommandReceiver() {
            @Override
            public void onReceiveResult(String result) {
                successHandling(result, false);
                mSceneInfo.setFavoriteBoolean(isFavorite);
            }

            @Override
            public void onError(Exception error) {
                // Domoticz always gives an error: ignore
                errorHandling(error);
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        getActionBar().setTitle(R.string.title_scenes);
    }

    @Override
    public void onSceneClick(int idx, boolean action) {
        addDebugText("onSceneClick");
        addDebugText("Set " + idx + " to " + action);

        int jsonAction;
        int jsonUrl = Domoticz.Json.Url.Set.SCENES;

        if (action) jsonAction = Domoticz.Scene.Action.ON;
        else jsonAction = Domoticz.Scene.Action.OFF;

        mDomoticz.setAction(idx, jsonUrl, jsonAction, 0, new setCommandReceiver() {
            @Override
            public void onReceiveResult(String result) {
                successHandling(result, true);
            }

            @Override
            public void onError(Exception error) {
                errorHandling(error);
            }
        });
    }

    /**
     * Initializes the progress dialog
     */
    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage(getString(R.string.msg_please_wait));
        progressDialog.setCancelable(false);
    }

    /**
     * Shows the progress dialog if isn't already showing
     */
    private void showProgressDialog() {
        if (progressDialog == null) initProgressDialog();
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    /**
     * Hides the progress dialog if it is showing
     */
    private void hideProgressDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void errorHandling(Exception error) {
        super.errorHandling(error);
        hideProgressDialog();
    }
}