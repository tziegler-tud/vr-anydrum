package Learning;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

import java.util.List;

public class DBSCAN {

    private DBSCANClusterer clusterer;

    public DBSCAN(float radius, int minObjectNumber)
    {
        clusterer = new DBSCANClusterer(radius, minObjectNumber);//0.00005f, 1)
    }

    public List<Cluster<ClusterableDoublePoint>> cluster(List<ClusterableDoublePoint> points)
    {
       return clusterer.cluster(points);
    }
}
