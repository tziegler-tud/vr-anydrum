package com.example.bluefish.anydrum;

import Logic.FFTLogic;

//import Sensors.AccelerationSensorManager;
import Sensors.ArduinoSensorManager;
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

    private ArduinoSensorManager arduinoSensorManager;

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
        this.arduinoSensorManager = new ArduinoSensorManager(this,this, false);
        this.arduinoSensorManager.startCalibration();

        final Button btnCalibrate = (Button) findViewById(R.id.BtnUnlock);
        btnCalibrate.setOnClickListener(  new View.OnClickListener() {
            public void onClick(View v) {
                arduinoSensorManager.unlock();

            }
        });

        final Button btnFFT = (Button) findViewById(R.id.BtnFFT);
        btnFFT.setOnClickListener(  new View.OnClickListener() {
            public void onClick(View v) {
                FFTLogic mFFTLogic = new FFTLogic();
                FFTRealTimeAnalyzer mFTR = new FFTRealTimeAnalyzer();
                double[] data = mFFTLogic.transform(arduinoSensorManager.getLastKnock());

                fftChart.appendData(data);
                TextView text = (TextView) findViewById(R.id.viewMaxima);





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
        this.dataChart = new LineChart(this, (GraphView) findViewById(R.id.graph));
        this.dataChart.setManual(-1,1,0,500);

        this.fftChart = new LineChart(this, (GraphView) findViewById(R.id.graphViewFFT));
        //fftChart.setManual(-5,5,0,64);
        fftChart.setScaling();
        fftChart.setXAxisBoundsManual(true);
        fftChart.setMinX(0);
        fftChart.setMaxX(250);
    }

    public void updateChart(){

    }

    public void unlock(){
        this.arduinoSensorManager.unlock();

    }

    private void drawChart(){
        this.dataChart.appendData(this.arduinoSensorManager.getLastKnock());
    }
    public ArduinoUSB getArduinoUSB(){
        return null;
    }

}
