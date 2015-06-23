package tcc.dmlibrary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import tcc.database.DataBaseEngine;
import tcc.tolibrary.PlantaBaixa.AmbienteTO;
import tcc.tolibrary.PlantaBaixa.PlantaBaixaTO;

/**
 * Created by FAGNER on 16/06/2015.
 */
public class PlantaBaixaDM {

    public SQLiteDatabase m_DataBase = null;
    public DataBaseEngine m_dbEngine = null;

    public PlantaBaixaDM(Context context)
    {
        m_dbEngine = new DataBaseEngine(context);
    }

    public ArrayList<PlantaBaixaTO> BuscaPlantasBaixas()
    {
        ArrayList<PlantaBaixaTO> _resp = new ArrayList<>();

        String _querySQL = new StringBuilder().
                append(" select id, ").
                append(" descricao ").
                append(" from planta_baixa ").toString();


        m_DataBase = m_dbEngine.getReadableDatabase();

        Cursor _cursorPedidos = m_DataBase.
                rawQuery(_querySQL, null);

        while (_cursorPedidos.moveToNext()) {

            PlantaBaixaTO _plantaBaixaTO = new PlantaBaixaTO();

            _plantaBaixaTO.setID(_cursorPedidos.getLong(0));
            _plantaBaixaTO.setDescricao(_cursorPedidos.getString(1));

            _plantaBaixaTO.setAmbientes(BuscaAmbientes(_plantaBaixaTO.getID()));

           _resp.add(_plantaBaixaTO);
        }


        m_DataBase.close();

        return _resp;
    }

    private ArrayList<AmbienteTO> BuscaAmbientes(long plantaBaixaID) {

        ArrayList<AmbienteTO> _resp = new ArrayList<>();

        String _querySQL = new StringBuilder().
                append(" select id, ").
                append(" descricao ").
                append(" from ambiente ").
                append(" where planta_baixa_id = " + String.valueOf(plantaBaixaID)).toString();

        m_DataBase = m_dbEngine.getReadableDatabase();

        Cursor _cursorPedidos = m_DataBase.
                rawQuery(_querySQL, null);

        while (_cursorPedidos.moveToNext()) {

            AmbienteTO _plantaBaixaTO = new AmbienteTO(_cursorPedidos.getLong(0));

            _plantaBaixaTO.setDescricao(_cursorPedidos.getString(1));

            _resp.add(_plantaBaixaTO);
        }


        m_DataBase.close();

        return _resp;
    }

}
