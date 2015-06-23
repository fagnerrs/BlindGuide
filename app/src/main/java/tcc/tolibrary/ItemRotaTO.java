package tcc.tolibrary;

import tcc.tolibrary.PlantaBaixa.AmbienteTO;

/**
 * Created by FAGNER on 28/03/2015.
 */
public class ItemRotaTO {
    private long Id;
    private long RotaID;
    private Integer Passo;
    private Integer Angulo;
    private AmbienteTO Ambiente;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public long getRotaID() {
        return RotaID;
    }

    public void setRotaID(long rotaID) {
        RotaID = rotaID;
    }

    public Integer getPasso() {
        return Passo;
    }

    public void setPasso(Integer passo) {
        Passo = passo;
    }

    public Integer getAngulo() {
        return Angulo;
    }

    public void setAngulo(Integer angulo) {
        Angulo = angulo;
    }

    public AmbienteTO getAmbiente() {
        return Ambiente;
    }

    public void setAmbiente(AmbienteTO ambiente) {
        Ambiente = ambiente;
    }
}
