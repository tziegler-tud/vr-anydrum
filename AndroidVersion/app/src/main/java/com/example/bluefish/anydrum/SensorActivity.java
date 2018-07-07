package com.example.bluefish.anydrum;

import android.support.v7.app.AppCompatActivity;

public interface SensorActivity  {


    ArduinoUSB getArduinoUsb();

    void updateChart();

    void sensorManagerEvent(int id);
    /*
    id:
    0   initialised
    1   locked
    2   unlocked
    3   calibration started
    4   calibration stopped
    5   knock detected
    6   waiting for knock
    7   knock completed
     */


}
