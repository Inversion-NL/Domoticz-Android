package nl.inversion.domoticz;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Utils.SharedPrefUtil;

public class MainActivity extends ActionBarActivity {

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawer;
    private String mActivityTitle;
    private String[] drawerActions;
    private String[] fragments;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example used: http://blog.teamtreehouse.com/add-navigation-drawer-android
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString(); // Store current title of activity

        addDrawerItems();
        setupDrawer();

        SharedPrefUtil mSharedPres = new SharedPrefUtil(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        int screenIndex = mSharedPres.getStartupScreenIndexValue();

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main, Fragment.instantiate(MainActivity.this, fragments[screenIndex]));
        tx.commit();

    }

    @Override
    public void onResume(){
        super.onResume();
        checkConnectionSettings();
    }

    /**
     * Checks if connection data (username, password, url and port) have data
     */
    private void checkConnectionSettings() {
        Domoticz mDomoticz = new Domoticz(this);

        if (!mDomoticz.isConnectionDataComplete()) {
            Log.d(TAG, "Connection data incomplete, show warning dialog");
            mDomoticz.showConnectionSettingsMissingDialog();
        }
    }

    /**
     * Adds the items to the drawer and registers a click listener on the items
     */
    private void addDrawerItems() {

        drawerActions = getResources().getStringArray(R.array.drawer_actions);
        fragments = getResources().getStringArray(R.array.drawer_fragments);

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, drawerActions);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                    tx.replace(R.id.main, Fragment.instantiate(MainActivity.this, fragments[position]));
                    tx.commit();
                } catch (Exception e) {
                    Log.e(TAG, "Fragment error");
                    e.printStackTrace();
                }
                mDrawer.closeDrawer(mDrawerList);
            }
        });
    }

    /**
     * Sets the drawer with listeners for open and closed
     */
    private void setupDrawer() {

        // final CharSequence currentTitle = getSupportActionBar().getTitle();

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a mDrawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle(R.string.drawer_navigation_title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a mDrawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle(currentTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true); // hamburger menu icon
        mDrawer.setDrawerListener(mDrawerToggle); // attach hamburger menu icon to drawer

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}