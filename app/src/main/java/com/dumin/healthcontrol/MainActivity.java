package com.dumin.healthcontrol;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // the ID of the dialog AlertDialog with buttons
    private final int TIME_FRAME_RADIO = 0;
    private SPrefManager appPref;

    private ViewPager viewPager;
    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appPref = new SPrefManager(this);

        // Creates the Toolbar and NavigationView
        onCreateNavigationView();

        // Button add new entries
        onCreateActionButton();

        // Creates Tabs with pages
        onCreateTabLayout();

        Toast.makeText(this, appPref.loadPreferences(appPref.MEASUREMENT), Toast.LENGTH_SHORT).show();
    }

    // Creates the Toolbar and NavigationView
    private void onCreateNavigationView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Button add new entries
    private void onCreateActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_entry_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNewEntry.class);
                intent.putExtra(appPref.APP_PREFERENCES, appPref.loadPreferences(appPref.MEASUREMENT));
                startActivityForResult(intent, 1);
            }
        });
    }

    // Creates Tabs with pages
    private void onCreateTabLayout() {

        // Add pages in fragments
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2); // Limit is all
        setupViewPager(viewPager);

        // for rendering pages in TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }
    // creates pages(fragments) and fills ViewPager
    private void setupViewPager(@NonNull ViewPager viewPager) {

        adapter.addFragment(new EntriesList(), "Data");
        adapter.addFragment(new Graphics(), "Graphics");
        adapter.addFragment(new Statistics(), "Statistics");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.time_frame) {
            showDialog(TIME_FRAME_RADIO);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // Controls the settings of the time interval
    @Override
    protected Dialog onCreateDialog(int id) {
        String timeFrame = appPref.loadPreferences(SPrefManager.TIME_FRAME);
        final int[] checkedItem = {0};
        final String[] mChooseTime = { SPrefManager.WEEK, SPrefManager.MONTH,
                SPrefManager.YEAR, SPrefManager.All_TIME };
        for(int i=0; i<mChooseTime.length; i++){
            if(mChooseTime[i].equals(timeFrame)){ checkedItem[0] = i; }
        }
        switch (id) {
            case TIME_FRAME_RADIO:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder = new AlertDialog.Builder(this);

                builder.setTitle("Select the time interval");

                builder.setCancelable(false);
                        // add one button to close the dialog
                builder.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        appPref.savePreferences(SPrefManager.TIME_FRAME, mChooseTime[checkedItem[0]]);
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "You have chosen: "
                                                        + mChooseTime[checkedItem[0]],
                                                Toast.LENGTH_SHORT).show();
                                        updateFragments();
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                })

                        // add switches
                        .setSingleChoiceItems(mChooseTime, checkedItem[0],
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int item) {
                                        checkedItem[0] = item;
                                    }
                                });
                return builder.create();
            default:
                return null;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch(item.getItemId()){
            case R.id.blood_pressure: {
                appPref.savePreferences(appPref.MEASUREMENT, appPref.BLOOD_PRESSURE);
                break;
            }
            case R.id.glucose: {
                appPref.savePreferences(appPref.MEASUREMENT, appPref.GLUCOSE);
                break;
            }
            case R.id.temperature: {
                appPref.savePreferences(appPref.MEASUREMENT, appPref.TEMPERATURE);
                break;
            }
            case R.id.notification:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
        default: appPref.savePreferences(appPref.MEASUREMENT, appPref.BLOOD_PRESSURE);
        }

        updateFragments();

        TextView viewMs = (TextView) findViewById(R.id.tv_measurement);
        viewMs.setText(appPref.loadPreferences(appPref.MEASUREMENT));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // gets the result from the intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(data == null) {return;}
        if(data.getBooleanExtra("update",false)){
            updateFragments();
        }
    }

    private void updateFragments(){
        // The message for the loader that the data has been changed
        this.getSupportLoaderManager().getLoader(EntriesList.LOADER_ID).forceLoad();
        this.getSupportLoaderManager().getLoader(Graphics.LOADER_ID).forceLoad();
        this.getSupportLoaderManager().getLoader(Statistics.LOADER_ID).forceLoad();
    }

}
