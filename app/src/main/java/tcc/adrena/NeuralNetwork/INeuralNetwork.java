package tcc.adrena.NeuralNetwork;

import tcc.adrena.Data.DataSet;

public interface INeuralNetwork
{
   void Learn(DataSet trainingSet) throws Exception;
   double[] Recognize(double[] input) throws Exception;
}
