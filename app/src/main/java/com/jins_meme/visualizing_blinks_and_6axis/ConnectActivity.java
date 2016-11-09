package com.jins_meme.visualizing_blinks_and_6axis;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jins_jp.meme.MemeConnectListener;
import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeScanListener;
import com.jins_jp.meme.MemeStatus;

import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends AppCompatActivity {

    private int REQUEST_CODE_ACCESS_COARSE_LOCATION = 0x01;
    private boolean locationPermissionGranted = false;

    private MemeLib memeLib;
    private List<String> scannedAddresses;
    private ArrayAdapter<String> scannedAddressAdapter;
    private boolean scanning = false;

    private ListView deviceListView;
    private ProgressDialog progressDialog;

    final private MemeConnectListener memeConnectListener = new MemeConnectListener() {
        @Override
        public void memeConnectCallback(boolean b) {
            //describe actions after connection with JINS MEME
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.hide();
                }
            });
            finish();
        }

        @Override
        public void memeDisconnectCallback() {
            //describe actions after disconnection from JINS MEME
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidateOptionsMenu();
                    Toast.makeText(ConnectActivity.this, "DISCONNECTED", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        if (savedInstanceState == null) {
            init();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Sets MemeConnectListener to get connection result.
        memeLib.setMemeConnectListener(memeConnectListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (memeLib.isConnected()) {
            menu.findItem(R.id.action_scan).setVisible(false);
            menu.findItem(R.id.action_stop).setVisible(false);
        } else if (scanning) {
            menu.findItem(R.id.action_scan).setVisible(false);
            menu.findItem(R.id.action_stop).setVisible(true);
        } else {
            menu.findItem(R.id.action_scan).setVisible(true);
            menu.findItem(R.id.action_stop).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_scan) {
            startScan();
            return true;
        } else if (id == R.id.action_stop) {
            stopScan();
            clearList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        memeLib = MemeLib.getInstance(); //MemeLib is singleton
        memeLib.setVerbose(true); // for debug

        deviceListView = (ListView) findViewById(R.id.device_list_view);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                progressDialog = new ProgressDialog(ConnectActivity.this);
                progressDialog.setMessage("Connecting...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                stopScan();
                memeLib.connect(scannedAddresses.get(i));
                clearList();
            }
        });

        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showPermissionError();
            } else {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startScan() {

        if (!locationPermissionGranted) {
            invalidateOptionsMenu();
            showPermissionError();
            return;
        }

        scanning = true;
        scannedAddresses = new ArrayList<>();
        scannedAddressAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scannedAddresses);
        deviceListView.setAdapter(scannedAddressAdapter);

        invalidateOptionsMenu();

        //Sets MemeConnectListener to get connection result.
        memeLib.setMemeConnectListener(memeConnectListener);

        //starts scanning JINS MEME
        MemeStatus status = memeLib.startScan(new MemeScanListener() {
            @Override
            public void memeFoundCallback(String address) {
                scannedAddresses.add(address);

                runOnUiThread(new Runnable() {
                    public void run() {
                        scannedAddressAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        if (status == MemeStatus.MEME_ERROR_APP_AUTH) {
            Toast.makeText(this, "App Auth Failed", Toast.LENGTH_LONG).show();
        }
    }

    private void stopScan() {
        scanning = false;
        invalidateOptionsMenu();

        //stop scanning JINS MEME
        if (memeLib.isScanning())
            memeLib.stopScan();
    }

    private void clearList() {
        scannedAddressAdapter.clear();
        deviceListView.deferNotifyDataSetChanged();
    }

    private void checkPermissions() {
        // for Android 6
        // Check ACCESS_COARSE_LOCATION permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 「Never ask again」Checkbox
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Keep preference for the first time false return
                SharedPreferences preferences = this.getSharedPreferences("LIVE_VIEW", Context.MODE_PRIVATE);
                if (preferences.getBoolean("FIRST_LOCATION_PERMISSION_ACCESS", true)) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("FIRST_LOCATION_PERMISSION_ACCESS", false);
                    editor.apply();
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ACCESS_COARSE_LOCATION);
                } else {
                    showPermissionError();
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        } else {
            locationPermissionGranted = true;
        }

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.bluetooth_off)
                    .setPositiveButton(android.R.string.yes, null)
                    .show();
        }

    }

    private void showPermissionError() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.location_required)
                .setPositiveButton(android.R.string.yes, null)
                .show();
    }
}
