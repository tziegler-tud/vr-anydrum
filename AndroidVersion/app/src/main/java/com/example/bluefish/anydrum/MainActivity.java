package com.example.bluefish.anydrum;

import Learning.DBSCAN;
import Sensors.AccelerationSensor;
import Visualizations.LineChart;
import android.content.Context;
import android.view.*;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private PdUiDispatcher dispatcher;
    private int updatePeriodACL = 100; // in ms
    private AccelerationSensor aclSensor;
    public AccelerationSensor getAclSensor() {
        return aclSensor;
    }
    private DBSCAN dbscan;
    public DBSCAN getDbscan() {
        return dbscan;
    }
    public SensorManager mSensorManager;
    public SensorManager getmSensorManager() {
        return mSensorManager;
    }



    // Used to load the 'native-lib' library on application startup.
   static {
        System.loadLibrary("native-lib");
    }




    private void initGui(){
        final MainActivity refMain = this;
        final Button playSnareDrum = (Button) findViewById(R.id.btnSnareDrum);
        final Button playBassDrum = (Button) findViewById(R.id.btnBassDrum);
        final Button playHiHatDrum = (Button) findViewById(R.id.btnHiHat);

        final Button btnStart = (Button) findViewById(R.id.btnStart);

        playSnareDrum.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        PdBase.sendBang("bangSnareDrum");
                        LearningTimer timer = new LearningTimer((TextView) findViewById(R.id.timeSnare), 10, refMain, EnumDrum.SNARE);
                    }
                }
        );
        playBassDrum.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        PdBase.sendBang("bangBassDrum");
                        LearningTimer timer = new LearningTimer((TextView) findViewById(R.id.timeBass), 10, refMain, EnumDrum.BASS);
                    }
                }
        );
        playHiHatDrum.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        PdBase.sendBang("bangHiHatDrum");
                        LearningTimer timer = new LearningTimer((TextView) findViewById(R.id.timeHiHat), 10, refMain, EnumDrum.HIHAT);
                    }
                }
        );
        btnStart.setOnClickListener(  new View.OnClickListener() {
            public void onClick(View v) {
                boolean useClusters = aclSensor.isStartUsingClusters()  ? false : true;
                aclSensor.setStartUsingClusters(useClusters);
                if(useClusters)
                    btnStart.setBackgroundColor(0xff00bb00);
                else
                    btnStart.setBackgroundColor(0xffeeeeee);
            }
        });
    }

    public void createChart()
    {
        LineChart chart = new LineChart(this, aclSensor.getListOfSensorDataFiltered());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sensorValue);
        tv.setText(stringFromJNI());

        dbscan = new DBSCAN(0.00002f, 1);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        aclSensor = new AccelerationSensor(this, updatePeriodACL); //period in ms
        aclSensor.subscribeToAccelerationSensor();

        ArduinoUSB arduinoUSB = new ArduinoUSB(this);


        try {
            initPD();
            loadPDPatch();
        }catch (IOException e)
        {
            finish();
        }
        initGui();
    }

    private void initPD() throws IOException{
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate,0, 2, 8, true);

        dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);
    }

    private void loadPDPatch() throws  IOException
    {
        File dir = getFilesDir();
        IoUtils.extractZipResource(getResources().openRawResource(R.raw.puredatafiles), dir, true);
        File pdPatchSnareDrum = new File(dir, "snareDrum.pd");
        File pdPatchBassDrum = new File(dir, "bassDrum.pd");
        File pdPatchHiHatDrum = new File(dir, "hihatDrum.pd");


        PdBase.openPatch(pdPatchSnareDrum.getAbsolutePath());
        PdBase.openPatch(pdPatchBassDrum.getAbsolutePath());
        PdBase.openPatch(pdPatchHiHatDrum.getAbsolutePath());
    }


    @Override
    protected  void onResume()
    {
        super.onResume();
        PdAudio.startAudio(this);
    }

    @Override
    protected  void onPause()
    {
        super.onPause();
        PdAudio.stopAudio();
    }



    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
