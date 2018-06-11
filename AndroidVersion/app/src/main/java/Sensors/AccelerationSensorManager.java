package Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.example.bluefish.anydrum.MainActivity;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class AccelerationSensorManager implements SensorEventListener {

    private MainActivity refMain;

    private SensorManager mSensorManager;

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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        System.out.println("zAcl: "+ sensorEvent.values[2]);
        this.buffer.add(sensorEvent.values[2]);
        refMain.updateChart(sensorEvent.values[2]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public CircularFifoQueue<Float> getBuffer(){
        return this.buffer;

    }
}
