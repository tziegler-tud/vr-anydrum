package Learning;
import  org.apache.commons.math3.ml.clustering.Clusterable;

public class ClusterableDoublePoint implements org.apache.commons.math3.ml.clustering.Clusterable{
    private double[] values;

    public ClusterableDoublePoint(double[] values, int length){

        this.values = new double[length];
        for(int i=0;i<length;i++){
            this.values[i] = values[i];
        }
    }

    @Override
    public double[] getPoint() {
        return values;
    }
}
