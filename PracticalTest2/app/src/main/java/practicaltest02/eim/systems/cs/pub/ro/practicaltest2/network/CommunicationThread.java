package practicaltest02.eim.systems.cs.pub.ro.practicaltest2.network;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import practicaltest02.eim.systems.cs.pub.ro.practicaltest2.general.Constants;
import practicaltest02.eim.systems.cs.pub.ro.practicaltest2.general.Utilities;

public class CommunicationThread extends Thread{

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }

        try {

            // READ si WRITE de pe canalul de com
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (word )!");

            String word =  bufferedReader.readLine();
            if (word == null || word.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (word)!");
                return;
            }

            // date de primit de la server
            HashMap<String, ArrayList<String>> data = serverThread.getData();
            ArrayList lista = null;
            String dataAnagram = null;

            //cache
            if (data.containsKey(word)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                lista = data.get(word);
                dataAnagram = lista.toString();
            }
            // nu este in cache
            else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + word);
                Log.e(Constants.TAG, "GET: " + Constants.WEB_SERVICE_ADDRESS + word);

                // raspuns
                String pageSourceCode= "";
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                pageSourceCode = httpClient.execute(httpGet, responseHandler);
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }

                lista = new ArrayList();

                // parcurgere json pentru extragere date
                JSONObject obj_json = null;
                obj_json = XML.toJSONObject(pageSourceCode);
                JSONObject arrayOfString = obj_json.getJSONObject(Constants.ARRAY_OF_STRING);
                String data_string = arrayOfString.getString("string") ;
                String[] anagramArray = data_string.split(",|\\[|\\]");

                for (String str : anagramArray)
                    if (!str.isEmpty())
                        lista.add(str);

                serverThread.setData(word, lista);
                Log.e(Constants.TAG, lista.toString());

                dataAnagram = lista.toString();
            }
            printWriter.println(dataAnagram);
            printWriter.flush();

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch (JSONException jsonException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
            if (Constants.DEBUG) {
                jsonException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }


    }


}
