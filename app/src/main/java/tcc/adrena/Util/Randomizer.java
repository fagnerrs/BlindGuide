package tcc.adrena.Util;

import java.util.Random;

public class Randomizer {

	private Randomizer(){}
	
	private static Random rnd = new Random();

    /*
     * Retorna um valor double aleat�rio
     */
    public static double NextDouble()
    {
        return rnd.nextDouble();
    }

    /*
     * Retorna um valor double aleat�rio de 0 at� determinado n�mero
     */
    public static double NextDouble(int max)
    {
        return rnd.nextDouble() * max;
    }
}
