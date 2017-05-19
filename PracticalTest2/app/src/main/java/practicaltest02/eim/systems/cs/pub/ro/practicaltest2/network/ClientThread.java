package practicaltest02.eim.systems.cs.pub.ro.practicaltest2.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import practicaltest02.eim.systems.cs.pub.ro.practicaltest2.general.Constants;
import practicaltest02.eim.systems.cs.pub.ro.practicaltest2.general.Utilities;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String word;
    private TextView allAnagramShow;

    private Socket socket;

    public ClientThread(String address, int port, String word, TextView allAnagramShow) {
        this.address = address;
        this.port = port;
        this.word = word;
        this.allAnagramShow = allAnagramShow;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(word);
            printWriter.flush();

            String allAnagram;
            while ((allAnagram = bufferedReader.readLine()) != null) {
                final String finalAllAnagram = allAnagram;
                allAnagramShow.post(new Runnable() {
                    @Override
                    public void run() {
                        allAnagramShow.setText(finalAllAnagram);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }

    }

}
