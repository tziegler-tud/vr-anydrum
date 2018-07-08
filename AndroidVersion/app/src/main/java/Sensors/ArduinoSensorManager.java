package Sensors;

import Logic.*;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import com.example.bluefish.anydrum.*;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Vector;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class ArduinoSensorManager implements SensorEventListener {

    private SensorActivity refMain;
    private Context mContext;


    private SensorManager mSensorManager;
    private SensorDataLogic mSensorDataLogic;
    private double[] statistics;

    private boolean calibrating;
    private int calIndex;

    private int knockLength = 115;


    private double[] lastKnock = new double[knockLength+13];

    private ArduinoUSB arduinoUSB;

    private boolean currentKnock;
    private boolean autoUnlock;
    private boolean knockDetectedState;
    private boolean lockState;
    private int lockedCount;
    private CircularFifoQueue<Integer> bufferL = new CircularFifoQueue<>(1000);
    private CircularFifoQueue<Integer> bufferR = new CircularFifoQueue<>(1000);

    /**
     * Invokes new AccelerationSensorManager
     *
     *
     *
     *
     */
    public ArduinoSensorManager(SensorActivity refMain, Context mContext, boolean autoUnlock){

        this.statistics = new double[3];

        this.mContext = mContext;
        this.refMain = refMain;
        this.mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        this.autoUnlock = autoUnlock;

        this.arduinoUSB = refMain.getArduinoUSB();

        this.statistics[0] = 0;
        this.statistics[1] = 0;
        this.statistics[2] = 0;



        this.mSensorDataLogic = new SensorDataLogic(new double[]{0,0,0});

        this.mSensorDataLogic = new SensorDataLogic(new double[]{0,0,0});
        if(!this.checkHardwareSupport()){
            //abort mission
            return;
        }
        registerAccelerationSensor();


        startCalibration();


    }
    private void registerAccelerationSensor(){

        Sensor accelSensor = this.mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        this.mSensorManager.registerListener(this,accelSensor,5000);

   }

    private boolean checkHardwareSupport(){

        return this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;

    }



    public boolean startCalibration(){
        this.calibrating =  true;
        this.calIndex = 0;
        refMain.sensorManagerEvent(3);
        return true;
    }

    public boolean stopCalibration(){
        this.calibrating = false;
        this.calIndex = 0;
        this.statistics = calcStatistics(this.bufferL);
        refMain.sensorManagerEvent(4);
        return true;
    }

    private double[] calcStatistics(CircularFifoQueue<Integer> buffer){

        DataStatistics mStatistics = new DataStatistics(buffer);
        double variance = mStatistics.getVariance();
        double stdDev = mStatistics.getStdDev();
        double mean = mStatistics.getMean();

        double[] array = new double[]{stdDev,variance,mean};
        this.mSensorDataLogic.setStochasticValues(array);
        this.statistics =  array;

        return array;

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        ArduinoPacket pack =  arduinoUSB.lastPacket;
        dataInput(pack);
    }

    public void dataInput(ArduinoPacket packet) {


        int data =  packet.left;
        int dataL = data - (int) statistics[2];
        //int dataR = packet.right;

        //System.out.println("zAcl: " + sensorEvent.values[2]);
        this.bufferL.add(dataL);
        //this.bufferR.add(dataR);
        refMain.updateChart();

        if (this.calibrating) {
            calIndex += 1;
            if (calIndex >= 1500) {
                stopCalibration();
            }
        } else {
            if (!this.lockState) {
                if (this.mSensorDataLogic.detectKnocks(dataL,this.bufferL.get(bufferL.size()-2))) {
                    lock();
                    this.knock();
                }
            }
            else {
                if(currentKnock) this.lastKnock[lockedCount+13]=dataL;
                lockedCount += 1;
                if(lockedCount >= knockLength){
                    this.knockCompleted();

                }
                if(lockedCount >= knockLength+300) {
                    privateUnlock();
                    this.noKnock();
                }
            }
        }

    }


    private void knock(){
        this.knockDetectedState=true;
        refMain.sensorManagerEvent(5);

    }

    private void noKnock(){
        this.knockDetectedState=false;
        refMain.sensorManagerEvent(6);
    }

    private void knockCompleted(){
        currentKnock=false;
        refMain.sensorManagerEvent(7);
    }

    private void privateUnlock(){
        if(this.autoUnlock) {
            unlock();
        }
        else {
            currentKnock=false;
        }
    }

    public void lock(){
        this.currentKnock=true;
        this.lockedCount = 0;
        this.lockState=true;
        refMain.sensorManagerEvent(1);
    }

    public void unlock(){
        if(this.calibrating){

        }
        else {
            this.lockState = false;
            refMain.sensorManagerEvent(2);
            for (int i = 0; i < 13; i++) {
                this.lastKnock[i] = bufferL.get(bufferL.size() - knockLength - i - 1);
            }
        }
    }


    public CircularFifoQueue<Integer> getBufferL(){
        return this.bufferL;

    }

    public CircularFifoQueue<Integer> getBufferR(){
        return this.bufferR;

    }


    public boolean getKnockDetectedState(){
        return this.knockDetectedState;
    }

    public boolean getLockState(){
        return this.lockState;
    }

    public double[] getStatistics(){
        return this.statistics;
    }

    public double[] getLastKnock(){
        return this.lastKnock;
    }

    public void setAutoUnlock(boolean b){
        this.autoUnlock = b;
    }

    public void setKnockLength(int knockLength){
        this.knockLength = knockLength;
    }
    public int getKnockLength(){
        return this.knockLength;
    }

}

