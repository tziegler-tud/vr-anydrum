package Visualizations;

import Learning.ClusterableDoublePoint;
import com.example.bluefish.anydrum.MainActivity;
import com.example.bluefish.anydrum.R;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.Vector;

public class LineChart {
    MainActivity refMain;
    GraphView graph;
    LineGraphSeries<DataPoint> series;

    public LineChart(MainActivity refMain, Vector<ClusterableDoublePoint> listOfSensorData)
    {
        this.refMain = refMain;
        graph = (GraphView) refMain.findViewById(R.id.graph);
        graph.removeAllSeries();

        DataPoint data[] = new DataPoint[100];
        for (int i=0; i<100; ++i) {
            data[i] = new DataPoint(0,0);
        }
        for(int i=0; i<listOfSensorData.size(); ++i)
        {
            if(i>=100 || i >=listOfSensorData.size())
                break;
            data[i] = new DataPoint(i,  listOfSensorData.get((listOfSensorData.size()-1-i)).getPoint()[1]);//get newest sensordata from back
        }

        series = new LineGraphSeries<DataPoint>(data);
        graph.addSeries(series);
//
//        graph.getViewport().setMinY(-0.001);
//        graph.getViewport().setMaxY(0.001);
          graph.getViewport().setMinX(0);
          graph.getViewport().setMaxX(100);
          graph.getViewport().setScalableY(true);
    }
}
