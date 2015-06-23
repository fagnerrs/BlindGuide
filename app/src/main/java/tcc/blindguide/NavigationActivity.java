package tcc.blindguide;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import tcc.compass.CompassActivity;
import tcc.compass.GlobalData;
import tcc.compass.OrientationActivity;
import tcc.dmlibrary.RotaDM;
import tcc.rnapedometer.MediaPlayerManager;
import tcc.rnapedometer.RNAMode;
import tcc.rnapedometer.RNAPedometerManager;
import tcc.tolibrary.RotaTO;


public class NavigationActivity extends OrientationActivity {

    private RotaTO m_Rota = null;

    private static final String TAG = "BlindGuide";

    private static TextView m_TvAngulotext = null;
    private static TextView m_TvOrigem = null;
    private static TextView m_TvDestino = null;
    private TextView m_TvPasso;

    private static View compassView = null;

    private RNAPedometerManager m_PedometerManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        m_TvAngulotext = (TextView)this.findViewById(R.id.activity_navigation_TvAngulo);
        m_TvOrigem = (TextView)this.findViewById(R.id.activity_navigation_TvRotaOrigem);
        m_TvDestino = (TextView)this.findViewById(R.id.activity_navigation_TvRotaDestino);
        m_TvPasso = (TextView)this.findViewById(R.id.activity_navigation_TvPasso);

        this.setmAtualizaOrientacao(new IAtualizaOrientacao() {
            @Override
            public void AtualizaOrientacao(float angulo) {
                GlobalData.setBearing(angulo);
                m_TvAngulotext.setText(String.valueOf(angulo));
            }
        });

        // Set up action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        actionBar.setDisplayHomeAsUpEnabled(true);

        long _id = this.getIntent().getLongExtra("id",0);
        if (_id > 0){

            m_Rota = new RotaDM(this).BuscaRotaById(_id);

            m_TvOrigem.setText("Origem: " + m_Rota.getNomeOrigem());
            m_TvDestino.setText("Destino: " + m_Rota.getNomeDestino());

            compassView = findViewById(R.id.activity_navigation_compassview);

            try {
                    m_PedometerManager = new RNAPedometerManager(this.getAssets().open("Aprendizado.json"));

                    MediaPlayerManager.Inicialize(this);

                    m_PedometerManager.setModeOperation(RNAMode.Analysing);

                    m_PedometerManager.setRNAStepRefresh(new RNAPedometerManager.IRNAStep() {
                        @Override
                        public void Step(final double number) {
                            //MediaPlayerManager.PlaySound(R.drawable.step_sound);
                            m_TvPasso.setText(String.valueOf(m_PedometerManager.StepCounter()));
                            m_Rota.MonitorRota(NavigationActivity.this, (int)GlobalData.getBearing(), m_PedometerManager.StepCounter());
                        }
                    });
            } catch (IOException e) {
                e.printStackTrace();

                m_PedometerManager.setModeOperation(RNAMode.Stop);
            }

        }
    }


    @Override
    public void onPause() {
        super.onPause();

        m_PedometerManager.setModeOperation(RNAMode.Stop);
        m_PedometerManager.Reset();
    }

    @Override
    public void onSensorChanged(SensorEvent evt) {
        super.onSensorChanged(evt);

        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER || evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // Tell the compass to update it's graphics
            if (compassView != null) compassView.postInvalidate();
        }
        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                m_PedometerManager.ReceiveData(evt.values[0],evt.values[1],evt.values[2]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_navigation, menu);
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
