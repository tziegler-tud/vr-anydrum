package com.example.bluefish.anydrum;

import Sensors.AccelerationSensor;
import android.content.Context;
import android.view.*;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private PdUiDispatcher dispatcher;
    public SensorManager mSensorManager;
    public SensorManager getmSensorManager() {
        return mSensorManager;
    }


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }




    private void initGui(){
        final ToggleButton onOff = (ToggleButton) findViewById(R.id.toggleButton);
        final Button playSnareDrum = (Button) findViewById(R.id.btnSnareDrum);

        onOff.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                                             @Override
                                             public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                                // Log.i("onOff is: ", String.valueOf(b));
                                                 float value = (isChecked) ? 1.0f : 0.0f;
                                                 PdBase.sendFloat("onOff", value);
                                             }
                                         }

        );
        playSnareDrum.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // do something when the button is clicked
                        // Yes we will handle click here but which button clicked??? We don't know
                        PdBase.sendBang("bangSnareDrum");
                    }
                }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        AccelerationSensor aclSensor = new AccelerationSensor(this, 2 * 1000 * 1000);
        aclSensor.subscribeToAccelerationSensor();

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
        File pdPatch = new File(dir, "simpleTest.pd");
        File pdPatchSnareDrum = new File(dir, "drumset.pd");

        PdBase.openPatch(pdPatch.getAbsolutePath());
        PdBase.openPatch(pdPatchSnareDrum.getAbsolutePath());
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
