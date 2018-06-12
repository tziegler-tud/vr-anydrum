package Sensors;

import Logic.*;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Button;
import android.widget.TextView;
import com.example.bluefish.anydrum.MainActivity;
import com.example.bluefish.anydrum.R;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class AccelerationSensorManager implements SensorEventListener {

    private MainActivity refMain;



    private SensorManager mSensorManager;
    private SensorDataLogic mSensorDataLogic;
    private double[] statistics;

    private boolean calibrating;
    private int calIndex;

    private boolean detectionLocked;
    private int lockedCount;
    private CircularFifoQueue<Float> buffer = new CircularFifoQueue<>(100);

    /**
     * Invokes new AccelerationSensorManager
     *
     *
     *
     *
     */
    public AccelerationSensorManager(MainActivity refMain){

        this.refMain = refMain;
        this.mSensorManager = (SensorManager) this.refMain.getSystemService(Context.SENSOR_SERVICE);
        this.startCalibration();
        this.unlock();
        this.mSensorDataLogic = new SensorDataLogic(new double[]{0,0,0});
        if(!this.checkHardwareSupport()){
            //abort mission
            return;
        }
        registerAccelerationSensor();


    }


    private boolean checkHardwareSupport(){

        return this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;

    }

    private void registerAccelerationSensor(){

        Sensor accelSensor = this.mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        this.mSensorManager.registerListener(this,accelSensor,SensorManager.SENSOR_DELAY_GAME);


    }
    public boolean startCalibration(){
        this.calibrating =  true;
        this.calIndex = 0;
        refMain.paintButton((Button)refMain.findViewById(R.id.btnCalibrate),0xff9900);
        return true;
    }

    public boolean stopCalibration(){
        this.calibrating = false;
        this.calIndex = 0;
        refMain.paintButton((Button)refMain.findViewById(R.id.btnCalibrate),0x222222);
        return true;
    }

    private void calcStatistics(CircularFifoQueue<Float> buffer){

        DataStatistics mStatistics = new DataStatistics(buffer);
        double variance = mStatistics.getVariance();
        double stdDev = mStatistics.getStdDev();
        double mean = mStatistics.getMean();

        double[] array = new double[]{stdDev,variance,mean};
        this.mSensorDataLogic.setStochasticValues(array);
        refMain.setTextViewContent((TextView)refMain.findViewById(R.id.viewStdDeviation),Double.toString(stdDev));
        this.statistics =  array;

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        System.out.println("zAcl: " + sensorEvent.values[2]);
        this.buffer.add(sensorEvent.values[2]);
        refMain.updateChart(sensorEvent.values[2]);

        if (this.calibrating) {
            calIndex += 1;
            if (calIndex >= 100) {
                stopCalibration();
                calcStatistics(this.buffer);
            }
        } else {
            if (!this.detectionLocked) {
                if (this.mSensorDataLogic.detectKnocks(this.buffer)) {
                    lock();
                    this.knockDetected(true);
                }
            }
            else {
                 lockedCount += 1;
                 if(lockedCount >= 15){
                     unlock();
                     knockDetected(false);
                 }
            }
        }

    }

    private void knockDetected(boolean state){
        if (state){
            refMain.setKnockDetectedTextView("Knock detected");
        }
        else refMain.setKnockDetectedTextView("waiting for knock");
    }

    private void lock(){
        this.detectionLocked = true;
        this.lockedCount = 0;

        refMain.setLockStateTextView("locked");
    }

    private void unlock(){
        this.detectionLocked = false;
        refMain.setLockStateTextView("unlocked");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public CircularFifoQueue<Float> getBuffer(){
        return this.buffer;

    }


}
