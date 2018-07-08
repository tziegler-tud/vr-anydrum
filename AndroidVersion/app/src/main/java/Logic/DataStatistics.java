package Logic;

import org.apache.commons.collections4.queue.CircularFifoQueue;



public class DataStatistics {

        private CircularFifoQueue<Double> data;
        private int size;

        double mean,variance,stdDev;

        public DataStatistics(CircularFifoQueue<Double> data) {
            this.data = data;
            size = data.size();
            this.mean = calcMean();
            this.variance = calcVariance();
            this.stdDev = calcStdDev();
        }

        private double calcMean() {
            double sum = 0.0;
            for(double a : data)
                sum += a;
            mean =  sum/size;
            return mean;
        }

        private double calcVariance() {
            double mean = getMean();
            double temp = 0;
            for(double a :data)
                temp += (a-mean)*(a-mean);
            return temp/(size-1);
        }

        private double calcStdDev() {
            return Math.sqrt(getVariance());
        }

        public double getMean(){
            return mean;
        }

        public double getVariance(){
            return variance;
        }

        public double getStdDev(){
            return stdDev;
        }


}
