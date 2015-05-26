package tcc.dmlibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import tcc.database.DataBaseEngine;
import tcc.tolibrary.ConfiguracaoTO;

/**
 * Created by FAGNER on 28/03/2015.
 */
public class ConfiguracaoDM {
    public SQLiteDatabase m_DataBase = null;
    public DataBaseEngine m_dbEngine = null;

    public ConfiguracaoDM(Context context)
    {
        m_dbEngine = new DataBaseEngine(context);
    }


    public long Salvar(ConfiguracaoTO value) {

        this.DeletarTodos();

        long _insertedId = 0;

        ContentValues _values = new ContentValues();

        _values.put("passo_medio", value.getPassoMedio());


        m_DataBase = m_dbEngine.getWritableDatabase();

        _insertedId = m_DataBase.insert("configuracao", null, _values);

        return _insertedId;
    }


    public ConfiguracaoTO Busca()
    {
        ConfiguracaoTO _resp = null;


        String _querySQL = new StringBuilder().
                append(" select passo_medio ").
                append(" from configuracao ").toString();


        m_DataBase = m_dbEngine.getReadableDatabase();

        Cursor _cursorPedidos = m_DataBase.
                rawQuery(_querySQL, null);

        while (_cursorPedidos.moveToNext()) {

            _resp = new ConfiguracaoTO();
            _resp.setPassoMedio(_cursorPedidos.getDouble(0));

            break;
        }

        return _resp;
    }


    public void DeletarTodos()
    {
        m_DataBase = m_dbEngine.getWritableDatabase();
        m_DataBase.delete("configuracao", null , null);
    }
}
