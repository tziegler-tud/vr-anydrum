package Learning;

import com.example.bluefish.anydrum.EnumDrum;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import  org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.puredata.core.PdBase;


import java.util.List;

import static org.puredata.core.PdBase.sendBang;

public class DBSCAN {

    private DBSCANClusterer clusterer;
    private DescriptiveStatistics statHihat;
    private DescriptiveStatistics statSnare;
    private DescriptiveStatistics statBass;


    public List<ClusterableDoublePoint> points;
    private List<Cluster<ClusterableDoublePoint>> clusterHihat;
    private List<Cluster<ClusterableDoublePoint>> clusterSnare;
    private List<Cluster<ClusterableDoublePoint>> clusterBass;


    public DBSCAN(float radius, int minObjectNumber)
    {
        clusterer = new DBSCANClusterer(radius, minObjectNumber);//0.00005f, 1)
        statHihat = new DescriptiveStatistics();
        statSnare = new DescriptiveStatistics();
        statBass = new DescriptiveStatistics();
    }

    public List<Cluster<ClusterableDoublePoint>> cluster(List<ClusterableDoublePoint> points)
    {
       return clusterer.cluster(points);
    }

    public EnumDrum evaluatePoint(ClusterableDoublePoint point)
    {
        EnumDrum drumSound = EnumDrum.NONE;
        double distanceHihat = Double.MAX_VALUE;
        double distanceSnare = Double.MAX_VALUE;
        double distanceBass = Double.MAX_VALUE;


        //is part of hihat?
        double medianHihat = statHihat.getPercentile(50.0);
        distanceHihat = medianHihat -point.getPoint()[1];
        if(distanceHihat < 0) distanceHihat *= -1;
        drumSound = drumSound.HIHAT;

        //is part of snare?
        double medianSnare = statSnare.getPercentile(50.0);
        distanceSnare = medianSnare -point.getPoint()[1];
        if(distanceSnare < 0) distanceSnare *= -1;
        if(distanceSnare < distanceHihat)
            drumSound = EnumDrum.SNARE;

        //is part of drum?
        double medianBass = statBass.getPercentile(50.0);
        distanceBass = medianBass -point.getPoint()[1];
        if(distanceBass < 0) distanceBass *= -1;
        if(distanceBass < distanceSnare || distanceBass < distanceHihat)
            drumSound = EnumDrum.BASS;


        playSound(drumSound);

        return drumSound;
    }

    private void playSound(EnumDrum  drumsound)
    {
        //playsound
        switch (drumsound)
        {
            case HIHAT:
                PdBase.sendBang("bangHiHatDrum");
                break;
            case SNARE:
                PdBase.sendBang("bangSnareDrum");
                break;
            case BASS:
                PdBase.sendBang("bangBassDrum");
                break;
        }
    }

    public void setClusterHihat(List<Cluster<ClusterableDoublePoint>> clusterHihat) {
        this.clusterHihat = clusterHihat;
        addPointsToStat(statHihat, clusterHihat );
    }


    public void setClusterSnare(List<Cluster<ClusterableDoublePoint>> clusterSnare) {
        this.clusterSnare = clusterSnare;
        addPointsToStat(statSnare, clusterSnare );
    }


    public void setClusterBass(List<Cluster<ClusterableDoublePoint>> clusterBass) {
        this.clusterBass = clusterBass;
        addPointsToStat(statBass, clusterBass );
    }

    private void addPointsToStat(DescriptiveStatistics stat, List<Cluster<ClusterableDoublePoint>> clusters)
    {
        for(int i=0; i<clusters.size(); ++i)
        {
            Cluster cluster = clusters.get(i);
            for(int j=0; j<cluster.getPoints().size(); ++j)
            {
                ClusterableDoublePoint point = (ClusterableDoublePoint) cluster.getPoints().get(j);
                if(point.getPoint()[1] >= clusterer.getEps())
                     stat.addValue(point.getPoint()[1]);
            }
        }
    }
}
