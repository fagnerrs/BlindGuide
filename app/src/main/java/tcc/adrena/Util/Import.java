package tcc.adrena.Util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import tcc.adrena.Backpropagation.BackpropagationLayer;
import tcc.adrena.Kohonen.KohonenNeuron;
import tcc.adrena.NeuralNetwork.Backpropagation;
import tcc.adrena.NeuralNetwork.INeuralNetwork;
import tcc.adrena.NeuralNetwork.Kohonen;

public class Import {
	
	/*
     * Recupera um conjunto de padr�es para treinamento
     */
     public static tcc.adrena.Data.DataSet DataSet(String file) throws FileNotFoundException
     {
    	 Gson gson = new Gson();
		 BufferedReader br = new BufferedReader(new FileReader(file));
		 return gson.fromJson(br, tcc.adrena.Data.DataSet.class);
     }

    public static void KnowledgeBase(INeuralNetwork net, InputStream file) throws Exception
    {
        Gson gson = new Gson();

        if (net instanceof Kohonen)
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(file));
            ((Kohonen)net).SetCompetitiveLayer(gson.fromJson(br, KohonenNeuron[][].class));
        }
        else //Backpropagation
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(file));
            //BufferedReader br = new BufferedReader(new FileReader(file));
            java.lang.reflect.Type listOfLayersObject = new TypeToken<ArrayList<BackpropagationLayer>>(){}.getType();
            ArrayList<BackpropagationLayer> list = gson.fromJson(br, listOfLayersObject);
            ((Backpropagation)net).SetLayers(list);
        }
    }

     /*
      * Recupera a base de conhecimento de uma RNA a partir de um arquivo
      */
     public static void KnowledgeBase(INeuralNetwork net, String file) throws Exception
     {
    	 Gson gson = new Gson();

    	 if (net instanceof Kohonen)
    	 {
    		 BufferedReader br = new BufferedReader(new FileReader(file));
    		 ((Kohonen)net).SetCompetitiveLayer(gson.fromJson(br, KohonenNeuron[][].class));
    	 }
    	 else //Backpropagation
    	 {
    		 BufferedReader br = new BufferedReader(new FileReader(file));
    		 java.lang.reflect.Type listOfLayersObject = new TypeToken<ArrayList<BackpropagationLayer>>(){}.getType();
    		 ArrayList<BackpropagationLayer> list = gson.fromJson(br, listOfLayersObject);
    		 ((Backpropagation)net).SetLayers(list);
    	 }
     }

     /*
      * Recupera a estrutura de uma RNA a partir de um arquivo
      */
     public static INeuralNetwork NeuralNetworkStructure(String file) throws Exception
     {
    	 Gson gson = new Gson();
    	 BufferedReader br = new BufferedReader(new FileReader(file));
    	 CommonStructure stru = gson.fromJson(br,CommonStructure.class);
    	 INeuralNetwork ann = null;
    	 switch(stru.type)
    	 {
	    	 case Backpropagation:
	    		 Integer[] Iarray = stru.hiddenLayerSizes;
	    		 int[] iarray = new int[Iarray.length];;
	    		 for(int x = 0; x < Iarray.length; x++)
	    		 {
	    			 iarray[x] = Iarray[x].intValue();
	    		 }
	    		 ann = new Backpropagation(stru.inputLayerSize.intValue(), stru.outputLayerSize.intValue(), iarray);
	             ((Backpropagation)ann).SetErrorRate(stru.error.doubleValue());
	             ((Backpropagation)ann).SetMaxIterationNumber(stru.iterationNumber.intValue());
	             ((Backpropagation)ann).SetLearningRate(stru.learningRate.doubleValue());
	    		 break;
	    	 case Kohonen:
	    		 ann = new Kohonen(stru.inputLayerSize.intValue(), stru.competitiveNeuronLength.intValue(), stru.maximumWeightRange.intValue());
	             ((Kohonen)ann).SetIterationNumber(stru.iterationNumber.intValue());
	             ((Kohonen)ann).SetLearningRate(stru.learningRate.doubleValue());
	             ((Kohonen)ann).SetNeighborhoodRadius(stru.neighborhoodRadius.intValue());
	    		 break;
    	 }

         return ann;
     }

//     private static String ReadFile(String file) throws Exception
//     {
//    	 String json = "";
//    	 BufferedReader reader = new BufferedReader(new FileReader(file));
//    	 String line = null;
//    	 while ((line = reader.readLine()) != null) {
//    	     json += line;
//    	 }
//    	 return json;
//     }
	
}
