package com.example.bluefish.anydrum;

import Logic.FFTLogic;

import Sensors.AccelerationSensorManager;
import Visualizations.LineChart;
import android.content.Context;
import android.view.*;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;

import java.io.File;
import java.io.IOException;

public class FFTActivity extends AppCompatActivity implements SensorActivity{

    private AccelerationSensorManager acSensorManager;

    private LineChart dataChart;
    private LineChart fftChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fft);

        init();
    }


    @Override
    protected  void onResume()
    {
        super.onResume();
    }

    @Override
    protected  void onPause()
    {
        super.onPause();

    }

    private void init(){

        createCharts();
        this.acSensorManager = new AccelerationSensorManager(this,this, false);
        this.acSensorManager.startCalibration();

        final Button btnCalibrate = (Button) findViewById(R.id.BtnUnlock);
        btnCalibrate.setOnClickListener(  new View.OnClickListener() {
            public void onClick(View v) {
                acSensorManager.unlock();

            }
        });

        final Button btnFFT = (Button) findViewById(R.id.BtnFFT);
        btnFFT.setOnClickListener(  new View.OnClickListener() {
            public void onClick(View v) {
                FFTLogic mFFTLogic = new FFTLogic();
                FFTRealTimeAnalyzer mFTR = new FFTRealTimeAnalyzer();
                double[] data = mFFTLogic.transform(acSensorManager.getLastKnock());

                fftChart.appendData(data);
                TextView text = (TextView) findViewById(R.id.viewMaxima);
                mFTR.addCluster("test",acSensorManager.getLastKnock());
                text.setText(mFTR.getMaxima("test").toString());




            }
        });
    }

    public void sensorManagerEvent(int id){
        switch(id) {
            case 0:
                return;
            case 1:

                return;
            case 2:

            case 5:


            case 6:
                drawChart();
                return;

            default:
                return;

        }
    }


    private void createCharts()
    {
        this.dataChart = new LineChart(this, (GraphView) findViewById(R.id.graphViewData));
        this.dataChart.setManual(-1,1,0,64);

        this.fftChart = new LineChart(this, (GraphView) findViewById(R.id.graphViewFFT));
        //fftChart.setManual(-5,5,0,64);
        fftChart.setScaling();
        fftChart.setXAxisBoundsManual(true);
        fftChart.setMinX(0);
        fftChart.setMaxX(32);
    }

    public void updateChart(){

    }

    public void unlock(){
        this.acSensorManager.unlock();
    }

    private void drawChart(){
        this.dataChart.appendData(this.acSensorManager.getLastKnock());
    }


}
