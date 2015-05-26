package tcc.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import tcc.blindguide.NavigationActivity;
import tcc.blindguide.R;
import tcc.dmlibrary.RotaDM;
import tcc.tolibrary.RotaTO;


public class NavigationFragment extends Fragment {

    private final int REQ_CODE_SPEECH_INPUT = 99;
    private int RESULT_OK = -1;
    //private EditText m_EdtRotaOrigem;
    private ImageButton m_BtnRotaOrigem;

    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View _view = inflater.inflate(R.layout.fragment_nevagacao, container, false);

        m_BtnRotaOrigem = (ImageButton)_view.findViewById(R.id.fragment_navegacao_BtnRotaOrigem);
        //m_EdtRotaOrigem = (EditText)_view.findViewById(R.id.fragment_navegacao_EdtRotaOrigem);

        m_BtnRotaOrigem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        return _view;
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Informe a Rota!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this.getActivity().getApplicationContext(),
                    "Infelizmente não pude compreende-lo, tente novamente!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                    //m_EdtRotaOrigem.setText(result.get(0));

                    buscaRotaporNome(result.get(0));
                }
                break;
            }

        }
    }

    private void buscaRotaporNome(String nome) {
        RotaTO _rota = new RotaDM(this.getActivity()).BuscaRotaByName(nome);
        if (_rota != null){

            Intent _int = new Intent(this.getActivity(), NavigationActivity.class);
            _int.putExtra("id", _rota.getId());
            this.getActivity().startActivity(_int);
        }
        else
        {
            Toast.makeText(this.getActivity(), "Rota: " + nome + " não encontrada!", Toast.LENGTH_LONG).show();
        }
    }
}
