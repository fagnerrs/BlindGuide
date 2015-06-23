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
    private TextToSpeech m_TextToSpeech;

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

    public void MonitorRota(Activity activity, Integer angulo, Integer passo)
    {
        int _valorAnguloAceito = 30;

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

        Log.i("BlindGuide", "Monitoramento - Passo: " + String.valueOf(_currentStep.getPasso()));
        Log.i("BlindGuide", "Monitoramento - Angulo Atual: " + String.valueOf(angulo));
        Log.i("BlindGuide", "Monitoramento - Angulo Esperado: " + _currentStep.getAngulo());

        if (_currentStep != null)
        {
            if (angulo >= (_currentStep.getAngulo() - _valorAnguloAceito) &&
                angulo <= (_currentStep.getAngulo() + _valorAnguloAceito)){
                Toast.makeText(activity, "Rota certa...", Toast.LENGTH_SHORT).show();
                Log.i("BlindGuide", "Dentro da rota");
            }
            else
            {
                Log.i("BlindGuide", "Fora da rota");

                String _textToVoice = "";
                if (angulo < _currentStep.getAngulo())
                {
                    int _val = _currentStep.getAngulo() - angulo;

                    _textToVoice = "Vire " + String.valueOf(_val) +
                            " graus a direita.";
                }
                else
                {
                    int _val = angulo - _currentStep.getAngulo();

                    _textToVoice = "Vire " + String.valueOf(_val) +
                            " graus a esquerda.";
                }

                final String final_textToVoice = _textToVoice;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        m_TextToSpeech.speak(final_textToVoice, TextToSpeech.QUEUE_FLUSH, null);
                    }
                });

                Log.i("BlindGuide", "Monitoramento - " + _textToVoice);
            }
        }
    }
}
