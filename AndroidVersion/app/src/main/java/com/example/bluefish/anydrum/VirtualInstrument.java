package com.example.bluefish.anydrum;

public class VirtualInstrument {

    private String name;
    private double[] pattern;
    private Integer[] maxima;
    private boolean learned;

    public VirtualInstrument(String name){
        this.name = name;
    }


    public VirtualInstrument(String name, double[] pattern, Integer[] maxima){
        this.name = name;
        this.pattern = pattern;
        this. maxima = maxima;

    }

    public void setLearned(boolean learned){
        this.learned = learned;
    }

    public String getName() {
        return this.name;
    }

    public double[] getPatttern(){
        return this.pattern;

    }

    public Integer[] getMaxima(){
        return this.maxima;
    }

    public boolean learned(){
        return this.learned;
    }




}
