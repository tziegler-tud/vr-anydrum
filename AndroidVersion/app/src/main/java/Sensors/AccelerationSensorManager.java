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
import com.example.bluefish.anydrum.MainActivity;
import com.example.bluefish.anydrum.R;
import com.example.bluefish.anydrum.SensorActivity;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import static java.lang.Math.abs;
import static java.lang.Math.max;
//
//public class AccelerationSensorManager implements SensorEventListener {
//
//    private SensorActivity refMain;
//    private Context mContext;
//
//
//    private SensorManager mSensorManager;
//    private SensorDataLogic mSensorDataLogic;
//    private double[] statistics;
//
//    private boolean calibrating;
//    private int calIndex;
//
//    private int knockLength = 62;
//
//
//    private double[] lastKnock = new double[knockLength+2];
//
//    private boolean currentKnock;
//    private boolean autoUnlock;
//    private boolean knockDetectedState;
//    private boolean lockState;
//    private int lockedCount;
//    private CircularFifoQueue<Float> buffer = new CircularFifoQueue<>(200);
//
//    /**
//     * Invokes new AccelerationSensorManager
//     *
//     *
//     *
//     *
//     */
//    public AccelerationSensorManager(SensorActivity refMain, Context mContext, boolean autoUnlock){
//
//        this.mContext = mContext;
//        this.refMain = refMain;
//        this.mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
//        this.autoUnlock = autoUnlock;
//
//
//        this.mSensorDataLogic = new SensorDataLogic(new double[]{0,0,0});
//        if(!this.checkHardwareSupport()){
//            //abort mission
//            return;
//        }
//        registerAccelerationSensor();
//
//        startCalibration();
//
//
//    }
//
//
//    private boolean checkHardwareSupport(){
//
//        return this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
//
//    }
//
//    private void registerAccelerationSensor(){
//
//        Sensor accelSensor = this.mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
//        this.mSensorManager.registerListener(this,accelSensor,SensorManager.SENSOR_DELAY_FASTEST);
//
//
//    }
//    public boolean startCalibration(){
//        this.calibrating =  true;
//        this.calIndex = 0;
//        refMain.sensorManagerEvent(3);
//        return true;
//    }
//
//    public boolean stopCalibration(){
//        this.calibrating = false;
//        this.calIndex = 0;
//        this.statistics = calcStatistics(this.buffer);
//        refMain.sensorManagerEvent(4);
//        return true;
//    }
//
//    private double[] calcStatistics(CircularFifoQueue<Float> buffer){
//
//        DataStatistics mStatistics = new DataStatistics(buffer);
//        double variance = mStatistics.getVariance();
//        double stdDev = mStatistics.getStdDev();
//        double mean = mStatistics.getMean();
//
//        double[] array = new double[]{stdDev,variance,mean};
//        this.mSensorDataLogic.setStochasticValues(array);
//        this.statistics =  array;
//
//        return array;
//
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//
//        //System.out.println("zAcl: " + sensorEvent.values[2]);
//        this.buffer.add(sensorEvent.values[2]);
//        refMain.updateChart(sensorEvent.values[2]);
//
//        if (this.calibrating) {
//            calIndex += 1;
//            if (calIndex >= 200) {
//                stopCalibration();
//            }
//        } else {
//            if (!this.lockState) {
//                if (this.mSensorDataLogic.detectKnocks(this.buffer)) {
//                    lock();
//                    this.knock();
//                }
//            }
//            else {
//                 if(currentKnock) this.lastKnock[lockedCount+2]=sensorEvent.values[2];
//                 lockedCount += 1;
//                 if(lockedCount >= knockLength){
//                     this.knockCompleted();
//                     privateUnlock();
//                     this.noKnock();
//                 }
//            }
//        }
//
//    }
//
//
//    private void knock(){
//        this.knockDetectedState=true;
//        refMain.sensorManagerEvent(5);
//
//    }
//
//    private void noKnock(){
//        this.knockDetectedState=false;
//        refMain.sensorManagerEvent(6);
//    }
//
//    private void knockCompleted(){
//        refMain.sensorManagerEvent(7);
//    }
//
//    private void privateUnlock(){
//        if(this.autoUnlock) {
//            unlock();
//        }
//        else {
//            currentKnock=false;
//        }
//    }
//
//    public void lock(){
//        this.currentKnock=true;
//        this.lockedCount = 0;
//        this.lockState=true;
//        refMain.sensorManagerEvent(1);
//    }
//
//    public void unlock(){
//        if(this.calibrating){
//
//        }
//        else {
//            this.lockState = false;
//            refMain.sensorManagerEvent(2);
//            for (int i = 0; i < 2; i++) {
//                this.lastKnock[i] = buffer.get(buffer.size() - knockLength - i - 1);
//            }
//        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//
//    }
//
//    public CircularFifoQueue<Float> getBuffer(){
//        return this.buffer;
//
//    }
//
//    public boolean getKnockDetectedState(){
//        return this.knockDetectedState;
//    }
//
//    public boolean getLockState(){
//        return this.lockState;
//    }
//
//    public double[] getStatistics(){
//        return this.statistics;
//    }
//
//    public double[] getLastKnock(){
//        return this.lastKnock;
//    }
//
//    public void setAutoUnlock(boolean b){
//        this.autoUnlock = b;
//    }
//
//    public void setKnockLength(int knockLength){
//        this.knockLength = knockLength;
//    }
//    public int getKnockLength(){
//        return this.knockLength;
//    }
//
//}
