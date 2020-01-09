package myapp.integrated_apache_client;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;

import model.ESPData;

public class LiveVisualisation extends BaseActivity {
    ListView lvEsp;
    BaseAdapter ba;
    GraphView graph;
    Spinner spField;
    HashMap<String, LineGraphSeries<DataPoint>> seriesMap = new HashMap<>();
    HashMap<String, Thread> liveDataThreads = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_live_visualisation);
        super.onCreate(savedInstanceState);
        graph = (GraphView) findViewById(R.id.graph);
        lvEsp = (ListView) findViewById(R.id.lvEsp);
        spField = (Spinner) findViewById(R.id.spField);
        spField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (ESP esp : espConnected) {
                    seriesMap.get(esp.getMacAddress()).resetData(esp.getDataPointArray(spField.getSelectedItem() + ""));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Viewport viewport = graph.getViewport();
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(30);
        viewport.setScalable(true);
        graph.getGridLabelRenderer().setLabelVerticalWidth(100);
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
                convertView = getLayoutInflater().inflate(R.layout.my_live_list_item, null);
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                CheckBox cbESPName = (CheckBox) convertView.findViewById(R.id.cbESPName);
                cbESPName.setText(esp.getName());

                LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
                series.setColor(color);
                cbESPName.setTextColor(color);

                seriesMap.put(esp.getMacAddress(), series);
                cbESPName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            graph.addSeries(seriesMap.get(esp.getMacAddress()));
                        } else {
                            graph.removeSeries(seriesMap.get(esp.getMacAddress()));
                        }
                    }
                });
                if (liveDataThreads.get(esp.getMacAddress()) == null) {
                    Thread liveDataThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpGet httpGet = new HttpGet("http://" + esp.getIpAddress() + "/liveData");
                            try {
                                HttpResponse response = httpclient.execute(httpGet);
                                HttpEntity entity = response.getEntity();
                                if (entity != null) {
                                    InputStream in = entity.getContent();
                                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                                    makeToast("Live streaming starts! Name:" + esp.getName());
                                    while (true) {
                                        try {
                                            String line = br.readLine();
                                            makeToast(line);
                                            storeLiveData(esp, line);
                                            ESPData espData = ESPData.buildESPData(line);
                                            esp.addDataPoint(espData);
                                            LineGraphSeries<DataPoint> series = seriesMap.get(esp.getMacAddress());
                                            series.appendData(new DataPoint(series.getHighestValueX() + 1, espData.getData(spField.getSelectedItem() + "")), true, 300);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            break;
                                        }
                                    }
                                } else {
                                    makeToast("Live streaming failed!! Name:" + esp.getName());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                makeToast("Live Stream failed!! Name:" + esp.getName(), Toast.LENGTH_LONG);
                            }
                        }
                    });
                    liveDataThreads.put(esp.getMacAddress(), liveDataThread);
                }
                if (!liveDataThreads.get(esp.getMacAddress()).isAlive()) {
                    liveDataThreads.get(esp.getMacAddress()).start();
                }

                System.out.println("Showing ESP: " + esp);
                return convertView;
            }
        };
        lvEsp.setAdapter(ba);
    }

    public void storeLiveData(final ESP esp, final String line) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat formatter = new SimpleDateFormat("dd_MMM");
                formatter.setLenient(false);
                Date today = new Date();
                String date = formatter.format(today);
                final String fileName = "/storage/emulated/0/Download/" + esp.getName() + "_" + date + "_" + esp.getMacAddress() + "_liveSolarData.txt";
                final File file = new File(fileName);
                try {
                    FileWriter fw = new FileWriter(file,true);
                    fw.append(line);
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    protected void onStop() {

        super.onStop();
    }
}
