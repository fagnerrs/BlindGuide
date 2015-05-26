package tcc.adrena.Data;

public class DataSetObject {

	public double[] input, targetOutput;
	
	/*
     * Cria uma inst�ncia de um um novo dado de entrada nulo
     */
    public DataSetObject()
    {
        BuildNewDataSetObject(new double[0], new double[0]);
    }
    
	/*
     * Cria uma inst�ncia de um um novo dado de entrada
     */
    public DataSetObject(double[] input, double[] targetOutput)
    {
        BuildNewDataSetObject(input, targetOutput);
    }

    /*
     * Cria uma inst�ncia de um novo dado de entrada
     */
    public DataSetObject(double[] input)
    {
        BuildNewDataSetObject(input, new double[0]);
    }
    
    private void BuildNewDataSetObject(double[] input, double[] targetOutput)
    {
        this.SetInput(input);
        this.SetTargetOutput(targetOutput);
    }
    
    /*
     * Retorna o padr�o de entrada
     */
    public double[] GetInput()
    {
        return input;
    }

    /*
     * Retorna o padr�o desejado de sa�da
     */
    public double[] GetTargetOutput()
    {
        return targetOutput;
    }

    /*
     * Insere o padr�o de entrada
     */
    public void SetInput(double[] input)
    {
        this.input = input;
    }

    /*
     * Insere o padr�o desejado de sa�da
     */
    public void SetTargetOutput(double[] targetOutput)
    {
        this.targetOutput = targetOutput;
    }

    /*
     * Retorna o tamanho do padr�o de entrada
     */
    public int GetInputLenght()
    {
        return input.length;
    }

    /*
     * Retorna o tamanho do padr�o desejado de sa�da
     */
    public int GetTargetOutputLenght()
    {
        return targetOutput.length;
    }
}
