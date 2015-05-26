package tcc.rnapedometer;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import tcc.adrena.NeuralNetwork.Backpropagation;
import tcc.adrena.Util.Import;
import tcc.blindguide.R;

/**
 * Created by FAGNER on 03/04/2015.
 */
public class RNAPedometerManager {

    private int NEURON_INPUT_NUMBER = 15;
    private double VALUE_TO_STEP = 0.85;
    private int NEURON_INPUT_QTD = 3;
    private int NEURON_VALUE_AVG = 1;

    private ArrayList<SensorMotion> m_Motions = new ArrayList<SensorMotion>();
    private Integer m_SensorMotionID = 1;
    private RNAMode m_RNAMode = RNAMode.Stop;

    private int m_countCalibration = 0;
    private float m_xInicial = 0;
    private float m_yInicial = 0;
    private float m_zInicial = 0;
    private IRNAMessage m_RNAMessageRefresh;
    private IRNAStep m_RNAStepRefresh;
    private Backpropagation m_Backpropagation = null;
    private int m_StepCounter = 0;

    int _xChangedCount = 0;
    int m_countToVerify = 0;
    Queue m_QueueRight = new LinkedList();
    Queue m_QueueLeft = new LinkedList();

    float m_LastXValue = 0;
    boolean m_RightSide = false;

    boolean m_LefttSide = false;

    public int StepCounter()
    {
        return m_StepCounter;
    }

    public void AddStepCounter()
    {
        m_StepCounter++;
    }

    public RNAPedometerManager(InputStream learnFile) {

        try
        {
            m_Backpropagation = new Backpropagation(NEURON_INPUT_NUMBER,1);

            Import.KnowledgeBase(m_Backpropagation, learnFile);
        }
        catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void ReceiveData(float xAxis, float yAxis, float zAxis)
    {
        if (m_RNAMode == RNAMode.Calibrating)
        {
            m_countCalibration++;
            m_xInicial+=xAxis;
            m_yInicial+=yAxis;
            m_zInicial+=zAxis;
        }
        else {

            if (m_RNAMode == RNAMode.Recording) {
              if (m_xInicial < 0) {
                     xAxis = xAxis + (m_xInicial *-1);
                }
                else
                {
                    xAxis = xAxis-m_xInicial;
                }

                m_Motions.add(new SensorMotion(m_SensorMotionID, xAxis, yAxis, zAxis));
                m_SensorMotionID++;
            }
            else
            {
                if (m_RNAMode == RNAMode.Analysing)
                {
                    if (m_xInicial < 0) {
                        xAxis = xAxis + (m_xInicial *-1);
                    }
                    else
                    {
                        xAxis = xAxis-m_xInicial;
                    }

                    AnalyseStepsBySide(xAxis, yAxis, zAxis);
                    //AnalyseSteps(xAxis, yAxis, zAxis);
                }
            }
        }
    }

    public ArrayList<SensorMotion> getMotions() {
        return m_Motions;
    }

    public void setModeOperation(RNAMode mode) {

        if (mode == RNAMode.Recording) {

            m_SensorMotionID = 1;
            m_RNAMode = RNAMode.Calibrating;

            calibrateAxis(RNAMode.Recording);
        }
        else
        {

            if (mode == RNAMode.Stop) {

                m_RNAMode = RNAMode.Stop;

                MediaPlayerManager.PlaySound(R.drawable.stop_sound);

                m_xInicial = 0;
                m_yInicial = 0;
                m_zInicial = 0;

                m_countCalibration = 0;
            }
            else
            {
                if (mode == RNAMode.Analysing)
                {
                    m_SensorMotionID = 1;
                    m_RNAMode = RNAMode.Calibrating;

                    calibrateAxis(RNAMode.Analysing);
                }
            }
        }
    }

    private void calibrateAxis(final RNAMode mode)
    {
        AsyncTask<Void,Void,Void> _calibrateTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {

                try {

                    MediaPlayerManager.PlaySound(R.drawable.calibrate_sound);

                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                m_zInicial = m_zInicial/m_countCalibration;
                m_xInicial = m_xInicial/m_countCalibration;
                m_yInicial = m_yInicial/m_countCalibration;

                m_RNAMode = mode;

                if (mode == RNAMode.Analysing)
                {
                    MediaPlayerManager.PlaySound(R.drawable.analysing_steps_sound);
                }
                else
                {
                    if (mode == RNAMode.Recording)
                    {
                        MediaPlayerManager.PlaySound(R.drawable.start_sound);
                    }
                }

                if (m_RNAMessageRefresh != null)
                {
                    m_RNAMessageRefresh.Message("Gravação em andamento...");
                }

                return null;
            }
        };

        _calibrateTask.execute();

    }

    public void setM_RNAMessageRefresh(IRNAMessage m_RNAMessageRefresh) {
        this.m_RNAMessageRefresh = m_RNAMessageRefresh;
    }

    private void fillQueueWithZeros(Queue queue)
    {
        int _qtde = queue.size();
        for (int _pnt=_qtde; _pnt < NEURON_INPUT_NUMBER; _pnt++ )
        {
            queue.add(0);
        }
    }

    public void AnalyseStepsBySide(float x, float y, float z)
    {
        if ((m_LefttSide && m_QueueLeft.size() == NEURON_INPUT_NUMBER)
            || (m_LefttSide && x > 0))
        {
            Log.i("BlindGuide", "Escreve fila left...");

            if (m_QueueLeft.size() > NEURON_INPUT_QTD) {

                try {

                    fillQueueWithZeros(m_QueueLeft);

                    double[] _param = valueSteps(true, m_QueueLeft);
                    if (_param != null) {
                        double[] _resp = m_Backpropagation.Recognize(_param);

                        if (m_RNAStepRefresh != null) {
                            Log.i("BlindGuide", "Valor do passo: " + Double.toString(_resp[0]));

                            if (_resp[0] >= VALUE_TO_STEP) {
                                m_StepCounter++;
                            }
                            m_RNAStepRefresh.Step(_resp[0]);

                        }
                    }
                    else
                    {
                        Log.i("BlindGuide", "Parâmetros de entrada insuficiêntes. ");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.i("BlindGuide", "Fila Left Vazia");
            }


            m_QueueLeft.clear();
        }


        if ((m_RightSide && m_QueueRight.size() == NEURON_INPUT_NUMBER)
            || (m_RightSide && x < 0))
        {
            Log.i("BlindGuide", "Escreve fila right...");

            if (m_QueueRight.size() > NEURON_INPUT_QTD) {
                try {

                    fillQueueWithZeros(m_QueueRight);
                    double[] _param = valueSteps(false, m_QueueRight);
                    if (_param != null) {
                        double[] _resp = m_Backpropagation.Recognize(_param);

                        if (m_RNAStepRefresh != null) {
                            Log.i("BlindGuide", "Valor do passo: " + Double.toString(_resp[0]));

                            if (_resp[0] >= VALUE_TO_STEP) {
                                m_StepCounter++;
                            }

                            m_RNAStepRefresh.Step(_resp[0]);
                        }
                    }
                    else
                    {
                        Log.i("BlindGuide", "Parâmetros de entrada insuficiêntes. ");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.i("BlindGuide", "Fila Right Vazia");
            }

            m_QueueRight.clear();
        }

        if (m_LastXValue > 0 && x > 0)
        {
            m_QueueLeft.clear();
            m_LefttSide = false;

            if (m_QueueRight.size() == 0) {
                m_QueueRight.add(m_LastXValue);
            }

            m_QueueRight.add(x);

            m_RightSide = true;
        }

        if (m_LastXValue < 0 && x < 0)
        {
            m_QueueRight.clear();
            m_RightSide=false;

            if (m_QueueLeft.size() == 0) {
                m_QueueLeft.add(m_LastXValue);
            }
            m_QueueLeft.add(x);

            m_LefttSide = true;
        }

        m_LastXValue = x;
      //  Log.i("BlindGuide", String.valueOf(x));

    }

    public void AnalyseStepsByNumber(float x, float y, float z)
    {
        if (_xChangedCount >= NEURON_INPUT_NUMBER)
        {
            m_QueueRight.add(x);

            m_QueueRight.remove();


            if (m_countToVerify == 2)
            {
                m_countToVerify=0;

                try {

                    double[] _resp = m_Backpropagation.Recognize(valueSteps(false, m_QueueRight));

                    if (m_RNAStepRefresh != null)
                    {
                        Log.i("BlindGuide", "Valor do passo: " + Double.toString(_resp[0]));

                        m_RNAStepRefresh.Step(_resp[0]);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            m_countToVerify++;

        }
        else
        {
            m_QueueRight.add(x);
            _xChangedCount++;
        }
    }


    StringBuilder _valueBuilder = null;

    private double[] valueSteps(boolean isLeft, Queue queueValues)
    {
        _valueBuilder = new StringBuilder();

        double[] _resp = new double[NEURON_INPUT_NUMBER];
        int _pnt = 0;

        Log.i("BlindGuide", "Dados de entrada da REDE ");
        Log.i("BlindGuide", "--------------------------------------------");

        boolean _biggerOne = false;

        int _neuronValue = NEURON_VALUE_AVG;
        if (isLeft)
        {
            _neuronValue=_neuronValue*-1;
        }

        Iterator iterator = queueValues.iterator();
        while(iterator.hasNext())
        {

            String _stringValue = iterator.next().toString();
            _resp[_pnt] = Double.parseDouble(_stringValue);

            if (isLeft && _resp[_pnt] < (NEURON_VALUE_AVG*-1)) {
                _biggerOne = true;
            }
            else
            {
                if (!isLeft && _resp[_pnt] > NEURON_VALUE_AVG) {
                    _biggerOne = true;
                }
            }

            _valueBuilder.append(_stringValue).append(",");
            _pnt++;
        }

         Log.i("BlindGuide", _valueBuilder.toString());
        //WriteLog(_valueBuilder.toString());

        if (!_biggerOne) {
            _resp = null;
        }

        return _resp;
    }


    public void setRNAStepRefresh(IRNAStep m_RNAStep) {
        this.m_RNAStepRefresh = m_RNAStep;
    }

    public void Reset() {
        m_Motions.clear();

        m_StepCounter=0;

        String _dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String _pathfile = _dirPath + "/RNATrainning.txt";

        File _fileFile = new File(_pathfile);

        if (_fileFile.exists()) {
            _fileFile.delete();
        }
        _fileFile = null;

        m_StepCounter = 0;
    }


    // HELPERS
    public interface IRNAMessage
    {
        void Message(String status);
    }

    public interface IRNAStep
    {
        void Step(double number);
    }

    private void WriteLog(String message)
    {
        String _dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String _pathfile = _dirPath + "/RNATrainning.txt";

        File _fileFile = new File(_pathfile);

            try {
                if (!_fileFile.exists()) {


                    _fileFile.createNewFile();

                }


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        FileWriter _fileWriter = null;

        try
        {
            StringBuilder _builder = new StringBuilder();
            _builder.append(message)
                    .append("\r\n");

            _fileWriter = new FileWriter(_fileFile,true);
            _fileWriter.write(_builder.toString());

            _fileWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
