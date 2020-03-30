package ru.kharitonov.bluetoothscanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.*;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements BluetoothAdapter.LeScanCallback {

    public static final String TAG = "bt-pairing-test";

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 10;
    private List<BluetoothDevice> listDevices = new ArrayList<>();
    private List<String> listAddress = new ArrayList<>();
    private ScanCallback mScanCallback;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private BluetoothLeScanner mLeScanner;
    private boolean addMaddress = false;
    private List<BluetoothDevice> mTargetDevices = new ArrayList<>();
    private static final Set<String> TARGET_DEVICE_NAME = new HashSet<String>();


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d(TAG, "ACTION_FOUND: " + device.getName() + " address: "+ device.getAddress());
                    if (device.getName() != null) {
                       // Log.d(TAG, "target device found..");
                        mTargetDevices.add(device);
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d(TAG, "FINISHED: " );
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
        startScan();


//        if (Build.VERSION.SDK_INT >= 21) {
//            mScanCallback = new ScanCallback() {
//                @Override
//                public void onScanResult(int callbackType, ScanResult result) {
//                    super.onScanResult(callbackType, result);
//
//                    if (listAddress.isEmpty()) {
//                        listAddress.add(result.getDevice().getAddress());
//                    }
//
//                    else {
//                        for (String address : listAddress) {
//                            if (address.equals(result.getDevice().getAddress())) {
//                                addMaddress = false;
//                                return;
//                            }
//                            else{
//                                addMaddress = true;
//                            }
//                        }
//                        if(addMaddress){
//                            listAddress.add(result.getDevice().getAddress());
//                            addMaddress = true;
//                        }
//                    }
//
//                }
//            };
//        }
//
//        BluetoothManager manager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = manager.getAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        //startScanBLE();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void startScan(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();
    }



    private void startScanBLE(){
        if (mLeScanner == null)
            mLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        mLeScanner.startScan(new ArrayList<ScanFilter>(), new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(), mScanCallback);
    }

    private void bluetoothScan() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (bluetoothAdapter != null) {
            boolean result = bluetoothAdapter.startLeScan(this);
            int fe = 10;
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }

    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (listAddress.isEmpty()) {
                    listAddress.add(device.getAddress());
                }
                else {
                    for (String address : listAddress) {
                        if (!address.equals(device.getAddress())) {
                           addMaddress = true;
                        }
                    }
                    if(addMaddress){
                        listAddress.add(device.getAddress());
                        addMaddress = false;
                    }
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
    }

}

