package codeforgood.beacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.bluetooth.le.ScanSettings.SCAN_MODE_BALANCED;
import static android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY;

public class MainActivity extends ActionBarActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;

    private boolean mScanning;

    private static final int RQS_ENABLE_BLUETOOTH = 1;

    private TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });

        // Check if BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this,
                    "BLUETOOTH_LE not supported in this device!",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        getBluetoothAdapterAndLeScanner();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,
                    "bluetoothManager.getAdapter()==null",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        Button locationBtn = (Button) findViewById(R.id.locBtn);
        Button travelBtn = (Button) findViewById(R.id.travBtn);
        Button lavatoryBtn = (Button) findViewById(R.id.lavBtn);
        Button receptionBtn = (Button) findViewById(R.id.recepBtn);
        Button emServBtn = (Button) findViewById(R.id.emServBtn);
        Button lostBtn = (Button) findViewById(R.id.lostBtn);

        locationBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String toSpeak = "Head at 0 for 10 steps"; //YOUSEF INPUT HERE
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                //System.out.println("LOC:Hello World");
            }
        });

        travelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toSpeak = "Travel to:"; //YOUSEF INPUT HERE
                // DIALOG POPS UP
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                // Tell user what to do!
            }
        });

        lavatoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toSpeak = "The nearest lavatory is: "; //YOUSEF INPUT HERE, +GenderSpecific
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                // Tell user what to do!
            }
        });

        receptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String toSpeak = "To head to reception, walk 15 paces forward"; //YOUSEF INPUT HERE
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                // Tell user what to do!

                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

                mScanning = false;
                Log.i("Get Scanner", "Got scanner");
                List<ScanFilter> filters = new ArrayList<ScanFilter>();
                int count =1;
                filters.add(new ScanFilter.Builder().setDeviceName("MiniBeacon_05611").build());
                mBluetoothLeScanner.startScan(filters, new ScanSettings.Builder().setScanMode(SCAN_MODE_BALANCED).build(), mScanCallback);

            }

        });

        emServBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toSpeak = "Remain calm, the nearest exit is: "; //YOUSEF INPUT HERE
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                // Tell user what to do!

            }

        });

        lostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toSpeak = "Calling X"; //YOUSEF INPUT HERE
                //DIAL FAMILIAR NUMBER
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                // Tell user what to do!

            }

        });
    }



    @Override
    protected void onResume() {
        super.onResume();

        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, RQS_ENABLE_BLUETOOTH);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RQS_ENABLE_BLUETOOTH && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        getBluetoothAdapterAndLeScanner();

        Log.i("Get Scanner", "Starting Scan");


        Log.i("Get Scanner", "Finishing Scan");

        // Checks if Bluetooth is supported on the device.

        super.onActivityResult(requestCode, resultCode, data);
    }

    private int count;
    //Scan call back function
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            super.onScanResult(callbackType, result);
            int tx = result.getScanRecord().getTxPowerLevel();
            double accuracy = calculateDistance(tx, result.getRssi());
            //Log.i("Overall Record: ", result.getScanRecord().toString());
            //Log.i("Device Name: ", result.getDevice().getName() + " Distance " + accuracy );

            if (count%6==1) {
                if ((accuracy) < 1700) {

                    String toSpeak = "You've reached your Destination"; //YOUSEF INPUT HERE
                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    // Tell user what to do!
                    Log.i("You have reached ", result.getDevice().getName());
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);

                } else if (accuracy > 1700 && accuracy < 5000) {
                    String toSpeak = "You're close to your destination"; //YOUSEF INPUT HERE
                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    Log.i("You are close to ", result.getDevice().getName());
                } else {
                    String toSpeak = "Keep going straight"; //YOUSEF INPUT HERE
                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    Log.i("Keep Going to ", result.getDevice().getName());
                }
            }
            count++;
        }
    };

    public double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }
        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    private String getDistance(double accuracy) {
        if (accuracy == -1.0) {
            return "Unknown";
        } else if (accuracy < 1) {
            return "Immediate";
        } else if (accuracy < 3) {
            return "Near";
        } else {
            return "Far";
        }
    }

    private void getBluetoothAdapterAndLeScanner(){
        Log.i("Get Scanner", "Getting scanner");
        // Get BluetoothAdapter and BluetoothLeScanner.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth not enabled");
            builder.setMessage("Press OK to enable bluetooth");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("Foo", "Enabled");
                    mBluetoothAdapter.enable();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.show();

        }
        /**mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        mScanning = false;
        Log.i("Get Scanner", "Got scanner");
        List<ScanFilter> filters = new ArrayList<ScanFilter>();
        int count =1;
        filters.add(new ScanFilter.Builder().setDeviceName("MiniBeacon_05611").build());
        mBluetoothLeScanner.startScan(filters, new ScanSettings.Builder().setScanMode(SCAN_MODE_BALANCED).build(), mScanCallback);**/
    }
}
