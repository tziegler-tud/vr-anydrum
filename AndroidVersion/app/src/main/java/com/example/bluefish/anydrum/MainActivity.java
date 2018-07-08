package com.example.bluefish.anydrum;

import Learning.ClusterableDoublePoint;
import Learning.DBSCAN;

import Learning.LearningCounter;
import Learning.LearningTimer;
import Sensors.ArduinoSensorManager;
import Visualizations.LineChart;
import android.content.Intent;
import android.view.*;
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

import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorActivity {

    private PdUiDispatcher dispatcher;
    public  TextView viewInformation;

    private int updatePeriodACL = 200; // in nano mili sec?
    private long oldSystemTime=0,updateTimeMS = 100;//ms

   // private AccelerationSensor aclSensor;
    public  ArduinoUSB arduinoUSB;
    /*public AccelerationSensor getAclSensor() {
        return aclSensor;
    }*/

    private ArduinoSensorManager arduinoSensorManager;
    //private AccelerationSensorManager acSensorManager;

    private LineChart chart;

    private DBSCAN dbscan;

    private boolean knockLengthLearned = false;

    private FFTRealTimeAnalyzer mFFTRealTimeAnalyzer;

    private boolean learning;
    private boolean matchingEnabled;

    private List<VirtualInstrument> instruments;

    private VirtualInstrument currentInstrument;


    public DBSCAN getDbscan() {
        return dbscan;
    }

    public LearningCounter counter;




    // Used to load the 'native-lib' library on application startup.
//   static {
//        System.loadLibrary("native-lib");
//    }




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

        learning=false;
        TextView textView = (TextView) findViewById(R.id.ViewLearningState);
        textView.setText("Learning requiered");

        playSnareDrum.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        PdBase.sendBang("bangSnareDrum");
//                        LearningTimer timer = new LearningTimer((TextView) findViewById(R.id.timeSnare), 10, refMain, EnumDrum.SNARE);

//                        counter = new LearningCounter((TextView) findViewById(R.id.timeSnare), 2, refMain, EnumDrum.SNARE);
                        startLearning(instruments.get(1));
                        playSnareDrum.setBackgroundColor(0xff00bb00);

                    }
                }
        );
        playBassDrum.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        PdBase.sendBang("bangBassDrum");
//                        LearningTimer timer = new LearningTimer((TextView) findViewById(R.id.timeBass), 10, refMain, EnumDrum.BASS);
//                        counter = new LearningCounter((TextView) findViewById(R.id.timeSnare), 5, refMain, EnumDrum.BASS);
                        startLearning(instruments.get(2));
                        playBassDrum.setBackgroundColor(0xff00bb00);
                    }
                }
        );
        playHiHatDrum.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        PdBase.sendBang("bangHiHatDrum");
//                        LearningTimer timer = new LearningTimer((TextView) findViewById(R.id.timeHiHat), 10, refMain, EnumDrum.HIHAT);
//                        counter = new LearningCounter((TextView) findViewById(R.id.timeSnare), 5, refMain, EnumDrum.HIHAT);
                        startLearning(instruments.get(0));

                        playHiHatDrum.setBackgroundColor(0xff00bb00);


                    }
                }
        );
        btnStart.setOnClickListener(  new View.OnClickListener() {
            public void onClick(View v) {
//                boolean useClusters = arduinoUSB.isStartUsingClusters()  ? false : true;
////                aclSensor.setStartUsingClusters(useClusters);
//                arduinoUSB.setStartUsingClusters(useClusters);
//
//                if(useClusters)
//                    btnStart.setBackgroundColor(0xff00bb00);
//                else
//                    btnStart.setBackgroundColor(0xffeeeeee);
                toggleMatching();
                if(matchingEnabled)
                    btnStart.setBackgroundColor(0xff00bb00);
                else
                    btnStart.setBackgroundColor(0xffeeeeee);

            }
        });
//        btnStart.setOnClickListener(  new View.OnClickListener() {
//                                          public void onClick(View v) {
//                                              startLearning();
//                                          }
//                                      });



    }

    private void initInstruments(){
        VirtualInstrument v1,v2,v3;

        v1 = new VirtualInstrument("Hi-Hat");
        v2 = new VirtualInstrument("Snare");
        v3 = new VirtualInstrument("Base");

        TextView textView1 = (TextView) findViewById(R.id.timeHiHat);
        TextView textView2 = (TextView) findViewById(R.id.timeSnare);
        TextView textView3 = (TextView) findViewById(R.id.timeBass);

        v1.setTxtView(textView1);
        v2.setTxtView(textView2);
        v3.setTxtView(textView3);


        instruments.add(v1);
        instruments.add(v2);
        instruments.add(v3);

    }

    private void createChart()
    {
//        LineChart chart = new LineChart(this, aclSensor.getListOfSensorDataFiltered());//getListOfSensorData
        this.chart = new LineChart(this, (GraphView) findViewById(R.id.graph));

        this.chart.setManual(-1,1,0,1000);

        this.chart.setScalingY();
    }

    public void updateChart() {
        long time= System.currentTimeMillis();
            if(time-oldSystemTime >= updateTimeMS) {

                this.chart.appendData(this.arduinoSensorManager.getBufferL(), this.arduinoSensorManager.getBufferR());
                oldSystemTime=System.currentTimeMillis();
            }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instruments = new LinkedList<>();
        viewInformation = (TextView) findViewById(R.id.viewInfo);
        setContentView(R.layout.activity_main);

        /*// Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sensorValue);
        tv.setText(stringFromJNI());
*/
        dbscan = new DBSCAN(0.00002f, 1);
        createChart();
        this.mFFTRealTimeAnalyzer = new FFTRealTimeAnalyzer();
//        this.acSensorManager = new AccelerationSensorManager(this,this, true);
        initInstruments();
        this.matchingEnabled = false;

        arduinoUSB = new ArduinoUSB(this);
        this.arduinoSensorManager = new ArduinoSensorManager(this,this,true);


/*
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        aclSensor = new AccelerationSensor(this, updatePeriodACL); //period in ms
        aclSensor.subscribeToAccelerationSensor();
*/





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
                this.stopCalibration(arduinoSensorManager.getStatistics());
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
        arduinoSensorManager.startCalibration();


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

            if(currentInstrument != null && arduinoSensorManager.getLastKnock() != null)
                if(learnPattern(currentInstrument,arduinoSensorManager.getLastKnock())){

            }
            else {
                stopLearning();
            }
        }
        if(matchingEnabled) {
            String name = mFFTRealTimeAnalyzer.matchData(arduinoSensorManager.getLastKnock());
            switch (name) {
                case "Hi-Hat":
                    playSound(EnumDrum.HIHAT);
                    break;
                case "Snare":
                    playSound(EnumDrum.SNARE);
                    break;
                case "Base":
                    playSound(EnumDrum.BASS);
                    break;
            }
        }
    }

        /*
        List<Integer> list = mFFTRealTimeAnalyzer.calcMaxima(arduinoSensorManager.getLastKnock());
        ClusterableDoublePoint cp = new ClusterableDoublePoint(convertToDoubleArray(list),4);
        if(counter!=null && counter.isLearned == false)
             counter.reduceCounter(cp);
        */
//        if(arduinoUSB.isStartUsingClusters())
//            dbscan.evaluatePoint(cp);


//        if(arduinoUSB.isStartUsingClusters())
//            dbscan.evaluatePoint(cp);


//    }

    private void playSound(EnumDrum  drumsound)
    {
        //playsound
        switch (drumsound)
        {
            case HIHAT:
                PdBase.sendBang("bangHiHatDrum");
                break;
            case SNARE:
                PdBase.sendBang("bangSnareDrum");
                break;
            case BASS:
                PdBase.sendBang("bangBassDrum");
                break;
        }
    }


    private double[] convertToDoubleArray(List<Integer> list){
        int size = list.size();
        double[] data = new double[size];
        for(int i=0;i<size;i++){
            data[i] = list.get(i);
        }


        return data;
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

    private void startLearning(VirtualInstrument v){
        TextView textView = (TextView) findViewById(R.id.ViewLearningState);
        textView.setText("Learning...");
        v.forget();

        currentInstrument = v;
        this.learning=true;


    }

    private void stopLearning(){
        TextView textView = (TextView) findViewById(R.id.ViewLearningState);
        textView.setText("learning completed!");
        Button btn = (Button) findViewById(R.id.btnSnareDrum);
        btn.setBackgroundColor(0xffeeeeee);
        btn = (Button) findViewById(R.id.btnHiHat);
        btn.setBackgroundColor(0xffeeeeee);
        btn = (Button) findViewById(R.id.btnBassDrum);
        btn.setBackgroundColor(0xffeeeeee);
        this.learning=false;
        this.currentInstrument = null;

    }

    /* returns true if succesfull, false if failed */
    private boolean learnPattern(VirtualInstrument v, double[] d) {

        if (v != null) {


            if (!v.learned()) {
                System.out.println("im here!");
                v.setLearned(v.learn(mFFTRealTimeAnalyzer.calcMaxima(d)));
                System.out.println("cp2");

                v.getTxtView().setText(Integer.toString(v.getLearncounter()));
                System.out.println("cp3");
                if (v.learned()) {

                    System.out.println("cp4");
                    mFFTRealTimeAnalyzer.addCluster(v.getName(), v.getMaxima());
                    System.out.println("cp5");
                    stopLearning();
                    System.out.println("cp6");
                }
                System.out.println("cp4-1");
                return true;
            }

            System.out.println("all instruments learned, returning false...");
            return false;
        }
        else {
            return false;
        }
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

    private void toggleMatching(){
        if(this.matchingEnabled){
            this.matchingEnabled=false;
        }
        else{
            this.matchingEnabled=true;
        }
    }

    public void updateArduino(ArduinoPacket packet){
        arduinoSensorManager.dataInput(packet);

    }
    public void setViewInfo(String info) {
        viewInformation.setText(info);
    }

    public ArduinoUSB getArduinoUSB() {
        return arduinoUSB;
     }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    //public native String stringFromJNI();
}