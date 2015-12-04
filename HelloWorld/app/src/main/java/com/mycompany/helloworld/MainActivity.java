package com.mycompany.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;

public class MainActivity extends Activity {

    private TextView serverStatus;
    private ImageView image;

    // DEFAULT IP
    public static String SERVERIP = "189.35.189.32";

    // DESIGNATE A PORT
    public static final int SERVERPORT = 12345;

    private Handler handler = new Handler();

    private ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serverStatus = (TextView) findViewById(R.id.server_status);
        image = (ImageView) findViewById(R.id.imageView);
        //SERVERIP = getLocalIpAddress();

        Thread fst = new Thread(new ServerThread());
        fst.start();
    }

    public class ServerThread implements Runnable {

        public void run() {
            try {
                if (SERVERIP != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Listening on IP: " + SERVERIP);
                            System.out.println("Listening on IP: " + SERVERIP);
                        }
                    });
                    serverSocket = new ServerSocket(SERVERPORT);
                    while (true) {
                        // LISTEN FOR INCOMING CLIENTS
                        System.out.println("Esperando clientes");
                        Socket client = serverSocket.accept();
                        InputStream is = client.getInputStream();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatus.setText("Connected.");
                                System.out.println("Connected.");
                            }
                        });
                        while( true) {
                            try {
                                int filesize = 65383;
                                int bytesRead;
                                byte[] mybytearray2 = new byte[filesize];
                                bytesRead = is.read(mybytearray2,0,mybytearray2.length);
                                System.out.println("ByteImage Tamanho " + bytesRead);
                                Bitmap bmp = BitmapFactory.decodeByteArray(mybytearray2, 0, mybytearray2.length);
                                image.setImageBitmap(bmp);
                            /*
                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            String line = null;
                            while ((line = in.readLine()) != null) {
                                Log.d("ServerActivity", line);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // DO WHATEVER YOU WANT TO THE FRONT END
                                        // THIS IS WHERE YOU CAN BE CREATIVE
                                    }
                                });
                            }
                            */
                                break;
                            } catch (Exception e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        serverStatus.setText("Oops. Connection interrupted. Please reconnect your phones.");
                                        System.out.println("Oops. Connection interrupted. Please reconnect your phones.");
                                    }
                                });
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Couldn't detect internet connection.");
                            System.out.println("Couldn't detect internet connection.");
                        }
                    });
                }
            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Error");
                        System.out.println("Error");
                    }
                });
                e.printStackTrace();
            }
        }
    }

    // GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) { return inetAddress.getHostAddress(); }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
/*
public class MainActivity extends AppCompatActivity {


    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket socket;
    private ServerSocket serverscoket;
    private byte [] image;

    String[] web = {
            "Google Plus",
            "Twitter",
            "Windows",
            "Bing",
            "Itunes",
            "Wordpress"
    } ;
    Integer[] imageId = {
            R.drawable.cam2,
            R.drawable.cam2,
            R.drawable.cam2,
            R.drawable.cam2,
            R.drawable.cam2,
            R.drawable.cam2,

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView list;

        //Bitmap bpm = BitmapFactory.decodeFile("@drawable/cam2");

    CustomList adapter = new CustomList(MainActivity.this, web, imageId);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();

            }
        });

        //calculate how many bytes our image consists of.
        //int bytes = bpm.getByteCount();

        //ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
        //bpm.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

        //byte[] array = buffer.array(); //Get the underlying array containing the data.




        try {
            serverscoket = new ServerSocket(12348);
            System.out.println("Server on");
            socket = serverscoket.accept();
            System.out.println("Just connected to "
                    + socket.getRemoteSocketAddress());
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //editTextAddress = (EditText)findViewById(R.id.ipField);
        //editTextPort = (EditText)findViewById(R.id.portField);
        //buttonConnect = (Button)findViewById(R.id.button);
        /*
        try {
            socket = new Socket("172.26.213.241",5002);
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


        new Thread () {
            public void run() {
                try {

                    while (true) {
                        if (Thread.interrupted()) {
                            break;
                        }
                        // Le o vetor index recebido
                        image = (byte[])input.readObject();
                    }
                } catch (Exception e) {
                    System.out.println("Algo de errado aconteceu!! " + e.getMessage());
                }
            }

        }.start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
*/