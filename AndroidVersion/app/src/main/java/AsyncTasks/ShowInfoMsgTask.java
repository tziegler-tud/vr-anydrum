package AsyncTasks;

import android.os.AsyncTask;
import android.widget.TextView;
import com.example.bluefish.anydrum.ArduinoUSB;
import com.example.bluefish.anydrum.MainActivity;
import com.example.bluefish.anydrum.R;

public class ShowInfoMsgTask extends AsyncTask<Byte[], Integer, Integer> {

    protected Integer doInBackground(Byte[]... data) {

        return 0;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(int val) {
    }

}
