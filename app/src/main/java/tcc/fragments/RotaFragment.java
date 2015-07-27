package tcc.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import tcc.adapters.IRefreshListView;
import tcc.adapters.RotasAdapter;
import tcc.blindguide.MaintenanceRotaActivity;
import tcc.blindguide.R;
import tcc.dmlibrary.RotaDM;
import tcc.tolibrary.RotaTO;


public class RotaFragment extends Fragment {


    private RotasAdapter m_RotasAdapter;
    private ArrayList<RotaTO> m_RotasTO;
    private ListView m_listViewRotas;
    private TextView _tvRota;
    private BroadcastReceiver mReceiver;

    public RotaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                atualizaListView();
            }

        };

        IntentFilter filter = new IntentFilter("android.intent.CLOSE_ROTA_ACTIVITY");
        this.getActivity().registerReceiver(mReceiver, filter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mReceiver != null) {
            this.getActivity().unregisterReceiver(mReceiver);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View _view = inflater.inflate(R.layout.fragment_rota, container, false);

        m_listViewRotas = (ListView) _view.findViewById(R.id.configuration_listviewRotas);
        m_listViewRotas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer ss = 0;
            }
        });

        Button _buttonAddRota = (Button) _view.findViewById(R.id.configuration_btnAddRota);
        _tvRota = (TextView) _view.findViewById(R.id.configuration_tvRotas);

        // Chama tela de cadastro de nova rota
        _buttonAddRota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent _int = new Intent(getActivity(), MaintenanceRotaActivity.class);
            getActivity().startActivity(_int);

            //ApplicationManager.Navigate(R.id.framelayout_rotas, new RotaFragment());
            }
        });

        atualizaListView();

        return _view;
    }

    private void atualizaListView()
    {
        // Busca rotas cadastradas no banco de dados
        m_RotasTO = new RotaDM(this.getActivity()).BuscaRotas();


        m_RotasAdapter = new RotasAdapter(this.getActivity(), m_RotasTO);
        m_RotasAdapter.setRefreshListView(new IRefreshListView() {
            @Override
            public void RefreshListView(long id) {

                RotaTO _rotaToDelete = null;

                for(RotaTO _rotaTO : m_RotasTO)
                {
                    if (_rotaTO.getId() == id)
                    {
                        _rotaToDelete = _rotaTO;
                        break;
                    }
                }

                if (_rotaToDelete != null)
                {
                    m_RotasTO.remove(_rotaToDelete);
                    m_listViewRotas.setAdapter(m_RotasAdapter);
                }

            }
        });

        m_listViewRotas.setAdapter(m_RotasAdapter);


        if (m_RotasTO.size()== 0)
            _tvRota.setText("Nenhuma rota cadastrada!");
        else
            _tvRota.setText("Listagem de rotas");
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
