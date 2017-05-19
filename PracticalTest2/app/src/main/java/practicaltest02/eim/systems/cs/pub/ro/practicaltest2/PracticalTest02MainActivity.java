package practicaltest02.eim.systems.cs.pub.ro.practicaltest2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import practicaltest02.eim.systems.cs.pub.ro.practicaltest2.general.Constants;
import practicaltest02.eim.systems.cs.pub.ro.practicaltest2.network.ClientThread;
import practicaltest02.eim.systems.cs.pub.ro.practicaltest2.network.ServerThread;

public class PracticalTest02MainActivity extends AppCompatActivity {

    // server widgets
    private EditText port = null;
    private Button startServerButton = null;

    // client widgets
    private EditText cuvantClient = null;
    private Button getWord = null;
    private TextView allAnagramShow = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = port.getText().toString();
            if (serverPort == null || serverPort.isEmpty() ) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            serverThread = new ServerThread(Integer.parseInt(serverPort));
            /*if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            } */
            serverThread.start();
        }
    }

    private getAnagramClickListener getAnagram = new getAnagramClickListener();
    private class getAnagramClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {

            String clientAddress = "localhost";
            String clientPort = port.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String word = cuvantClient.getText().toString();
            if (word == null || word.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameter from client (anagram) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            allAnagramShow.setText(Constants.EMPTY_STRING);
            clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort),word, allAnagramShow);
            clientThread.start();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");
        setContentView(R.layout.activity_practical_test02_main);

        /*
             private EditText port = null;
    private Button startServerButton = null;

    // client widgets
    private EditText cuvantClient = null;
    private Button getWord = null;
    private EditText allAnagramShow = null;

         */

        // server
        port = (EditText) findViewById(R.id.port);
        startServerButton = (Button) findViewById(R.id.start_server);
        startServerButton.setOnClickListener(connectButtonClickListener);

        // client
        cuvantClient = (EditText) findViewById(R.id.cuvant_client);
        getWord = (Button)findViewById(R.id.get);
        getWord.setOnClickListener(getAnagram);
        allAnagramShow = (TextView) findViewById(R.id.allAnagram);

    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
