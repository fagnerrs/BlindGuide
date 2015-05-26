package tcc.rnapedometer;

/**
 * Created by FAGNER on 03/04/2015.
 */
public class SensorMotion {

    private Integer ID;
    private float xAxis;
    private float yAxis;
    private float zAxis;

    public SensorMotion(Integer id, float xAxis, float yAxis, float zAxis)
    {
        ID = id;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.zAxis = zAxis;
    }

    public float getzAxis() {
        return zAxis;
    }

    public void setzAxis(float zAxis) {
        this.zAxis = zAxis;
    }

    public float getyAxis() {
        return yAxis;
    }

    public void setyAxis(float yAxis) {
        this.yAxis = yAxis;
    }

    public float getxAxis() {
        return xAxis;
    }

    public void setxAxis(float xAxis) {
        this.xAxis = xAxis;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }
}
