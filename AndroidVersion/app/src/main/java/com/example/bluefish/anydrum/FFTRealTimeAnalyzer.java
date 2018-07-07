package com.example.bluefish.anydrum;
import Logic.FFTLogic;

import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;


public class FFTRealTimeAnalyzer {

    private FFTLogic mFFTLogic;

    private List<double[]> clusters = new LinkedList<>();
    private List<List<Integer>> clusterMaxima = new LinkedList<>();
    private List<List<Integer>> clusterMinima = new LinkedList<>();

    private List<String> names = new LinkedList<>();
    public FFTRealTimeAnalyzer(){
        mFFTLogic = new FFTLogic();

    }

    public void addCluster(String name, double[] data){

        double[] fft_data = mFFTLogic.transform(data);
        clusters.add(fft_data);
        clusterMaxima.add(findLocalMaxima(fft_data));
        clusterMinima.add(findLocalMinima(fft_data));
        names.add(name);

    }

    public boolean clearCluster(){
        clusters.clear();
        clusterMaxima.clear();
        clusterMinima.clear();
        names.clear();
        return true;
    }

    public String matchData(double[] data){
        //List<Double> dist = new ArrayList<>();
        double dist = 1000;
        String name = "default";
        int index = 0;
        int error = 1000;
        List<Integer> maxima = findLocalMaxima(data);
        //List<Integer> minima = findLocalMinima(data);
        for(int i=0;i<clusterMaxima.size();i++){
            try {
                List<Integer> max = clusterMaxima.get(i);
                List<Integer> min = clusterMinima.get(i);
                //dist.add(calcDistance(data,d));
                int e = matchMaxima(maxima, max);
                System.out.println("error for instrument "+names.get(i) +": " + e);
                if(e<error){
                    error = e;
                    name = names.get(i);
                }
            }
            catch (IndexOutOfBoundsException e){
                break;
            }

        }

        return name;
    }

    private int matchMaxima(List<Integer> d1, List<Integer> d2){
        int error = 0;
        int size = min(d1.size(),d2.size());
        for(int i=0;i<size;i++){
            error = error + abs(d1.get(i)-d2.get(i));
        }
        return error;
    }

    private double calcDistance(double[] data1, double[] data2){

        double[] dist_array = new double[data1.length];
        double sum = 0;
        for(int i=0;i<data1.length;i++){
            dist_array[i] = data1[i] - data2[i];
            sum = sum + data1[i]-data2[i];
        }
        return sum;
    }

    private List<Integer> findLocalMaxima(double[] data){
        int window_size = 5;
        int maxima_amount = 3;
        List<Integer> localMaxima = new LinkedList<>();
        List<Integer> maxima = new LinkedList<>();
        Integer[] max = new Integer[maxima_amount];

        for(int i=2;i<data.length-2;i++){

            if(data[i]>=data[i-1]&& data[i]>=data[i-2] && data[i]>=data[i+1]&& data[i]>=data[i+2]){
                localMaxima.add(i);
            }

        }

        for(int j=0;j<maxima_amount;j++) {
            double res = 0;
            int highest_index = -1;

            for (int k=0;k<localMaxima.size();k++) {
                int v = localMaxima.get(k);
                if(data[v]>res){
                    res = data[v];
                    max[j] = v;
                    highest_index = k;
                }
            }
            if(highest_index!=-1){
                maxima.add(max[j]);
                localMaxima.remove(highest_index);
            }

        }
        System.out.println("local maxima: " + maxima.toString());
        return maxima;
    }

    private List<Integer> findLocalMinima(double[] data){
        int window_size = 5;
        int minima_amount = 3;
        List<Integer> localMinima = new LinkedList<>();
        List<Integer> minima = new LinkedList<>();
        Integer[] max = new Integer[minima_amount];

        for(int i=2;i<data.length-2;i++){
            if(data[i]<data[i-1] && data[i]<data[i-2] && data[i]<data[i+1]&& data[i]<data[i+2]){
                localMinima.add(i);
            }

        }

        for(int j=0;j<minima_amount;j++) {
            double res = 0;
            int highest_index = -1;

            for (int k=0;k<localMinima.size();k++) {
                int v = localMinima.get(k);
                if(data[v]<res){
                    res = data[v];
                    max[j] = v;
                    highest_index = k;
                }
            }
            if(highest_index!=-1){
                minima.add(max[j]);
                localMinima.remove(highest_index);
            }

        }
        System.out.println("local minima: " + minima.toString());
        return minima;
    }

        public List<Integer> getMaxima(String name){
            return clusterMaxima.get(0);
        }


}
