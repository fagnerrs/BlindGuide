package tcc.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by FAGNER on 28/03/2015
 */
public class DataBaseEngine extends SQLiteOpenHelper
{
    private static String m_DatabaseName = "blindguidebd.db";
    private static int m_Version = 1;

    public DataBaseEngine(Context context) {
        super(context, m_DatabaseName, null, m_Version);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteTables(db);
        createTables(db);
    }

    public void createTables(SQLiteDatabase db)
    {

        db.execSQL(new StringBuilder().append("create table configuracao")
                .append(" ( passo_medio numeric ) ")
                .toString());


        db.execSQL(new StringBuilder().append("create table rota")
                .append(" ( id numeric, ")
                .append(" nome_origem text, ")
                .append(" nome_destino text )")
                .toString());


        db.execSQL(new StringBuilder().append("create table item_rota")
                .append(" ( id numeric,  ")
                .append(" rota_id numeric,  ")
                .append(" passo integer, ")
                .append(" angulo numeric ) ")
                .toString());


        db.execSQL(new StringBuilder().append("create table ponto_interesse")
                .append(" ( id numeric,  ")
                .append(" item_rota_id numeric,  ")
                .append(" rota_id numeric, ")
                .append(" descricao text ) ")
                .toString());


    }

    public void deleteTables(SQLiteDatabase db)
    {
        db.execSQL(new StringBuilder().append(" drop table if exists configuracao ")
                .toString());

        db.execSQL(new StringBuilder().append(" drop table if exists rota ")
                .toString());

        db.execSQL(new StringBuilder().append(" drop table if exists item_rota ")
                .toString());

        db.execSQL(new StringBuilder().append(" drop table if exists ponto_interesse ")
                .toString());
    }

}
