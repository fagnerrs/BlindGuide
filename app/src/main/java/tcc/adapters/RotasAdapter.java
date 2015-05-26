package tcc.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import tcc.blindguide.R;
import tcc.dmlibrary.RotaDM;
import tcc.blindguide.MaintenanceRotaActivity;
import tcc.tolibrary.RotaTO;

/**
 * Created by FAGNER on 28/03/2015.
 */
public class RotasAdapter  extends BaseAdapter  {

    private final Context m_Context;
    private LayoutInflater m_BaseInflater;
    private final ArrayList<RotaTO> m_BaseList;
    private IRefreshListView m_RefreshListView;

    public RotasAdapter(Context context, ArrayList<RotaTO> baseList)
    {
        m_BaseList = baseList;
        m_BaseInflater = LayoutInflater.from(context);
        m_Context = context;
    }

    @Override
    public int getCount() {
        return m_BaseList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View _view = m_BaseInflater.inflate(R.layout.rotas_adapter_view, null);
        RotaTO _rotaTO = m_BaseList.get(position);

        TextView _tvOrigem = (TextView)_view.findViewById(R.id.rotasadapterview_TvOrigem);
        TextView _tvDestino = (TextView)_view.findViewById(R.id.rotasadapterview_TvDestino);

        _tvOrigem.setText("Origem: " + _rotaTO.getNomeOrigem());
        _tvDestino.setText("Destino: " + _rotaTO.getNomeDestino());

        ImageButton _btnUpdate = (ImageButton)_view.findViewById(R.id.rotasadapterview_BtnAlterar);
        ImageButton _btnRemove = (ImageButton)_view.findViewById(R.id.rotasadapterview_BtnRemover);

        _btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long _id = Long.valueOf(v.getTag().toString());

                Intent _int = new Intent(m_Context, MaintenanceRotaActivity.class);
                _int.putExtra("id", _id);
                m_Context.startActivity(_int);

            }
        });

        _btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog _dialog;
                AlertDialog.Builder _builder = new AlertDialog.Builder(m_Context);
                _builder.setMessage("Confirma exclusão da ROTA?");
                _builder.setNegativeButton("Não", null);
                _builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long _id = Long.valueOf(v.getTag().toString());

                        new RotaDM(m_Context).Delete(_id);

                        if (m_RefreshListView != null)
                        {
                            m_RefreshListView.RefreshListView(_id);
                        }
                    }
                });

                _dialog = _builder.create();
                _dialog.show();



            }
        });


        _btnUpdate.setTag(_rotaTO.getId());
        _btnRemove.setTag(_rotaTO.getId());

        return _view;
    }

    public IRefreshListView getRefreshListView() {
        return m_RefreshListView;
    }

    public void setRefreshListView(IRefreshListView m_RefreshListView) {
        this.m_RefreshListView = m_RefreshListView;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public boolean isEnabled(int arg0)
    {
        return true;
    }
}
