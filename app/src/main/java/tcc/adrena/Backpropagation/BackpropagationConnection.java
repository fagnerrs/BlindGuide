package tcc.adrena.Backpropagation;

import tcc.adrena.Util.Randomizer;

public class BackpropagationConnection {

    public BackpropagationNeuron neuron;
    public double valueWeight; //{peso da liga��o}
    public double deltaWeight;

    public BackpropagationConnection(BackpropagationNeuron neuron)
    {
        this.neuron = neuron;
        this.valueWeight = (Randomizer.NextDouble(1)-0.5); //RANDON VALUE BETWEEN [-0.5 AND +0.5]
        this.deltaWeight = 0;
    }

}
