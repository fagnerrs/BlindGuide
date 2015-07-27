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
import java.util.Timer;
import java.util.TimerTask;

import tcc.compass.CompassActivity;
import tcc.compass.GlobalData;
import tcc.compass.OrientationActivity;
import tcc.dmlibrary.RotaDM;
import tcc.rnapedometer.MediaPlayerManager;
import tcc.rnapedometer.RNAMode;
import tcc.rnapedometer.RNAPedometerManager;
import tcc.tolibrary.ItemRotaTO;
import tcc.tolibrary.RotaTO;


public class NavigationActivity extends OrientationActivity {

    private RotaTO m_Rota = null;

    private static final String TAG = "BlindGuide";

    private static TextView m_TvAngulotext = null;
    private static TextView m_TvOrigem = null;
    private static TextView m_TvDestino = null;
    private TextView m_TvPasso;
    private TextView m_TvCurrentPlace;
    private static View compassView = null;

    private RNAPedometerManager m_PedometerManager = null;
    private Timer m_TimerMonitor;
    private Timer m_TimerPassosMonitor;
    private Integer m_CurrentStep = 0;
    private TextView m_TvAnguloRota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        m_TvAngulotext = (TextView)this.findViewById(R.id.activity_navigation_TvAngulo);
        m_TvOrigem = (TextView)this.findViewById(R.id.activity_navigation_TvRotaOrigem);
        m_TvDestino = (TextView)this.findViewById(R.id.activity_navigation_TvRotaDestino);
        m_TvPasso = (TextView)this.findViewById(R.id.activity_navigation_TvPasso);
        m_TvCurrentPlace = (TextView)this.findViewById(R.id.activity_navigation_TvCurrentPlace);
        m_TvAnguloRota = (TextView)this.findViewById(R.id.activity_navigation_TvAnguloRota);

        m_TimerMonitor = new Timer();
        m_TimerMonitor.schedule(new TimerTask() {
            @Override
            public void run() {
                if (m_Rota != null)
                    NavigationActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           m_Rota.MonitorRota(NavigationActivity.this, (int) GlobalData.getBearing(), m_PedometerManager.StepCounter());
                        }
                    });
            }
        }, 5000, 8000);

       /* m_TimerPassosMonitor = new Timer();
        m_TimerPassosMonitor.schedule(new TimerTask() {
            @Override
            public void run() {
                if (m_Rota != null)
                    NavigationActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // m_Rota.RetornaPassosPorVoz(NavigationActivity.this, m_PedometerManager.StepCounter());
                        }
                    });
            }
        }, 10000, 10000); */

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

            m_TvOrigem.setText(m_Rota.getNomeOrigem());
            m_TvDestino.setText(m_Rota.getNomeDestino());
            m_TvCurrentPlace.setText(m_Rota.getAmbiente(1).getAmbiente().getDescricao());
            m_TvAnguloRota.setText(String.valueOf(m_Rota.getAmbiente(1).getAngulo()));
            m_TvPasso.setText("0");
            m_Rota.getMaxStep();

            compassView = findViewById(R.id.activity_navigation_compassview);

            try {
                m_PedometerManager = new RNAPedometerManager(this.getAssets().open("Aprendizado.json"));

                MediaPlayerManager.Inicialize(this);

                m_PedometerManager.setModeOperation(RNAMode.Analysing);

                m_PedometerManager.setRNAStepRefresh(new RNAPedometerManager.IRNAStep() {
                    @Override
                    public void Step(final double number) {

                        int _passo = m_PedometerManager.StepCounter();

                        if (m_CurrentStep != _passo){

                            m_TvPasso.setText(String.valueOf(_passo));

                            if (m_CurrentStep <= m_Rota.getMaxStep()) {
                                ItemRotaTO _itemRota = m_Rota.getAmbiente(_passo);

                                m_TvCurrentPlace.setText(_itemRota.getAmbiente().getDescricao());
                                m_TvAnguloRota.setText(String.valueOf(_itemRota.getAngulo()));

                                m_CurrentStep = _passo;

                                if (_itemRota.getObservacao() != null && !_itemRota.getObservacao().equals("")) {
                                    m_Rota.Speak(NavigationActivity.this, _itemRota.getObservacao());
                                }

                                if (m_CurrentStep == m_Rota.getMaxStep()) {
                                    m_Rota.Speak(NavigationActivity.this, "Fim da Rota!");
                                }
                            }
                        }

                    }
                });



            } catch (IOException e) {
                e.printStackTrace();

                m_PedometerManager.setModeOperation(RNAMode.Stop);
            }

        }
    }

    @Override
    protected void onStop() {
        try{

            super.onStop();


            m_TimerMonitor.cancel();
            m_TimerPassosMonitor.cancel();

            m_PedometerManager.setModeOperation(RNAMode.Stop);
            m_PedometerManager.Reset();


        }
        catch (Exception ex){
            ex.printStackTrace();
        }
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
