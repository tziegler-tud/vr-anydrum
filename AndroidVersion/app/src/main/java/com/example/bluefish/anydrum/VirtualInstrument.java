package com.example.bluefish.anydrum;

import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class VirtualInstrument {

    private String name;
    private double[] pattern;
    private List<Integer> maxima;
    private List<List<Integer>> maximaList;
    private boolean learned;
    private int learncounter;

    private TextView txtView;

    public VirtualInstrument(String name){
        this.name = name;

        this.pattern = new double[]{};
        this.maxima = new LinkedList<>();
        this.maximaList = new LinkedList<>();


        learncounter = 3;
        this.learned = false;

        this.txtView = null;
    }


    public VirtualInstrument(String name, double[] pattern, List<Integer> maxima){
        this.name = name;
        this.pattern = pattern;
        this.maxima = maxima;

        this.maximaList = new LinkedList<>();

        this.learncounter = 10;

    }

    public void setLearned(boolean learned){
        this.learned = learned;
    }

    public boolean learn(List<Integer> data){
        if(learncounter>=0){
            this.maximaList.add(data);
            --learncounter;
            System.out.println("cp-A: "+learncounter);

            return false;
        }
        else {
            this.learned = true;
            int[] sum = new int[]{0,0,0,0};
            for(List<Integer> int_list:maximaList){
                for(int i=0;i<sum.length;i++){
                    sum[i] += int_list.get(i);
                }
            }
            for(int i=0;i<sum.length;i++){
                maxima.add(sum[i]/4);
            }
            return true;
        }
    }

    public String getName() {
        return this.name;
    }

    public double[] getPatttern(){
        return this.pattern;

    }

    public List<Integer> getMaxima(){
        return this.maxima;
    }

    public void forget(){
        this.learned = false;
        this.maximaList.clear();
    }

    public boolean learned(){
        return this.learned;
    }

    public int getLearncounter(){
        return learncounter;
    }

    public void setTxtView(TextView t){
        txtView = t;
    }

    public TextView getTxtView(){
        return this.txtView;
    }




}
