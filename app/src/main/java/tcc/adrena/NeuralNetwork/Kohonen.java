package tcc.adrena.NeuralNetwork;

import java.util.ArrayList;

import tcc.adrena.Data.*;
import tcc.adrena.Kohonen.*;

public class Kohonen implements INeuralNetwork {

	 public int inputLayerSize, competitiveNeuronLength, iterationNumber, neighborhoodRadius, maximumWeightRange;
	 public double learningRate;
	 public KohonenNeuron[][] competitiveLayer;

     /*
      * M�todo de aprendizado da Rede
      */
     public void Learn(DataSet trainingSet) throws Exception
     {
         if (trainingSet.GetOutputSize() != 0 || trainingSet.GetInputSize() != this.inputLayerSize)
             throw new Exception("Incorrect data format!");
         else
         {
             ArrayList<DataSetObject> dataset = trainingSet.GetList();

             //Para cada itera��o
             for (int itN = 0; itN < iterationNumber; itN++)
             {
                 //Para cada entrada
                 for (int inputN = 0; inputN < trainingSet.Length(); inputN++)
                 {
                     //Calcula neur�nio vencedor
                     KohonenNeuron neuro = CalculateWinnerNeuron(dataset.get(inputN));

                     //Atualiza pesos do neur�nio vencedor e de seus vizinhos
                     ArrayList<KohonenNeuron> winners = GetNeighbors(neuro);
                     for (int x = 0; x < winners.size(); x++)
                         winners.get(x).UpdateWeights(dataset.get(inputN), learningRate);
                 }
             }
         }
     }

     /*
      * M�todo de aprendizado da Rede, enviando alguns par�metros de configura��o do aprendizado
      */
     public void Learn(DataSet trainingSet, int iterationNumber, int learningRate, int neighborhoodRadius) throws Exception
     {
         this.iterationNumber = iterationNumber;
         this.learningRate = learningRate;
         this.neighborhoodRadius = neighborhoodRadius;
         Learn(trainingSet);
     }

     public double[] Recognize(double[] input) throws Exception
     {
         if (input.length != this.inputLayerSize)
             throw new Exception("Incorrect data format!");
         else
         {
             DataSetObject obj = new DataSetObject(input);
             //Calcula neur�nio vencedor
             return CalculateWinnerNeuron(obj).GetWeights();
         }
     }

     public KohonenNeuron RecognizeWinnerNeuron(double[] input) throws Exception
     {
         if (input.length != this.inputLayerSize)
             throw new Exception("Incorrect data format!");
         else
         {
             DataSetObject obj = new DataSetObject(input);
             //Calcula neur�nio vencedor
             return CalculateWinnerNeuron(obj);
         }
     }
     
     /*
      * Cria uma inst�ncia de uma rede Kohonen
      */
     public Kohonen(int inputLayerSize, int competitiveNeuronLength)
     {
         BuildNewKohonen(inputLayerSize, competitiveNeuronLength, competitiveNeuronLength);
     }

     /*
      * Cria uma inst�ncia de uma rede Kohonen
      */
     public Kohonen(int inputLayerSize, int competitiveNeuronLength, int maximumWeightRange)
     {
         BuildNewKohonen(inputLayerSize, competitiveNeuronLength, maximumWeightRange);
     }

     /*
      * Retorna o intervalo de peso m�ximo escolhido na instancia��o da rede
      */
     public int GetMaximumWeightRange()
     {
         return this.maximumWeightRange;
     }
     
     /*
      * Retorna todos os neur�nios da camada competitiva
      */
     public KohonenNeuron[][]  GetCompetitiveLayer()
     {
         return this.competitiveLayer;
     }

     /*
      * Define todos os neur�nios da camada competitiva
      */
     public void SetCompetitiveLayer(KohonenNeuron[][] cLayers) throws Exception
     {
         if (cLayers.length  == this.competitiveLayer.length)
             this.competitiveLayer = cLayers;
         else
             throw new Exception("Incorrect data format!");
     }
     
     /*
      * Retorna o tamanho da camada competitiva (onde NxN)
      */
     public int GetCompetitiveLayerLength()
     {
         return this.competitiveNeuronLength;
     }

     /*
      * Retorna o tamanho da camada de entrada
      */
     public int GetInputLayerSize()
     {
         return this.inputLayerSize;
     }

     /*
      * Define o fator de aprendizagem
      */
     public void SetLearningRate(double rate)
     {
         learningRate = rate;
     }

     /*
      * Retorna o fator de aprendizagem
      */
     public double GetLearningRate()
     {
         return learningRate;
     }

     /*
      * Define o raio da vizinhan�a
      */
     public void SetNeighborhoodRadius(int radius)
     {
         neighborhoodRadius = radius;
     }

     /*
      * Retorna o raio da vizinhan�a
      */
     public int GetNeighborhoodRadius()
     {
         return neighborhoodRadius;
     }

     /*
      * Define o n�mero de itera��es
      */
     public void SetIterationNumber(int number)
     {
         iterationNumber = number;
     }

     /*
      * Retorna o n�mero de itera��es
      */
     public int GetIterationNumber()
     {
         return iterationNumber;
     }

     private void BuildNewKohonen(int inputLayerSize, int competitiveNeuronLength, int maximumWeightRange)
     {
    	 this.maximumWeightRange = maximumWeightRange;
         iterationNumber = 20 * inputLayerSize * competitiveNeuronLength; 
         learningRate = 0.2; 
         neighborhoodRadius = 1;

         this.inputLayerSize = inputLayerSize;
         this.competitiveNeuronLength = competitiveNeuronLength;
         competitiveLayer = new KohonenNeuron[competitiveNeuronLength][competitiveNeuronLength];
         for (int i = 0; i < competitiveNeuronLength; i++)
             for (int j = 0; j < competitiveNeuronLength; j++)
                 (competitiveLayer[i][j] = new KohonenNeuron(inputLayerSize, i, j)).RandomizeWeights(maximumWeightRange);
     }

     /*
      * Dado um padr�o de entrada, calcula o neur�nio vencedor
      */
     private KohonenNeuron CalculateWinnerNeuron(DataSetObject pattern) throws Exception
     {
         double smallestDist = -1;
         KohonenNeuron winner = new KohonenNeuron(inputLayerSize);

         //Para cada neur�nio da camada competitiva
         for(int i = 0; i < competitiveNeuronLength; i ++)
         {
             for(int j = 0; j < competitiveNeuronLength; j ++)
             {
                 KohonenNeuron neuron = competitiveLayer[i][j];

                 //Calcula a dist�ncia euclidiana
                 double dist = neuron.GetEuclideanDistance(pattern);
                 //Primeiro neur�nio sempre � o primeiro vencedor
                 if (smallestDist == -1)
                 {
                     smallestDist = dist;
                     winner = neuron;
                 }
                 //Se a dist�ncia for a menor que a dist�ncia do �ltimo vencedor, temos um novo vencedor 
                 if (dist < smallestDist)
                 {
                     smallestDist = dist;
                     winner = neuron;
                 }
             }
         }

         return winner;
     }

     /*
      * Retorna todos os neur�nios no raio de vizinhan�a do vencedor
      */
     private ArrayList<KohonenNeuron> GetNeighbors(KohonenNeuron neuron)
     {
    	 ArrayList<KohonenNeuron> list = new ArrayList<KohonenNeuron>();

         int[] index = FindIndex(neuron);

         int X = index[0];
         int Y = index[1];

         for (int i = X - neighborhoodRadius; i <= X + neighborhoodRadius; i++)
         {
             for (int j = Y - neighborhoodRadius; j <= Y + neighborhoodRadius; j++)
             {
                 if (i < competitiveNeuronLength && i >= 0 && j < competitiveNeuronLength && j >= 0)
                 {
                     list.add(competitiveLayer[i][j]);
                 }
             }
         }

         return list;
     }

     private int[] FindIndex(KohonenNeuron neuron)
     {
         int[] IJ = new int[2];
         for (int i = 0; i < competitiveLayer.length; i++)
         {
             for (int j = 0; j < competitiveLayer[i].length; j++)
             {
                 if (neuron == competitiveLayer[i][j])
                 {
                     IJ[0] = i;
                     IJ[1] = j;
                 }
             }
         }
         return IJ;
     }

}
