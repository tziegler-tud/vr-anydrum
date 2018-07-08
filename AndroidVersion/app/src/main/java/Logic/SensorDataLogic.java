package Logic;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.NoSuchElementException;

public class SensorDataLogic {

    private double stdDev;
    private double variance;
    private double mean;

    public SensorDataLogic(double[] statistics){

        this.stdDev = statistics[0];
        this.variance = statistics[1];
        this.mean = statistics[2];


    }

    public boolean detectKnocks(int currentVal, int prevVal){

            if(currentVal>prevVal+10*this.stdDev && currentVal>4*prevVal){
                return true;
            }
        return false;




    }

    public void setStochasticValues(double[] statistics){
        this.stdDev = statistics[0];
        this.variance = statistics[1];
        this.mean = statistics[2];
    }
}
