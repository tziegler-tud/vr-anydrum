package Learning;
import  org.apache.commons.math3.ml.clustering.Clusterable;

public class ClusterableFloat implements org.apache.commons.math3.ml.clustering.Clusterable{
    private double[] values;

    public ClusterableFloat(float[] values){
       // this.values = new double[];
    }

    @Override
    public double[] getPoint() {
        //double[] result = {0.0, value};
        double[] result = new double[2];
        return result;
    }
}
