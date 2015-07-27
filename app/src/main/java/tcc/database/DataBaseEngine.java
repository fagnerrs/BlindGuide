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
    private static int m_Version = 10;

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

        db.execSQL(new StringBuilder().append("create table planta_baixa")
                .append(" ( id numeric, ")
                .append("  descricao text ) ")
                .toString());

        db.execSQL(new StringBuilder().append("create table ambiente")
                .append(" ( id numeric, ")
                .append(" planta_baixa_id numeric, ")
                .append("  descricao text, ")
                .append("  observacoes text ) ")
                .toString());


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
                .append(" ambiente_id numeric, ")
                .append(" angulo numeric,  ")
                .append(" observacao text ) ")
                .toString());


        db.execSQL(new StringBuilder().append("create table ponto_interesse")
                .append(" ( id numeric,  ")
                .append(" item_rota_id numeric,  ")
                .append(" rota_id numeric, ")
                .append(" descricao text ) ")
                .toString());

        insertPlantaBaixa(db);

    }

    private void insertPlantaBaixa(SQLiteDatabase db)
    {
        db.execSQL(" insert into planta_baixa (id, descricao) values (1, 'Laboratório de Informática - UNISC')");
        db.execSQL(" insert into ambiente (id, planta_baixa_id, descricao) values (1, 1, 'Entrada Principal')");
        db.execSQL(" insert into ambiente (id, planta_baixa_id, descricao) values (2, 1, 'Laboratório 6')");
        db.execSQL(" insert into ambiente (id, planta_baixa_id, descricao) values (3, 1, 'Laboratório 7')");
        db.execSQL(" insert into ambiente (id, planta_baixa_id, descricao) values (4, 1, 'Laboratório 8')");

        db.execSQL(" insert into planta_baixa (id, descricao) values (2, 'Fagner - Casa')");
        db.execSQL(" insert into ambiente (id, planta_baixa_id, descricao) values (5, 2, 'Sala')");
        db.execSQL(" insert into ambiente (id, planta_baixa_id, descricao) values (6, 2, 'Quarto')");
        db.execSQL(" insert into ambiente (id, planta_baixa_id, descricao) values (7, 2, 'Garagem')");
        db.execSQL(" insert into ambiente (id, planta_baixa_id, descricao) values (8, 2, 'Lavanderia')");


    }

    public void deleteTables(SQLiteDatabase db)
    {
        db.execSQL(new StringBuilder().append(" drop table if exists planta_baixa ")
                .toString());

        db.execSQL(new StringBuilder().append(" drop table if exists ambiente ")
                .toString());

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
