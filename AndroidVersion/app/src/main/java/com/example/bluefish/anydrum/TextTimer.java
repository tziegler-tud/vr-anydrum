package com.example.bluefish.anydrum;

import Learning.ClusterableDoublePoint;
import android.os.CountDownTimer;
import android.widget.TextView;
import org.apache.commons.math3.ml.clustering.Cluster;

import java.util.List;

public class TextTimer {

    public TextTimer(final TextView refTextView, int seconds, final MainActivity refMain)
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

                List<Cluster<ClusterableDoublePoint>> cluster = refMain.getAclSensor().clusterLastValuesSinceStart(sublist);
               /* for(int i = 0; i<cluster.size(); ++i)
                {
                    for(int j = 0; j<cluster.get(i).getPoints().size(); ++j))
                    {
                        .
                    }
                }*/
                refTextView.setText("done!");
                refTextView.setText(String.valueOf("cl: "+cluster.size()));
            }

        }.start();

    }
}
