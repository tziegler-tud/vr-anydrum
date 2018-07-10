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

    public boolean detectKnocks(CircularFifoQueue <Float> buffer){
        int l = buffer.size();
        try {
            float currentVal = buffer.get(l-1);
            float prevVal = buffer.get(l-2);
            if(currentVal>prevVal+10*this.stdDev && currentVal>8*prevVal){
                System.out.println("Knock detected!");
                return true;
            }
        }
        catch (NoSuchElementException e) {
            System.out.println("buffer not yet filled");
            return false;
        }
        return false;




    }

    public void setStochasticValues(double[] statistics){
        this.stdDev = statistics[0];
        this.variance = statistics[1];
        this.mean = statistics[2];
    }
}
