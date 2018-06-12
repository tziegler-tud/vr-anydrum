package Logic;

import org.apache.commons.collections4.queue.CircularFifoQueue;


public class DataStatistics {

        private CircularFifoQueue<Float> data;
        private int size;

        public DataStatistics(CircularFifoQueue<Float> data) {
            this.data = data;
            size = data.size();
        }

        public double getMean() {
            double sum = 0.0;
            for(double a : data)
                sum += a;
            return sum/size;
        }

        public double getVariance() {
            double mean = getMean();
            double temp = 0;
            for(double a :data)
                temp += (a-mean)*(a-mean);
            return temp/(size-1);
        }

        public double getStdDev() {
            return Math.sqrt(getVariance());
        }


}
