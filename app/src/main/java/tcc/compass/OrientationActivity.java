package tcc.compass;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import tcc.blindguide.R;

public abstract class OrientationActivity extends Activity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mAcclerometer;
    private Sensor mField;
    private float[] mMagnetic;
    private float[] mGravity;
    private float mValorAngulo;
    private IAtualizaOrientacao mAtualizaOrientacao;


    @Override
    protected void onResume() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAcclerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, mAcclerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mField, SensorManager.SENSOR_DELAY_UI);

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void updateDirection()
    {
        float[] temp = new float[9];
        float[] R = new float[9];

        SensorManager.getRotationMatrix(temp, null, mGravity, mMagnetic);
        SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_X, SensorManager.AXIS_Z, R);

        float[] values = new float[3];
        SensorManager.getOrientation(R, values);

        for (int i=0; i < values.length; i++){
            Double degrees = (values[i]*180)/Math.PI;
            values[i] = degrees.floatValue();
        }

        mValorAngulo = 0;
        if (values[0] < 0)
            mValorAngulo = values[0] + 360;
        else
            mValorAngulo = values[0];

        if (mAtualizaOrientacao != null) {
            mAtualizaOrientacao.AtualizaOrientacao(mValorAngulo);
           // Log.i("BlindGuide", "Ângulo: " + String.valueOf(mValorAngulo));
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        switch (sensorEvent.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                mGravity = sensorEvent.values.clone();
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetic = sensorEvent.values.clone();
                break;
        }

        if (mGravity != null && mMagnetic != null){
            updateDirection();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public IAtualizaOrientacao getmAtualizaOrientacao() {
        return mAtualizaOrientacao;
    }

    public void setmAtualizaOrientacao(IAtualizaOrientacao mAtualizaOrientacao) {
        this.mAtualizaOrientacao = mAtualizaOrientacao;
    }

    public interface IAtualizaOrientacao
    {
        void AtualizaOrientacao(float angulo);
    }
}
