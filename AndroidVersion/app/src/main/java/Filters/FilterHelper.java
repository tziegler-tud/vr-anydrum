package Filters;

import Learning.ClusterableDoublePoint;
import android.widget.TextView;
import com.example.bluefish.anydrum.MainActivity;
import com.example.bluefish.anydrum.R;
import org.apache.commons.math3.genetics.Fitness;

import java.text.DecimalFormat;
import java.util.Vector;

public class FilterHelper {

    private DecimalFormat formatter;
    Vector<ClusterableDoublePoint> listOfSensorData, listOfSensorDataFiltered;
    public Vector<ClusterableDoublePoint> getListOfSensorData() {
        return listOfSensorData;
    }
    public Vector<ClusterableDoublePoint> getListOfSensorDataFiltered() {return listOfSensorDataFiltered;}



    public FilterHelper(){
        listOfSensorData =new Vector<ClusterableDoublePoint>();
        listOfSensorDataFiltered = new Vector<ClusterableDoublePoint>();
        formatter  = new DecimalFormat("#,###,###.###");
    }


    //Box-Filter-Kernel size 5
    public double blockFilter(float size, double stdDeviationValue, Vector<ClusterableDoublePoint> listOfSensorData,  MainActivity refMain)
    {
//        this.listOfSensorData.clear();
//        this.listOfSensorDataFiltered.clear();

        if(listOfSensorData.size() < size)
            size = listOfSensorData.size();
        float weight = 1.0f/size;
        float filteredValue =0;

        for(int i=1; i<=size; i++)
        {
            if((listOfSensorData.size()-i) >= 0) {
                filteredValue +=  weight* listOfSensorData.get(listOfSensorData.size() - i).getPoint()[1];
            }
        }
        filteredValue /= size;

        double highpassFilteredY = highpassFilter(stdDeviationValue/10.0, filteredValue); //stdDeviationValue around 0.0002f
//        TextView viewCurrentValue = (TextView) refMain.findViewById(R.id.sensorValue);
//        viewCurrentValue.setText(formatter.format(highpassFilteredY));
        this.listOfSensorDataFiltered.add(new ClusterableDoublePoint(new double[]{listOfSensorData.get(listOfSensorData.size()-1).getPoint()[0], highpassFilteredY}, 2));

        this.listOfSensorData = listOfSensorData;
        return highpassFilteredY;
    }

    public static double highpassFilter(double threshold, double skalar)
    {
        if(skalar < threshold )//&& (skalar >-threshold))
            return 0;
        return skalar;
    }
}
