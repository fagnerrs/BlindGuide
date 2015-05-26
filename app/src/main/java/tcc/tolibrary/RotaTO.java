package tcc.tolibrary;

import java.util.ArrayList;

/**
 * Created by FAGNER on 28/03/2015.
 */
public class RotaTO {
    private long Id;
    private String NomeOrigem;
    private String NomeDestino;
    private ArrayList<ItemRotaTO> ItensRota;

    public RotaTO()
    {
        ItensRota = new ArrayList<>();
    }

    public String getNomeDestino() {
        return NomeDestino;
    }

    public void setNomeDestino(String nomeDestino) {
        NomeDestino = nomeDestino;
    }

    public String getNomeOrigem() {
        return NomeOrigem;
    }

    public void setNomeOrigem(String nomeOrigem) {
        NomeOrigem = nomeOrigem;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public ArrayList<ItemRotaTO> getItensRota() {
        return ItensRota;
    }

    public void SetItensRota(ArrayList<ItemRotaTO> itensRota)
    {
        ItensRota = itensRota;
    }

    public void AddItemRota(int angulo)
    {
        ItemRotaTO _itemRotaT0 = new ItemRotaTO();
        _itemRotaT0.setAngulo(angulo);
        _itemRotaT0.setPasso(ItensRota.size()+1);

        ItensRota.add(_itemRotaT0);
    }

    public void RemoverUltimoPasso() {

        if (ItensRota.size() > 0)
        {
            ItensRota.remove(ItensRota.size()-1);
        }
    }
}
