package AsyncTasks;

import android.os.AsyncTask;
import android.widget.TextView;
import com.example.bluefish.anydrum.MainActivity;
import com.example.bluefish.anydrum.R;

public class ShowInfoMsgTask extends AsyncTask<String, Integer, String> {
    String newMsg="";
    protected String doInBackground(String ... msg) {

        newMsg = msg.toString();
        return newMsg;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(MainActivity refMain) {
        TextView info= (TextView) refMain.findViewById(R.id.viewInfo);
        info.setText(newMsg);
    }
}
