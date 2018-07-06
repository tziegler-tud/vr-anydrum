package com.example.bluefish.anydrum;

import Learning.ClusterableDoublePoint;
import android.os.CountDownTimer;
import android.widget.TextView;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.puredata.core.PdBase;

import java.util.List;
import java.util.Vector;

public class LearningTimer {
    public List<Cluster<ClusterableDoublePoint>> cluster;


    public LearningTimer(final TextView refTextView, int seconds, final MainActivity refMain, final EnumDrum enumDrum)
    {
        new CountDownTimer(seconds * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                long secondsRemaining = (millisUntilFinished / 1000);
                refTextView.setText(String.valueOf(secondsRemaining));
            }

            public void onFinish() {
                Vector<ClusterableDoublePoint> listSensorData = new Vector<>();
                switch (enumDrum)
                {
                    case HIHAT:
                        listSensorData=refMain.getAclSensor().getListOfSensorDataFiltered();
                        break;
                    case SNARE:
                        listSensorData=refMain.arduinoUSB.getListOfSensorDataFilteredArduinoR();
                        break;
                    case BASS:
                        listSensorData=refMain.arduinoUSB.getListOfSensorDataFilteredArduinoL();
                        break;
                }
                int sizeOfStart = listSensorData.size();


                int sizeOfEnd = listSensorData.size();
                int numberOfNewValues = sizeOfEnd-sizeOfStart;

                List<ClusterableDoublePoint> sublist = listSensorData.subList(sizeOfStart-1, sizeOfEnd-1);
                for(int i = 0; i<sublist.size(); ++i)
                    sublist.get(i).getPoint()[0] = 0;

                cluster = refMain.getDbscan().cluster(sublist);

                refTextView.setText(String.valueOf("cl: "+cluster.size()));


                switch (enumDrum)
                {
                    case HIHAT:
                        refMain.getDbscan().setClusterHihat(cluster);
                        break;
                    case SNARE:
                        refMain.getDbscan().setClusterSnare(cluster);
                        break;
                    case BASS:
                        refMain.getDbscan().setClusterBass(cluster);
                        break;
                }
            }

        }.start();

    }
}
