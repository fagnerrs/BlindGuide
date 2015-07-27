package tcc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import tcc.blindguide.R;
import tcc.tolibrary.ItemRotaTO;
import tcc.tolibrary.RotaTO;

/**
 * Created by FAGNER on 28/03/2015.
 */
public class ItemRotasAdapter extends BaseAdapter  {
    private LayoutInflater m_BaseInflater;
    private final ArrayList<ItemRotaTO> m_BaseList;

    public ItemRotasAdapter(Context context, ArrayList<ItemRotaTO> baseList)
    {
        m_BaseList = baseList;
        m_BaseInflater = LayoutInflater.from(context);
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

        View _view = m_BaseInflater.inflate(R.layout.item_rotas_adapter_view, null);
        ItemRotaTO _rotaTO = m_BaseList.get(position);

        TextView _tvPasso = (TextView)_view.findViewById(R.id.itemrotasadapterview_TvPasso);
        TextView _tvAngulo = (TextView)_view.findViewById(R.id.itemrotasadapterview_TvAngulo);
        TextView _tvObservacoes = (TextView)_view.findViewById(R.id.itemrotasadapterview_TvObservacoes);

        _tvPasso.setText(String.valueOf(_rotaTO.getPasso()));
        _tvAngulo.setText(String.valueOf(_rotaTO.getAngulo()));
        _tvObservacoes.setText(_rotaTO.getObservacao());

        return _view;
    }
}
