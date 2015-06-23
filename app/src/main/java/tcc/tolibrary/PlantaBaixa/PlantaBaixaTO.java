package tcc.tolibrary.PlantaBaixa;

import java.util.ArrayList;

/**
 * Created by FAGNER on 15/06/2015.
 */
public class PlantaBaixaTO {

    private long ID;
    private ArrayList<AmbienteTO> Ambientes;
    private String Descricao;

    public PlantaBaixaTO()
    {
        this.Ambientes = new ArrayList<>();
    }

    public ArrayList<AmbienteTO> getAmbientes() {
        return Ambientes;
    }

    public void setAmbientes(ArrayList<AmbienteTO> ambientes) {
        Ambientes = ambientes;
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
