package com.example.bluefish.anydrum;

import Learning.ClusterableDoublePoint;
import android.os.CountDownTimer;
import android.widget.TextView;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.puredata.core.PdBase;

import java.util.List;

public class TextTimer {
    public List<Cluster<ClusterableDoublePoint>> cluster;


    public TextTimer(final TextView refTextView, int seconds, final MainActivity refMain, final EnumDrum enumDrum)
    {
        final int sizeOfStart = refMain.getAclSensor().getListOfSensorDataFiltered().size();

        new CountDownTimer(seconds * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                long secondsRemaining = (millisUntilFinished / 1000);
                refTextView.setText(String.valueOf(secondsRemaining));
            }

            public void onFinish() {
                int sizeOfEnd = refMain.getAclSensor().getListOfSensorDataFiltered().size();
                int numberOfNewValues = sizeOfEnd-sizeOfStart;

                List<ClusterableDoublePoint> sublist = refMain.getAclSensor().getListOfSensorDataFiltered().subList(sizeOfStart-1, sizeOfEnd-1);
                for(int i = 0; i<sublist.size(); ++i)
                    sublist.get(i).getPoint()[0] = 0;

                cluster = refMain.getAclSensor().clusterLastValuesSinceStart(sublist);

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
