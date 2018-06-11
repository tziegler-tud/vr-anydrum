package Visualizations;

import Learning.ClusterableDoublePoint;
import com.example.bluefish.anydrum.MainActivity;
import com.example.bluefish.anydrum.R;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Vector;

public class LineChart {
    MainActivity refMain;
    GraphView graph;
    LineGraphSeries<DataPoint> series;

   public LineChart(MainActivity refMain)
    {
        this.refMain = refMain;
        graph = (GraphView) refMain.findViewById(R.id.graph);
        graph.removeAllSeries();

        DataPoint data[] = new DataPoint[200];
        for (int i=0; i<200; ++i) {
            data[i] = new DataPoint(0,0);
        }


        series = new LineGraphSeries<DataPoint>(data);
        graph.addSeries(series);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-1);
        graph.getViewport().setMaxY(1);
        graph.getViewport().setMaxX(100);
    }


   public void appendData(CircularFifoQueue<Float> buffer){

       this.series.resetData(generateData(buffer));

   }

    private DataPoint[] generateData(CircularFifoQueue<Float>buffer) {
        int count = buffer.size();
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            values[i] = new DataPoint(i, (double)buffer.get(i));
        }
        return values;
    }
}
