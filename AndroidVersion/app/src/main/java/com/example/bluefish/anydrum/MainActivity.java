package com.example.bluefish.anydrum;

import Learning.DBSCAN;
import Sensors.AccelerationSensorManager;
import Visualizations.LineChart;
import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorActivity {

    private PdUiDispatcher dispatcher;
    private int updatePeriodACL = 100; // in ms
    private AccelerationSensor aclSensor;
    public  ArduinoUSB arduinoUSB;
    public AccelerationSensor getAclSensor() {
        return aclSensor;
    }
    private AccelerationSensorManager acSensorManager;

    private LineChart chart;

    private DBSCAN dbscan;

    private boolean knockLengthLearned = false;

    private FFTRealTimeAnalyzer mFFTRealTimeAnalyzer;

    private boolean learning;

    private List<VirtualInstrument> instruments = new LinkedList<>();


    public DBSCAN getDbscan() {
        return dbscan;
    }

    public List<ArduinoPacket> packetList;


    // Used to load the 'native-lib' library on application startup.
   static {
        System.loadLibrary("native-lib");
    }*/




    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void onGroupItemClick(MenuItem item) {



    }

    public void showFFTActivity(MenuItem item){
        Intent intent = new Intent(this, FFTActivity.class);

        startActivity(intent);

    }



    private void initGui(){
        final MainActivity refMain = this;
        final Button playSnareDrum = (Button) findViewById(R.id.btnSnareDrum);
        final Button playBassDrum = (Button) findViewById(R.id.btnBassDrum);
        final Button playHiHatDrum = (Button) findViewById(R.id.btnHiHat);

        final Button btnStart = (Button) findViewById(R.id.btnStart);
        final Button btnCalibrate = (Button) findViewById(R.id.btnCalibrate);

        learning=false;
        TextView textView = (TextView) findViewById(R.id.ViewLearningState);
        textView.setText("Learning requiered");
        /*
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
                arduinoUSB.setStartUsingClusters(useClusters);

                if(useClusters)
                    btnStart.setBackgroundColor(0xff00bb00);
                else
                    btnStart.setBackgroundColor(0xffeeeeee);

            }
        });*/
        btnStart.setOnClickListener(  new View.OnClickListener() {
                                          public void onClick(View v) {
                                              startLearning();
                                          }
                                      });


        btnCalibrate.setOnClickListener(  new View.OnClickListener() {
            public void onClick(View v) {
                refMain.acSensorManager.startCalibration();

            }
        });
    }

    private void initInstruments(){
        VirtualInstrument v1,v2,v3;

        v1 = new VirtualInstrument("Hi-Hat");
        v2 = new VirtualInstrument("Base");
        v3 = new VirtualInstrument("TomTom");

        instruments.add(v1);
        instruments.add(v2);
        instruments.add(v3);

    }

    private void createChart()
    {
//        LineChart chart = new LineChart(this, aclSensor.getListOfSensorDataFiltered());//getListOfSensorData
        this.chart = new LineChart(this, (GraphView) findViewById(R.id.graphViewData));
        this.chart.setManual(-1,1,0,200);
    }

    public void updateChart(float data){
        this.chart.appendData(this.acSensorManager.getBuffer());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        packetList = new ArrayList<ArduinoPacket>();

        /*// Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sensorValue);
        tv.setText(stringFromJNI());
*/
        dbscan = new DBSCAN(0.00002f, 1);
        createChart();
        this.mFFTRealTimeAnalyzer = new FFTRealTimeAnalyzer();
        this.acSensorManager = new AccelerationSensorManager(this,this, true);
        initInstruments();
        startCalibration();


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        aclSensor = new AccelerationSensor(this, updatePeriodACL); //period in ms
        aclSensor.subscribeToAccelerationSensor();


        arduinoUSB = new ArduinoUSB(this);



        try {
            initPD();
            loadPDPatch();
        }catch (IOException e)
        {
            finish();
        }
        initGui();
    }

    public void sensorManagerEvent(int id){
        switch(id) {
            case 0:
                return;
            case 1:
                this.lock();
                return;
            case 2:
                this.unlock();
                return;
            case 3:
                return;
            case 4:
                this.stopCalibration(acSensorManager.getStatistics());
            case 5:
                this.knock();
                return;
            case 6:
                this.noKnock();
                return;
            case 7:
                this.match();
                return;
            default:
                return;

        }
    }

    public void startCalibration(){
        acSensorManager.startCalibration();
        this.paintButton((Button)this.findViewById(R.id.btnCalibrate),0xff9900);


    }

    public void stopCalibration(double[] array){

        setStdDevTextView(array[0]);
    }
    private void initPD() throws IOException{
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate,0, 2, 8, true);

        dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);
    }

    private void knock(){
        TextView textView = (TextView) findViewById(R.id.viewKnockDetected);
        textView.setText("Knock detected");


    }

    private void noKnock(){
        TextView textView = (TextView) findViewById(R.id.viewKnockDetected);
        textView.setText("waiting for knock");
    }


    private void lock(){
        TextView textView = (TextView) findViewById(R.id.viewLockState);
        textView.setText("locked");
    }

    private void unlock(){
        TextView textView = (TextView) findViewById(R.id.viewLockState);
        textView.setText("unlocked");
    }

    private void match(){
        if(learning){

            if(learnPattern(acSensorManager.getLastKnock())){

            }
            else {
                stopLearning();
            }
        }

        String matchedInstrument = mFFTRealTimeAnalyzer.matchData(acSensorManager.getLastKnock());
        System.out.println("matched: " + matchedInstrument);

    }

    private void setStdDevTextView(double stdDev){
       this.setTextViewContent((TextView)this.findViewById(R.id.viewStdDeviation),Double.toString(stdDev));
    }

    public void paintButton(Button btn, int color){
        btn.setBackgroundColor(color);
    }

    public void setTextViewContent(TextView txt, String content){
        txt.setText(content);
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

    private void startLearning(){
        TextView textView = (TextView) findViewById(R.id.ViewLearningState);
        textView.setText("Learning... Waiting for Knock");
        mFFTRealTimeAnalyzer.clearCluster();
        for(VirtualInstrument v:instruments){
            v.setLearned(false);
        }
        this.learning=true;


    }

    private void stopLearning(){
        TextView textView = (TextView) findViewById(R.id.ViewLearningState);
        textView.setText("learning completed!");
        this.learning=false;

    }

    /* returns true if succesfull, false if failed */
    private boolean learnPattern(double[] d) {

        for (VirtualInstrument v : instruments) {
            if (!v.learned()) {
                mFFTRealTimeAnalyzer.addCluster(v.getName(), d);
                v.setLearned(true);
                System.out.println("instrument " + v.getName() + " learned");
                return true;
            }


        }
        System.out.println("all instruments learned, returning false...");
        return false;
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


    public void storeArduinoData(ArduinoPacket packet)
    {
        packetList.add(packet);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    //public native String stringFromJNI();
}