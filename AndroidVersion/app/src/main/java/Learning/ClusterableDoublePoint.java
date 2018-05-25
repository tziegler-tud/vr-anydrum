package Learning;
import  org.apache.commons.math3.ml.clustering.Clusterable;

public class ClusterableDoublePoint implements org.apache.commons.math3.ml.clustering.Clusterable{
    private double[] values;

    public ClusterableDoublePoint(double[] values){
        this.values = new double[]{values[0], values[1]};
    }

    @Override
    public double[] getPoint() {
        return values;
    }
}
