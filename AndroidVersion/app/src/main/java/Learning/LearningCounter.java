

package Learning;


import Learning.ClusterableDoublePoint;
import android.os.CountDownTimer;
import android.widget.TextView;
import com.example.bluefish.anydrum.EnumDrum;
import com.example.bluefish.anydrum.MainActivity;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.puredata.core.PdBase;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class LearningCounter {
    public List<Cluster<ClusterableDoublePoint>> cluster;
    private int currentCount = 0;
    private TextView refTextView;
    private EnumDrum enumDrum;
    private MainActivity refMain;
    private int startArduinoL, startArduinoR ;
    private Vector<ClusterableDoublePoint> listSnsorData;
    public boolean isLearned = false;

    public LearningCounter(TextView refTextView, int counts,  MainActivity refMain, final EnumDrum enumDrum)
    {
        listSnsorData = new Vector<>();
//        final int startACL = refMain.getAclSensor().getListOfSensorDataFiltered().size();
          startArduinoL = refMain.arduinoUSB.getListOfSensorDataArduinoL().size();
          startArduinoR = refMain.arduinoUSB.getListOfSensorDataArduinoR().size();

        currentCount = counts;
        this.enumDrum = enumDrum;
        this.refMain = refMain;
        this.refTextView = refTextView;
        refTextView.setText(String.valueOf(currentCount));
    }

    public int reduceCounter(ClusterableDoublePoint knock)
    {
        listSnsorData.add(knock);
        --currentCount;
        refTextView.setText(String.valueOf(currentCount));
        if(currentCount <= 0)
            onFinish();
        return currentCount;
    }

    private void onFinish() {
        isLearned = true;
//        switch (enumDrum)
//        {
//            case HIHAT:
////                        listSensorData=refMain.getAclSensor().getListOfSensorDataFiltered();
////                        sizeOfStart = startACL;
//                break;
//            case SNARE:
//                listSensorData=refMain.arduinoUSB.getListOfSensorDataArduinoL();
//                sizeOfStart = startArduinoR;
//                break;
//            case BASS:
//                listSensorData=refMain.arduinoUSB.getListOfSensorDataArduinoR();
//                sizeOfStart = startArduinoL;
//                break;
//        }


//        for(int i = 0; i<listSnsorData.size(); ++i)
//            listSnsorData.get(i).getPoint()[0] = 0;

        cluster = new ArrayList<>();
        cluster = refMain.getDbscan().cluster(listSnsorData);

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


}

