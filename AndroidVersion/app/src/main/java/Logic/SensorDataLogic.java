package Logic;

import org.apache.commons.collections4.queue.CircularFifoQueue;

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

        float currentVal = buffer.get(99);
        float prevVal = buffer.get(98);
        if(currentVal>prevVal+10*this.stdDev && currentVal>5*buffer.get(98)){
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
