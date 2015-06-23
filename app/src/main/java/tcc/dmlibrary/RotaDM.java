package tcc.dmlibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import tcc.database.DataBaseEngine;
import tcc.tolibrary.ConfiguracaoTO;
import tcc.tolibrary.ItemRotaTO;
import tcc.tolibrary.PlantaBaixa.AmbienteTO;
import tcc.tolibrary.RotaTO;

/**
 * Created by FAGNER on 28/03/2015.
 */
public class RotaDM {

    public SQLiteDatabase m_DataBase = null;
    public DataBaseEngine m_dbEngine = null;

    public RotaDM(Context context)
    {
        m_dbEngine = new DataBaseEngine(context);
    }

    public long Salvar(RotaTO value) {

        long _insertedId = 0;

        ContentValues _values = new ContentValues();

        value.setId(MaxIdRota()+1);

        _values.put("id", value.getId());
        _values.put("nome_origem", value.getNomeOrigem());
        _values.put("nome_destino", value.getNomeDestino());

        m_DataBase = m_dbEngine.getWritableDatabase();

        _insertedId = m_DataBase.insert("rota", null, _values);

        long _maxItemID = MaxIdItemRota()+1;

        for (ItemRotaTO _item : value.getItensRota())
        {
            _item.setId(_maxItemID);
            salvarItemRota(value.getId(), _item);

            _maxItemID++;
        }

        return _insertedId;
    }

    private long salvarItemRota(long rotaID, ItemRotaTO value) {

        long _insertedId = 0;

        ContentValues _values = new ContentValues();

        _values.put("id", value.getId());
        _values.put("rota_id", rotaID);
        _values.put("passo", value.getPasso());
        _values.put("angulo", value.getAngulo());
        _values.put("ambiente_id", value.getAmbiente().getID());

        m_DataBase = m_dbEngine.getWritableDatabase();

        _insertedId = m_DataBase.insert("item_rota", null, _values);

        return _insertedId;
    }


    public ArrayList<RotaTO> BuscaRotas()
    {
        ArrayList<RotaTO> _resp = new ArrayList<>();

        String _querySQL = new StringBuilder().
                append(" select id, ").
                append(" nome_origem, ").
                append(" nome_destino ").
                append(" from rota ").toString();


        m_DataBase = m_dbEngine.getReadableDatabase();

        Cursor _cursorPedidos = m_DataBase.
                rawQuery(_querySQL, null);

        while (_cursorPedidos.moveToNext()) {

            RotaTO _rota = new RotaTO();
            _rota.setId(_cursorPedidos.getLong(0));
            _rota.setNomeOrigem(_cursorPedidos.getString(1));
            _rota.setNomeDestino(_cursorPedidos.getString(2));

            _resp.add(_rota);
        }

        m_DataBase.close();

        return _resp;
    }

    public RotaTO BuscaRotaByName(String nome)
    {
        RotaTO _resp = null;

        String _querySQL = new StringBuilder().
                append(" select id, ").
                append(" nome_origem, ").
                append(" nome_destino ").
                append(" from rota ").
                append(" where nome_origem = ").append("'").append(nome).append("'").
                append(" COLLATE NOCASE ").toString();


        m_DataBase = m_dbEngine.getReadableDatabase();


        Cursor _cursorPedidos = m_DataBase.
                rawQuery(_querySQL, null);

        while (_cursorPedidos.moveToNext()) {
            _resp = new RotaTO();

            _resp.setId(_cursorPedidos.getLong(0));
            _resp.setNomeOrigem(_cursorPedidos.getString(1));
            _resp.setNomeDestino(_cursorPedidos.getString(2));

            break;
        }

        if (_resp != null) {
            _resp.SetItensRota(BuscaItensRota(_resp.getId()));
        }

        m_DataBase.close();

        return _resp;
    }

    public RotaTO BuscaRotaById(long id)
    {
        RotaTO _resp = new RotaTO();

        String _querySQL = new StringBuilder().
                append(" select id, ").
                append(" nome_origem, ").
                append(" nome_destino ").
                append(" from rota ").
                append(" where id = ").append(String.valueOf(id)).toString();


        m_DataBase = m_dbEngine.getReadableDatabase();

        Cursor _cursorPedidos = m_DataBase.
                rawQuery(_querySQL, null);

        while (_cursorPedidos.moveToNext()) {


            _resp.setId(_cursorPedidos.getLong(0));
            _resp.setNomeOrigem(_cursorPedidos.getString(1));
            _resp.setNomeDestino(_cursorPedidos.getString(2));

           break;
        }

        _resp.SetItensRota(BuscaItensRota(_resp.getId()));

        m_DataBase.close();

        return _resp;
    }

    private ArrayList<ItemRotaTO> BuscaItensRota(long rotaId)
    {
        ArrayList<ItemRotaTO> _resp = new ArrayList<>();

        String _querySQL = new StringBuilder().
                append(" select id, ").
                append(" rota_id, ").
                append(" passo, ").
                append(" angulo, ").
                append(" ambiente_id ").
                append(" from item_rota ").
                append(" where rota_id = ").append(rotaId).toString();


        m_DataBase = m_dbEngine.getWritableDatabase();

        Cursor _cursorPedidos = m_DataBase.
                rawQuery(_querySQL, null);

        while (_cursorPedidos.moveToNext()) {

            ItemRotaTO _rota = new ItemRotaTO();

            _rota.setId(_cursorPedidos.getLong(0));
            _rota.setRotaID(_cursorPedidos.getLong(1));
            _rota.setPasso(_cursorPedidos.getInt(2));
            _rota.setAngulo(_cursorPedidos.getInt(3));
            _rota.setAmbiente(new AmbienteTO(_cursorPedidos.getLong(4)));

            _resp.add(_rota);
        }

        return _resp;
    }

    public void Delete(long id)
    {

        m_DataBase = m_dbEngine.getWritableDatabase();

        m_DataBase.delete("item_rota", "rota_id = " + String.valueOf(id) , null);
        m_DataBase.delete("rota", "id = " + String.valueOf(id) , null);

    }

    public void DeletarTodos()
    {
        m_DataBase = m_dbEngine.getWritableDatabase();
        m_DataBase.delete("rota", null , null);
    }

    public long MaxIdRota()
    {
        long _resp = 1;

        m_DataBase = m_dbEngine.getReadableDatabase();
        Cursor _maxPed = m_DataBase.rawQuery("select max(id) from rota", null);

        while (_maxPed.moveToNext())
        {
            _resp = _maxPed.getLong(0) ;
        }

        return _resp;
    }

    public long MaxIdItemRota()
    {
        long _resp = 1;

        m_DataBase = m_dbEngine.getReadableDatabase();
        Cursor _maxPed = m_DataBase.rawQuery("select max(id) from item_rota", null);

        while (_maxPed.moveToNext())
        {
            _resp = _maxPed.getLong(0) ;
        }

        return _resp;
    }

    public void Atualizar(RotaTO value) {

        long _insertedId = 0;

        ContentValues _values = new ContentValues();

        _values.put("id", value.getId());
        _values.put("nome_origem", value.getNomeOrigem());
        _values.put("nome_destino", value.getNomeDestino());

        m_DataBase = m_dbEngine.getWritableDatabase();

        _insertedId = m_DataBase.update("rota", _values, "id = ?", new String[] {String.valueOf(value.getId())});

        /*long _maxItemID = MaxIdItemRota()+1;

        for (ItemRotaTO _item : value.getItensRota())
        {
            _item.setId(_maxItemID);
            salvarItemRota(value.getId(), _item);

            _maxItemID++;
        }*/

    }
}
