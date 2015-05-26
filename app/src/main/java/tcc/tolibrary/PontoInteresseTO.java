package tcc.tolibrary;

/**
 * Created by FAGNER on 28/03/2015.
 */
public class PontoInteresseTO {

    private long Id;
    private long RotaId;
    private long ItemRotaId;
    private String Descricao;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public long getRotaId() {
        return RotaId;
    }

    public void setRotaId(long rotaId) {
        RotaId = rotaId;
    }

    public long getItemRotaId() {
        return ItemRotaId;
    }

    public void setItemRotaId(long itemRotaId) {
        ItemRotaId = itemRotaId;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }
}
