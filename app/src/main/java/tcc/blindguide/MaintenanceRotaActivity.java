package tcc.blindguide;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import tcc.adapters.ItemRotasAdapter;
import tcc.compass.LowPassFilter;
import tcc.dmlibrary.RotaDM;
import tcc.tolibrary.RotaTO;

public class MaintenanceRotaActivity extends Activity implements SensorEventListener, LocationListener {

    private ListView m_LisViewItemRotas;
    private RotaTO m_RotaTO = null;
    private ItemRotasAdapter m_Adapter;
    private TextView m_TextViewAngulo;
    private Integer m_Angulo = 0;



    private static final String TAG = "SensorsActivity";
    private static final AtomicBoolean computing = new AtomicBoolean(false);

    private static final int MIN_TIME = 30 * 1000;
    private static final int MIN_DISTANCE = 10;

    private static final float grav[] = new float[3]; // Gravity (a.k.a
    // accelerometer data)
    private static final float mag[] = new float[3]; // Magnetic
    private static final float rotation[] = new float[9]; // Rotation matrix in
    // Android format
    private static final float orientation[] = new float[3]; // azimuth, pitch,
    // roll
    private static float smoothed[] = new float[3];

    private static SensorManager sensorMgr = null;
    private static List<Sensor> sensors = null;
    private static Sensor sensorGrav = null;
    private static Sensor sensorMag = null;

    private static LocationManager locationMgr = null;
    private static Location currentLocation = null;
    private static GeomagneticField gmf = null;

    private static double floatBearing = 0;
    private EditText m_EditTextOrigem;
    private EditText m_EditTextDestino;

   /* public RotaFragment() {
        m_RotaTO = new RotaTO();
    }

    public RotaFragment(RotaTO rotaTO)
    {
        m_RotaTO = rotaTO;
    } */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_rota);

        long _id = getIntent().getLongExtra("id", 0);

        // Set up action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Inflate the layout for this fragment
        if (_id > 0)
        {
            m_RotaTO = new RotaDM(MaintenanceRotaActivity.this).BuscaRotaById(_id);
            actionBar.setTitle("Rota - Alteração");
        }
        else
        {
            m_RotaTO = new RotaTO();
            actionBar.setTitle("Rota - Inclusão");
        }

        m_LisViewItemRotas = (ListView)this.findViewById(R.id.rota_listviewItemRotas);
        m_Adapter = new ItemRotasAdapter(this, m_RotaTO.getItensRota());
        m_LisViewItemRotas.setAdapter(m_Adapter);

        m_TextViewAngulo = (TextView)this.findViewById(R.id.rota_tvAngulo);

        Button _btnAddPass = (Button)this.findViewById(R.id.rota_btnAddPasso);
        _btnAddPass.setOnClickListener(adicionarPassoClick());

        Button _btnRemPass = (Button)this.findViewById(R.id.rota_btnRemPasso);
        _btnRemPass.setOnClickListener(removerPassoClick());

        Button _btnSalvar = (Button)this.findViewById(R.id.rota_btnSalvar);
        _btnSalvar.setOnClickListener(salvarRota());

        m_EditTextOrigem = (EditText)this.findViewById(R.id.rota_EdtNomeOrigem);
        m_EditTextDestino = (EditText)this.findViewById(R.id.rota_EdtNomeDestino);

        m_EditTextOrigem.setText(m_RotaTO.getNomeOrigem());
        m_EditTextDestino.setText(m_RotaTO.getNomeDestino());

        startCompass();
    }

    private View.OnClickListener removerPassoClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_RotaTO.RemoverUltimoPasso();
                m_LisViewItemRotas.setAdapter(m_Adapter);
            }
        };
    }

    private View.OnClickListener salvarRota() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (m_EditTextOrigem.getText().equals("")) {
                    Toast.makeText(getBaseContext(), "Informe a origem!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if (m_EditTextDestino.getText().equals("")) {
                        Toast.makeText(getBaseContext(), "Informe o destino!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        m_RotaTO.setNomeOrigem(m_EditTextOrigem.getText().toString());
                        m_RotaTO.setNomeDestino(m_EditTextDestino.getText().toString());

                        RotaDM _dm = new RotaDM(getBaseContext());

                        if (m_RotaTO.getId() == 0) {
                            _dm.Salvar(m_RotaTO);
                        }
                        else
                        {
                            _dm.Atualizar(m_RotaTO);
                        }

                        onBackPressed();
                    }
                }

            }
        };
    }

    private View.OnClickListener adicionarPassoClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                m_RotaTO.AddItemRota(m_Angulo);
                m_LisViewItemRotas.setAdapter(m_Adapter);
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();

        stopCompass();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent("android.intent.CLOSE_ROTA_ACTIVITY");
        //PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        this.sendBroadcast(intent);
    }

    public void startCompass() {

        try {
            sensorMgr = (SensorManager)this.getSystemService(this.SENSOR_SERVICE);

            sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
            if (sensors.size() > 0)
                sensorGrav = sensors.get(0);

            sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
            if (sensors.size() > 0)
                sensorMag = sensors.get(0);

            sensorMgr.registerListener(this, sensorGrav, SensorManager.SENSOR_DELAY_NORMAL);
            sensorMgr.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL);

            locationMgr = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

            try {
                /* defaulting to our place */
                Location hardFix = new Location("ATL");
                hardFix.setLatitude(39.931261);
                hardFix.setLongitude(-75.051267);
                hardFix.setAltitude(1);

                try {
                    Location gps = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location network = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (gps != null)
                    {
                        currentLocation = (gps);
                    }
                    else
                    {
                        if (network != null) {
                            currentLocation = (network);
                        } else {
                            currentLocation = (hardFix);
                        }
                    }
                } catch (Exception ex2) {
                    currentLocation = (hardFix);
                }
                onLocationChanged(currentLocation);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex1) {
            try {
                if (sensorMgr != null) {
                    sensorMgr.unregisterListener(this, sensorGrav);
                    sensorMgr.unregisterListener(this, sensorMag);
                    sensorMgr = null;
                }
                if (locationMgr != null) {
                    locationMgr.removeUpdates(this);
                    locationMgr = null;
                }
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }

    public void stopCompass() {

        try {
            try {
                sensorMgr.unregisterListener(this, sensorGrav);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                sensorMgr.unregisterListener(this, sensorMag);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            sensorMgr = null;

            try {
                locationMgr.removeUpdates(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            locationMgr = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void updateText(double bearing) {
        int range = (int) (bearing / (360f / 16f));
        String dirTxt = "";

        if (range == 15 || range == 0) dirTxt = "N";
        else if (range == 1 || range == 2) dirTxt = "NE";
        else if (range == 3 || range == 4) dirTxt = "E";
        else if (range == 5 || range == 6) dirTxt = "SE";
        else if (range == 7 || range == 8) dirTxt = "S";
        else if (range == 9 || range == 10) dirTxt = "SW";
        else if (range == 11 || range == 12) dirTxt = "W";
        else if (range == 13 || range == 14) dirTxt = "NW";

        m_Angulo = (int) bearing;
        m_TextViewAngulo.setText("" + (m_Angulo) + ((char) 176) + " " + dirTxt);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!computing.compareAndSet(false, true)) return;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            smoothed = LowPassFilter.filter(event.values, grav);
            grav[0] = smoothed[0];
            grav[1] = smoothed[1];
            grav[2] = smoothed[2];
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            smoothed = LowPassFilter.filter(event.values, mag);
            mag[0] = smoothed[0];
            mag[1] = smoothed[1];
            mag[2] = smoothed[2];
        }

        // Get rotation matrix given the gravity and geomagnetic matrices
        SensorManager.getRotationMatrix(rotation, null, grav, mag);
        SensorManager.getOrientation(rotation, orientation);
        floatBearing = orientation[0];

        // Convert from radians to degrees
        floatBearing = Math.toDegrees(floatBearing); // degrees east of true
        // north (180 to -180)

        // Compensate for the difference between true north and magnetic north
        if (gmf != null)
            floatBearing += gmf.getDeclination();

        // adjust to 0-360
        if (floatBearing < 0)
            floatBearing += 360;

        //GlobalData.setBearing((int) floatBearing);

        computing.set(false);

        updateText(floatBearing);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Log.w(TAG, "Compass data unreliable");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) throw new NullPointerException();
        currentLocation = (location);
        gmf = new GeomagneticField((float) currentLocation.getLatitude(), (float) currentLocation.getLongitude(), (float) currentLocation.getAltitude(),
                System.currentTimeMillis());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_maintenance_rota, menu);

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
            return true;
        }
        else
        {
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }
}
