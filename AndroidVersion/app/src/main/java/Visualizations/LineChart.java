package Visualizations;

import android.support.v7.app.AppCompatActivity;
import com.example.bluefish.anydrum.MainActivity;
import com.example.bluefish.anydrum.FFTActivity;
import com.example.bluefish.anydrum.R;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class LineChart {
    AppCompatActivity refMain;
    GraphView graph;
    LineGraphSeries<DataPoint> series;

   public LineChart(AppCompatActivity refMain, GraphView graphView)
    {
        this.refMain = refMain;
        this.graph = graphView;
        graph.removeAllSeries();

        DataPoint data[] = new DataPoint[200];
        for (int i=0; i<200; ++i) {
            data[i] = new DataPoint(0,0);
        }


        series = new LineGraphSeries<DataPoint>(data);
        graph.addSeries(series);

    }

    public void setMaxX(int x){
       this.graph.getViewport().setMaxX(x);
    }
    public void setMinX(int x){
        this.graph.getViewport().setMinX(x);
    }
    public void setMaxY(int y){
        this.graph.getViewport().setMaxY(y);
    }

    public void setYAxisBoundsManual(boolean b){
        graph.getViewport().setYAxisBoundsManual(b);
    }
    public void setXAxisBoundsManual(boolean b){
        graph.getViewport().setXAxisBoundsManual(b);
    }

    public void setScaling(){
        graph.getViewport().setXAxisBoundsManual(false);
        graph.getViewport().setYAxisBoundsManual(false);
        graph.getViewport().setScalableY(true);
    }

    public void setManual(int minY, int maxY, int minX, int maxX){
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(minY);
        graph.getViewport().setMaxY(maxY);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(minX);
        graph.getViewport().setMaxX(maxX);
    }
   public void appendData(CircularFifoQueue<Float> buffer){

       this.series.resetData(generateData(buffer));

   }

   public void appendData(double[] data){
       this.series.resetData(generateData(data));
   }

    private DataPoint[] generateData(CircularFifoQueue<Float>buffer) {
        int count = buffer.size();
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            values[i] = new DataPoint(i, (double)buffer.get(i));
        }
        return values;
    }

    private DataPoint[] generateData(double[] data){
        int count = data.length;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            values[i] = new DataPoint(i, data[i]);
        }
        return values;
    }
}
