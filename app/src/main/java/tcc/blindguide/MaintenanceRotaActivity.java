package tcc.blindguide;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import tcc.adapters.ItemRotasAdapter;
import tcc.compass.LowPassFilter;
import tcc.compass.OrientationActivity;
import tcc.dmlibrary.PlantaBaixaDM;
import tcc.dmlibrary.RotaDM;
import tcc.tolibrary.ItemRotaTO;
import tcc.tolibrary.PlantaBaixa.AmbienteTO;
import tcc.tolibrary.PlantaBaixa.PlantaBaixaTO;
import tcc.tolibrary.RotaTO;

public class MaintenanceRotaActivity extends OrientationActivity {

    private ListView m_LisViewItemRotas;
    private RotaTO m_RotaTO = null;
    private ItemRotasAdapter m_Adapter;
    private TextView m_TextViewAngulo;
    private float m_Angulo = 0;
    private EditText m_EditTextOrigem;
    private EditText m_EditTextDestino;
    private Spinner m_SpinnerPlantaBaixa;
    private ArrayList<AmbienteTO> m_Ambientes = null;
    private long m_LastAmbiente;
    private Spinner m_spinnerAmbiente;
    private SpeechRecognizer sr;
    private listener m_Listener;

    private static final String TAG = "BlindGuida";

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

        //m_TextViewAngulo = (TextView)this.findViewById(R.id.rota_tvAngulo);

        this.setmAtualizaOrientacao(new IAtualizaOrientacao() {
            @Override
            public void AtualizaOrientacao(float angulo) {
                m_Angulo = angulo;

                //m_TextViewAngulo.setText(String.valueOf(m_Angulo));
            }
        });

        Button _btnAddPass = (Button)this.findViewById(R.id.rota_btnAddPasso);
        _btnAddPass.setOnClickListener(adicionarPassoClick());
        _btnAddPass.setOnLongClickListener(adicionarPassoObservacao());

        Button _btnRemPass = (Button)this.findViewById(R.id.rota_btnRemPasso);
        _btnRemPass.setOnClickListener(removerPassoClick());

        Button _btnSalvar = (Button)this.findViewById(R.id.rota_btnSalvar);
        _btnSalvar.setOnClickListener(salvarRota());

        m_EditTextOrigem = (EditText)this.findViewById(R.id.rota_EdtNomeOrigem);
        m_EditTextDestino = (EditText)this.findViewById(R.id.rota_EdtNomeDestino);

        m_SpinnerPlantaBaixa = (Spinner)this.findViewById(R.id.rota_SpinnerPlantasBaixas);
        m_SpinnerPlantaBaixa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                PlantaBaixaTO _selectedPlantaBaixa =   (PlantaBaixaTO) parent.getItemAtPosition(position);

                ArrayAdapter<AmbienteTO> _adapter = new ArrayAdapter<AmbienteTO>(MaintenanceRotaActivity.this, android.R.layout.simple_spinner_item, _selectedPlantaBaixa.getAmbientes());

                m_spinnerAmbiente.setAdapter(_adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        m_spinnerAmbiente = (Spinner)this.findViewById(R.id.rota_spinnerAmbiente);
        m_spinnerAmbiente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AmbienteTO _selectedAmbiente = (AmbienteTO) parent.getItemAtPosition(position);
                m_LastAmbiente = _selectedAmbiente.getID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        m_EditTextOrigem.setText(m_RotaTO.getNomeOrigem());
        m_EditTextDestino.setText(m_RotaTO.getNomeDestino());

        sr = SpeechRecognizer.createSpeechRecognizer(this);

        m_Listener = new listener();
        m_Listener.setListenerVoz(new IListenerVoz() {
            @Override
            public void StartListening(String valor) {
            iniciaGravacaoPassos();

            if (valor.equals("pa")){

                ItemRotaTO _itemRota = new ItemRotaTO();
                _itemRota.setAngulo((int)m_Angulo);
                _itemRota.setAmbiente(new AmbienteTO(m_LastAmbiente));

                m_RotaTO.AddItemRota(_itemRota);
                m_LisViewItemRotas.setAdapter(m_Adapter);
            }
            }
        });

        sr.setRecognitionListener(m_Listener);

        loadPlantasBaixas();
    }

    private View.OnLongClickListener adicionarPassoObservacao() {

        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final ItemRotaTO _itemRota = new ItemRotaTO();

                AlertDialog _alert = null;
                AlertDialog.Builder _builder = new AlertDialog.Builder(MaintenanceRotaActivity.this);

                View _viewNotes = LayoutInflater.from(MaintenanceRotaActivity.this).inflate(R.layout.dialog_observacao_passo, null);
                final EditText _edtNotes = (EditText) _viewNotes.findViewById(R.id.edt_observacao);

                _edtNotes.setText(_itemRota.getObservacao());

                final int _angle = (int) m_Angulo;

                _builder.setTitle("Observações");
                _builder.setView(_viewNotes);
                _builder.setPositiveButton("Salvar", new android.content.DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        _itemRota.setObservacao(_edtNotes.getText().toString());
                        adicionarPasso(_itemRota, _angle);
                    }
                });

                _builder.setNegativeButton("Cancelar", null);

                _alert = _builder.create();
                _alert.show();

                return false;
            }
        };
    }

    private void loadAlertAmbiente(final ItemRotaTO itemRota, final DialogInterface.OnClickListener click)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Add the buttons
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               click.onClick(dialog, id);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        View _view = this.getLayoutInflater().inflate(R.layout.maintenance_rota_dialog, null, false);


        builder.setView(_view);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void loadPlantasBaixas(){

        ArrayList<PlantaBaixaTO> _plantasBaixas = new PlantaBaixaDM(this).BuscaPlantasBaixas();

        ArrayAdapter<PlantaBaixaTO> _adapter = new ArrayAdapter<PlantaBaixaTO>(this, android.R.layout.simple_spinner_item, _plantasBaixas);

        m_SpinnerPlantaBaixa.setAdapter(_adapter);

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
                adicionarPasso();
            }
        };
    }

    private void adicionarPasso()
    {
        adicionarPasso(new ItemRotaTO());
    }

    private void adicionarPasso(ItemRotaTO itemRota)
    {
        if (itemRota == null)
            itemRota = new ItemRotaTO();

        itemRota.setAngulo((int)m_Angulo);
        itemRota.setAmbiente(new AmbienteTO(m_LastAmbiente));

        m_RotaTO.AddItemRota(itemRota);

        m_LisViewItemRotas.setAdapter(m_Adapter);
    }

    private void adicionarPasso(ItemRotaTO itemRota, Integer angulo)
    {
        if (itemRota == null)
            itemRota = new ItemRotaTO();

        itemRota.setAngulo(angulo);
        itemRota.setAmbiente(new AmbienteTO(m_LastAmbiente));

        m_RotaTO.AddItemRota(itemRota);

        m_LisViewItemRotas.setAdapter(m_Adapter);
    }

    private void iniciaGravacaoPassos()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        sr.startListening(intent);

        Log.i("111111","11111111");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent("android.intent.CLOSE_ROTA_ACTIVITY");
        //PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        this.sendBroadcast(intent);

        sr.destroy();
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

    class listener implements RecognitionListener
    {
        private IListenerVoz ListenerVoz;

        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)
        {
            Log.d(TAG,  "error " +  error);
            Log.i("BlindGuide", "error " + error);
        }
        public void onResults(Bundle results)
        {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            String _valor = "";
            if (data != null && data.size() > 0){
                _valor = data.get(0).toString();
            }


           if (this.ListenerVoz != null){
                this.ListenerVoz.StartListening(_valor);
            }

        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }

        public IListenerVoz getListenerVoz() {
            return ListenerVoz;
        }

        public void setListenerVoz(IListenerVoz listenerVoz) {
            ListenerVoz = listenerVoz;
        }
    }

    interface IListenerVoz
    {
        void StartListening(String valor);
    }

}
