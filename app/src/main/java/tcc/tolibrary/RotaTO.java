package tcc.tolibrary;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by FAGNER on 28/03/2015.
 */
public class RotaTO {

    private long Id;
    private String NomeOrigem;
    private String NomeDestino;
    private ArrayList<ItemRotaTO> ItensRota;
    private static TextToSpeech m_TextToSpeech;
    private boolean m_EmRota = false;
    private Integer m_MaxSteps = -1;
    private boolean m_RouteFinish = false;

    public Integer getMaxStep()
    {
        if (m_MaxSteps > 0)
            return m_MaxSteps;

        for( ItemRotaTO _item :this.getItensRota())
        {
            if (m_MaxSteps < _item.getPasso()){
                m_MaxSteps = _item.getPasso();
            }

        }

        return m_MaxSteps;
    }

    public RotaTO()
    {
        ItensRota = new ArrayList<>();
    }

    public String getNomeDestino() {
        return NomeDestino;
    }

    public void setNomeDestino(String nomeDestino) {
        NomeDestino = nomeDestino;
    }

    public String getNomeOrigem() {
        return NomeOrigem;
    }

    public void setNomeOrigem(String nomeOrigem) {
        NomeOrigem = nomeOrigem;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public ArrayList<ItemRotaTO> getItensRota() {
        return ItensRota;
    }

    public void SetItensRota(ArrayList<ItemRotaTO> itensRota)
    {
        ItensRota = itensRota;
    }

    public void AddItemRota(ItemRotaTO itemRota)
    {
        itemRota.setPasso(ItensRota.size()+1);

        ItensRota.add(itemRota);
    }

    public void AddItemRota(int angulo)
    {
        ItemRotaTO _itemRotaT0 = new ItemRotaTO();
        _itemRotaT0.setAngulo(angulo);
        _itemRotaT0.setPasso(ItensRota.size()+1);

        ItensRota.add(_itemRotaT0);
    }

    public void RemoverUltimoPasso() {

        if (ItensRota.size() > 0)
        {
            ItensRota.remove(ItensRota.size()-1);
        }
    }

    public ItemRotaTO getAmbiente(Integer passo){

        ItemRotaTO _resp = null;
        for (ItemRotaTO _itemRota: this.getItensRota())
        {
            if (_itemRota.getPasso() == passo){
                _resp = _itemRota;
                break;
            }
        }

        if (_resp == null){
            _resp = getItensRota().get(getItensRota().size()-1);
        }

        return _resp;
    }

    public void MonitorRota(Activity activity, Integer angulo, Integer passo)
    {
        int _valorAnguloAceito = 15;

        if (passo == 0){
            passo = 1;
        }

        if (m_TextToSpeech == null) {
            m_TextToSpeech = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        m_TextToSpeech.setLanguage(Locale.getDefault());
                    }
                }
            });
        }

        ItemRotaTO _currentStep = null;
        for (ItemRotaTO _itemRota: this.getItensRota())
        {
            if (_itemRota.getPasso() == passo){
                _currentStep = _itemRota;
                break;
            }
        }

        Log.i("BlindGuide", "Monitoramento - Passo atual: " + String.valueOf(passo));
        Log.i("BlindGuide", "Monitoramento - Passo máximo: " + String.valueOf(this.getMaxStep()));

        if (passo == this.getMaxStep()){
            if (!m_RouteFinish){

                Log.i("BlindGuide", "Fim da Rota!");

                //m_TextToSpeech.speak("Fim da Rota!", TextToSpeech.QUEUE_FLUSH, null);


                m_RouteFinish = true;
            }
        }

        if (_currentStep != null)
        {
            Log.i("BlindGuide", "Monitoramento - Passo: " + String.valueOf(_currentStep.getPasso()));
            Log.i("BlindGuide", "Monitoramento - Angulo Atual: " + String.valueOf(angulo));
            Log.i("BlindGuide", "Monitoramento - Angulo Esperado: " + _currentStep.getAngulo());

            final String _resp = retonaAjusteDeAngulo( _currentStep.getAngulo(), angulo);
            if (!_resp.equals("")){


            m_TextToSpeech.speak(_resp, TextToSpeech.QUEUE_FLUSH, null);


            }
        }
    }

    public void RetornaPassosPorVoz(final Activity activity, final int passos) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (m_TextToSpeech == null) {
                    m_TextToSpeech = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                m_TextToSpeech.setLanguage(Locale.getDefault());
                            }
                        }
                    });
                }
                if (passos > 1)
                    m_TextToSpeech.speak(String.valueOf(passos) +" passos", TextToSpeech.QUEUE_FLUSH, null);
                else
                    m_TextToSpeech.speak(String.valueOf(passos) +" passo", TextToSpeech.QUEUE_FLUSH, null);
            }
        });

    }

    public void Speak(Activity activity, final String text){

        if (m_TextToSpeech == null) {
            m_TextToSpeech = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        m_TextToSpeech.setLanguage(Locale.getDefault());

                        m_TextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
        }
        else
        {
            m_TextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }


    }

    private String retonaAjusteDeAngulo(Integer anguloRota, Integer anguloAtual) {

        String _resp = "";

        Integer _grausDeTolerancia = 15;


        int _tmpValor = 0;

        if (anguloAtual >= (anguloRota - _grausDeTolerancia) &&
                anguloAtual < (anguloRota + _grausDeTolerancia)) {



            if (!m_EmRota) {
                _resp = "Usuário na rota";
                m_EmRota = true;
            }
            else
            {
                _resp = "";
            }

        }
        else
        {
            m_EmRota = false;
            int _quadranteAtual = retornaQuadrante(anguloAtual);
            int _quadrantePretendido = retornaQuadrante(anguloRota);

            boolean _isEsquerda = false;

            if (_quadranteAtual == _quadrantePretendido) {
                if (anguloRota > anguloAtual) {
                    _tmpValor = (anguloRota - anguloAtual);
                } else {
                    _isEsquerda = true;
                    _tmpValor = (anguloAtual - anguloRota);
                }
            } else if (_quadranteAtual > _quadrantePretendido) {

                if (_quadranteAtual == 4 && _quadrantePretendido == 1) {

                    _tmpValor = (360 - _quadranteAtual) + _quadrantePretendido;


                } else {
                    _isEsquerda = true;
                    _tmpValor =(anguloAtual - anguloRota);
                }
            } else {


                if (_quadranteAtual == 1 && _quadrantePretendido == 4) {
                    _isEsquerda = true;

                    _tmpValor =  ((360 - anguloRota) + anguloAtual);

                } else {

                    _tmpValor =  (anguloRota - anguloAtual);
                }

            }

            if (_isEsquerda) {
                _resp = "Vire a esqueda " + String.valueOf(_tmpValor) + "graus";
            } else {
                _resp = "Vire a direita " + String.valueOf(_tmpValor) + "graus";
            }

        }

        return _resp;
    }

    private int retornaQuadrante(float angulo)
    {
        int _resp = 0;

        if (angulo >= 0 && angulo <= 90)
        {
            _resp = 1;
        }
        else
        if (angulo > 90 && angulo <= 180)
        {
            _resp = 2;
        }
        else if (angulo > 180 && angulo <= 270)
        {
            _resp = 3;
        }
        else if (angulo >270 && angulo <= 360)
        {
            _resp = 4;
        }

        return _resp;
    }

    public void Destroy(){
        if (m_TextToSpeech != null)
        {
            m_TextToSpeech.shutdown();
        }
    }

}
