package myapp.integrated_apache_client;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends BaseActivity {
    ListView lvEsp;
    BaseAdapter ba;
    static Handler handler;
    final static int INCREMENT_PROGRESSBAR = 11;
    final static int REFRESH_LIST = 12;
    final static int FILE_DOWNLOAD = 13;
    final static int HOTSPOT_OFF = 11;
    final static int HOTSPOT_ON = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        begin();
        if (espConnected == null) {
            espConnected = new ArrayList<>();
        }
        lvEsp = (ListView) findViewById(R.id.lvEsp);
        final Button btnRefresh = (Button) findViewById(R.id.btn_refresh);
        String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
        int permsRequestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(perms, permsRequestCode);
        }
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                int messageType = data.getInt("messageType");
                switch (messageType) {
                    case INCREMENT_PROGRESSBAR:
                        ba.notifyDataSetChanged();
                        break;
                    case REFRESH_LIST:
                        ba.notifyDataSetChanged();
                        break;
                    case FILE_DOWNLOAD:
                        boolean success = data.getBoolean("success");
                        String fileName = data.getString("fileName");
                        if (success) {
                            Toast.makeText(getApplicationContext(), "File Downloaded (in Downloads folder):\n FileName: " + fileName, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "File Download Failed!!\n FileName: " + fileName, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (refreshThread != null && !refreshThread.isAlive())
                    if (refreshThread != null && !refreshThread.isAlive())
                        refreshThread.start();
                    else if (refreshThread == null) {
                        refreshThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (true) {
                                    getListOfConnectedDevice();
                                    try {
                                        Thread.sleep(5000);
                                    } catch (InterruptedException e) {
                                        break;
                                    }
                                }
                            }
                        });
                    }
            }
        });
        //logic to handle download button
        ba = new BaseAdapter() {
            @Override
            public int getCount() {
                return espConnected.size();
            }

            @Override
            public ESP getItem(int position) {
                return espConnected.get(position);
            }

            @Override
            public long getItemId(int position) {
                return getItem(position).id;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final ESP esp = getItem(position);
                convertView = getLayoutInflater().inflate(R.layout.my_list_item, null);
                ((TextView) convertView.findViewById(R.id.tvName)).setText(esp.getName());
                ((TextView) convertView.findViewById(R.id.tvIP)).setText(esp.getIpAddress());
                ((TextView) convertView.findViewById(R.id.tvFileSize)).setText("(" + esp.getMaxContentLength() + " bytes)");
                ProgressBar pb = (ProgressBar) convertView.findViewById(R.id.pbDownload);
                pb.setMax((int) esp.getMaxContentLength());
                pb.setProgress((int) esp.getDownloadedContentLength());
                Button btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
                Button btnDebugESP = (Button) convertView.findViewById(R.id.btnDebugESP);
                btnDebugESP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Debug ESP");
                        final EditText input = new EditText(MainActivity.this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
                        builder.setView(input);
                        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String debugText = input.getText().toString();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        HttpClient httpclient = new DefaultHttpClient();
                                        String encodedDebugText = "";
                                        try {
                                            encodedDebugText = URLEncoder.encode(debugText, "UTF-8");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                        HttpGet httpGet = new HttpGet("http://" + esp.getIpAddress() + "/debug?" + encodedDebugText);
                                        try {
                                            HttpResponse response = httpclient.execute(httpGet);
                                            HttpEntity entity = response.getEntity();
                                            if (entity != null) {
                                                makeToast("Debug request sent!! Name:" + esp.getName(), Toast.LENGTH_LONG);
                                                final String fileName = "/storage/emulated/0/Download/" + esp.getName() + "_" + esp.getMacAddress() + "_debugData.txt";
                                                final File file = new File(fileName);
                                                String debugData = getStringFromInputStream(entity.getContent());
                                                if(copyInputStreamToFile(new ByteArrayInputStream(debugData.getBytes(Charset.forName("UTF-8"))),file, esp)){
                                                    makeDialog(debugData);
                                                }else{
                                                    makeDialog("Could not save debug data into file!!\n"+debugData);
                                                }
                                            } else {
                                                makeToast("Debug request failed!! Name:" + esp.getName(), Toast.LENGTH_LONG);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            makeToast("Debug request failed!! Name:" + esp.getName()+" Exception: "+e.getMessage(), Toast.LENGTH_LONG);
                                        }
                                        if (refreshThread != null && !refreshThread.isAlive())
                                            refreshThread.start();
                                    }
                                }).start();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshThread.interrupt();
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Delete SolarData.txt")
                                .setMessage("Do you really want to delete file from ESP (" + esp.getName() + ") ?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                HttpClient httpclient = new DefaultHttpClient();
                                                HttpGet httpGet = new HttpGet("http://" + esp.getIpAddress() + "/delete?");
                                                try {
                                                    HttpResponse response = httpclient.execute(httpGet);
                                                    HttpEntity entity = response.getEntity();
                                                    if (entity != null) {
                                                        makeToast("Delete Success!! Name:" + esp.getName(), Toast.LENGTH_LONG);
                                                    } else {
                                                        makeToast("Delete Failed!! Name:" + esp.getName(), Toast.LENGTH_LONG);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    makeToast("Delete Failed!! Name:" + esp.getName(), Toast.LENGTH_LONG);
                                                }
                                                if (refreshThread != null && !refreshThread.isAlive())
                                                    refreshThread.start();
                                            }
                                        }).start();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (refreshThread != null && !refreshThread.isAlive())
                                            refreshThread.start();

                                    }
                                }).show();
                    }
                });
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!esp.downloadStarted) {
                            //Download file
                            downloadFile(esp, false);
                        } else {
                            makeToast("Download already started!!", Toast.LENGTH_SHORT);
                        }
                    }
                });
                System.out.println("Showing ESP: " + esp);
                return convertView;
            }
        };
        lvEsp.setAdapter(ba);
        if (refreshThread != null && !refreshThread.isAlive())
            refreshThread.start();
    }

    Thread refreshThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                getListOfConnectedDevice();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    });
    public static String getStringFromInputStream(InputStream inputStream) throws IOException
    {
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        for (String line; (line = r.readLine()) != null; ) {
            total.append(line).append('\n');
        }
        return total.toString();
    }
    private String getDynamicName(ESP esp) throws IOException {
        if (esp.isDynamicName()) return esp.name;
        String dynamicName = null;
        HttpClient httpclient = new DefaultHttpClient();
        String url = "http://" + esp.getIpAddress() + "/id?";
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = null;
        response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream inputStream;
            inputStream = entity.getContent();
            Scanner sc = new Scanner(inputStream);
            dynamicName = sc.nextLine();
            System.out.println(dynamicName);
        }
        return dynamicName;
    }

    public void getListOfConnectedDevice() {
        BufferedReader br = null;
        boolean isFirstLine = true;
        final ArrayList<ESP> espConnectedTemp = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ipAddress = splitted[0];
                    String macAddress = splitted[3];
                    boolean isReachable = InetAddress.getByName(
                            splitted[0]).isReachable(500);  // this is network call so we cant do that on UI thread, so i take background thread.
                    if (isReachable) {
                        Log.d("Device Information", ipAddress + " : "
                                + macAddress);
                        boolean connectedBefore = false;
                        for (int i = 0; i < espConnected.size(); i++) {
                            if (espConnected.get(i).ipAddress.equals(ipAddress)) {
                                if (!espConnected.get(i).downloadStarted) {
                                    String dynamicName = null;
                                    try {
                                        dynamicName = getDynamicName(espConnected.get(i));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        dynamicName = null;
                                    }
                                    if (dynamicName != null) {
                                        espConnected.get(i).name = dynamicName;
                                        espConnected.get(i).setDynamicName(true);
                                        downloadFile(espConnected.get(i), true);
                                    }
                                }
                                espConnectedTemp.add(espConnected.get(i));
                                connectedBefore = true;
                            }
                        }
                        if (!connectedBefore) {
                            final ESP esp = new ESP(ipAddress, macAddress);
                            String dynamicName = null;
                            try {
                                dynamicName = getDynamicName(esp);
                            } catch (Exception e) {
                                e.printStackTrace();
                                dynamicName = null;
                            }
                            if (dynamicName != null) {
                                esp.name = dynamicName;
                                esp.setDynamicName(true);
                                downloadFile(esp, true);
                            }
                            espConnectedTemp.add(esp);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                espConnected.clear();
                espConnected.addAll(espConnectedTemp);
                ba.notifyDataSetChanged();
            }
        });
        Bundle b = new Bundle();
        b.putInt("messageType", REFRESH_LIST);
        Message m = new Message();
        m.setData(b);
        handler.sendMessage(m);


    }

    public void downloadFile(final ESP esp, final boolean isAutoDownload) {
        if (isAutoDownload) {
            if (esp.isFileDownloaded()) {
                return;
            }
        }
        if (esp.downloadStarted) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat formatter = new SimpleDateFormat("dd_MMM");
                formatter.setLenient(false);
                Date today = new Date();
                String date = formatter.format(today);
                final String fileName = "/storage/emulated/0/Download/" + esp.getName() + "_" + date + "_" + esp.getMacAddress() + "_solarData.txt";
                final File file = new File(fileName);
                esp.downloadStarted = true;
                try {
                    esp.setDownloadedContentLength(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ba.notifyDataSetChanged();
                        }
                    });
                    String timestamp = "";
                    HttpClient httpclient = new DefaultHttpClient();
                    timestamp = time_stamp();
                    String url = "http://" + esp.getIpAddress() + "/solarData.txt?" + timestamp;
//            String url = "http://" + esp.getIpAddress() + "/solarData.txt";
//            String url = "https://www.realvnc.com/download/file/vnc.files/VNC-Server-6.2.0-Windows.exe";
                    HttpGet httpget = new HttpGet(url);
                    HttpResponse response = httpclient.execute(httpget);
                    HttpEntity entity = response.getEntity();
                    long contentLength = Long.parseLong(response.getFirstHeader("Content-Length").getValue());
                    esp.setMaxContentLength(contentLength);
                    if (isAutoDownload) {
                        if (contentLength <= file.length()) {
                            esp.setDownloadedContentLength(contentLength);
                            esp.setFileDownloaded(true);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Already downloaded:\n FileName: " + file.getName(), Toast.LENGTH_SHORT).show();
                                    ba.notifyDataSetChanged();
                                }
                            });
                            return;
                        }
                    }
                    if (entity != null) {
                        InputStream inputStream = entity.getContent();
                        if (copyInputStreamToFile(inputStream, file, esp)) {
                            Message msg = new Message();
                            Bundle b = new Bundle();
                            b.putString("fileName", file.getName());
                            b.putBoolean("success", true);
                            b.putInt("messageType", FILE_DOWNLOAD);
                            msg.setData(b);
                            handler.sendMessage(msg);
                            esp.setFileDownloaded(true);
                        } else {
                            Message msg = new Message();
                            Bundle b = new Bundle();
                            b.putString("fileName", file.getName());
                            b.putBoolean("success", false);
                            b.putInt("messageType", FILE_DOWNLOAD);
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    makeToast("Download Failed!! " + esp.getName(), Toast.LENGTH_SHORT);
                } finally {
                    esp.downloadStarted = false;
                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        boolean writeAccepted = false;
        switch (permsRequestCode) {
            case 200:
                writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!writeAccepted) {
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(perms, permsRequestCode);
            }
        }
    }

    private static boolean copyInputStreamToFile(InputStream in, File file, ESP esp) {
        OutputStream out = null;
        boolean success = false;
        try {
            int countByte = 0;
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len, i = 0;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                out.flush();
                countByte += len;
                esp.downloadedContentLength += len;
                i++;
                if (i % 50 == 0) {
                    Message msg = new Message();
                    Bundle b = new Bundle();
                    b.putInt("messageType", INCREMENT_PROGRESSBAR);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }
            }
            out.flush();
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putInt("messageType", INCREMENT_PROGRESSBAR);
            esp.downloadedContentLength += len;
            msg.setData(b);
            handler.sendMessage(msg);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            // Ensure that the InputStreams are closed even if there's an exception.
            try {
                if (out != null) {
                    out.close();
                }

                // If you want to close the "in" InputStream yourself then remove this
                // from here but ensure that you close it yourself eventually.
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    private String time_stamp() {

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        int timestamp = convertStringToInt(ts);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000L);
        String date = DateFormat.format("ddHHmm", cal).toString();
        return date;
    }

    public int convertStringToInt(String s) {
        int myNum = 0;
        try {
            myNum = Integer.parseInt(s);
        } catch (NumberFormatException nfe) {

        }
        return myNum;

    }

    private int getHotspotActualStatus() {
        int actualState = 0;
        WifiManager wifimanager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("getWifiApState");
            method.setAccessible(true);
            actualState = (Integer) method.invoke(wifimanager, (Object[]) null);
        } catch (Exception e) {
        }
        return actualState;
    }

    void begin() {
        final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.button_toggle);
        if (getHotspotActualStatus() == HOTSPOT_ON) {
            toggleButton.setChecked(true);
        }
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int actualState = getHotspotActualStatus();
                if (isChecked) {
                    if (actualState == HOTSPOT_OFF) { //if actual state is off
                        switchHotspot(true);
                    } else {
                        Toast.makeText(MainActivity.this, "Hotspot is being OFF!", Toast.LENGTH_SHORT).show();
                        toggleButton.setChecked(false);
                    }
                } else {
                    if (actualState == HOTSPOT_ON) { //if actual state is on
                        switchHotspot(false);
                    } else {
                        Toast.makeText(MainActivity.this, "Hotspot is being ON!", Toast.LENGTH_SHORT).show();
                        toggleButton.setChecked(true);
                    }
                }
            }
        });
    }

    private void switchHotspot(boolean onOff) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent in = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                in.setData(Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(in, 1);
                return;
            }
        }
        WifiManager wifimanager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration apConfig = null;
        try {
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            method.invoke(wifimanager, apConfig, onOff);
        } catch (Exception e) {
            e.printStackTrace();
            makeToast(e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onStop() {
        if (refreshThread.isAlive())
            refreshThread.interrupt();
        super.onStop();
    }
}