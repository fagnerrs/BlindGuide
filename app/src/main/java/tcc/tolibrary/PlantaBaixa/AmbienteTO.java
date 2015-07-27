package tcc.tolibrary.PlantaBaixa;

/**
 * Created by FAGNER on 15/06/2015.
 */
public class AmbienteTO {

    private long ID;
    private String Descricao;

    public AmbienteTO(long id)
    {
        this.setID(id);
    }

    public AmbienteTO(long id, String descricao)
    {
        this.setID(id);
        this.setDescricao(descricao);
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    @Override
    public String toString() {
        return this.getDescricao();
    }
}
