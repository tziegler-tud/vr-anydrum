package Visualizations;

import android.graphics.Color;
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
    LineGraphSeries<DataPoint> seriesL;
    LineGraphSeries<DataPoint> seriesR;

   public LineChart(AppCompatActivity refMain, GraphView graphView)
    {
        this.refMain = refMain;
        this.graph = graphView;
        graph.removeAllSeries();

        DataPoint data[] = new DataPoint[200];
        for (int i=0; i<200; ++i) {
            data[i] = new DataPoint(0,0);
        }


        seriesL = new LineGraphSeries<DataPoint>(data);

        seriesR = new LineGraphSeries<DataPoint>(data);
        seriesR.setColor(Color.GREEN);
        graph.addSeries(seriesL);

        //graph.addSeries(seriesR);

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
    public void setScalingY(){
        graph.getViewport().setYAxisBoundsManual(false);
        graph.getViewport().setScalableY(true);
    }
    public void setScalingX(){
        graph.getViewport().setXAxisBoundsManual(false);
        graph.getViewport().setYAxisBoundsManual(false);
    }
    public void setManualX(int minX, int maxX){

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(minX);
        graph.getViewport().setMaxX(maxX);
    }
    public void setManualY(int minY, int maxY){
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(minY);
        graph.getViewport().setMaxY(maxY);
    }


    public void setManual(int minY, int maxY, int minX, int maxX){
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(minY);
        graph.getViewport().setMaxY(maxY);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(minX);
        graph.getViewport().setMaxX(maxX);
    }
   public void appendData(CircularFifoQueue<Double> bufferL,CircularFifoQueue<Double> bufferR){

       this.seriesL.resetData(generateData(bufferL));
       this.seriesL.resetData(generateData(bufferL));
       this.seriesR.resetData(generateData(bufferR));
//       this.seriesL.appendData(generateDataPoint(bufferL.get(bufferL.size()-1)),true,200);
//       this.seriesR.appendData(generateDataPoint(bufferR.get(bufferR.size()-1)),true,200);

   }
    public void appendData(double[] data){

        this.seriesL.resetData(generateData(data));


    }



    private DataPoint[] generateData(CircularFifoQueue<Double>buffer) {
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

    private DataPoint generateDataPoint(int data){
       return new DataPoint(200,data);

    }
}
