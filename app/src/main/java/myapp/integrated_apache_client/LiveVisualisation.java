package myapp.integrated_apache_client;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import model.ESPData;

public class LiveVisualisation extends BaseActivity {
    ListView lvEsp;
    BaseAdapter ba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_live_visualisation);
        super.onCreate(savedInstanceState);
        lvEsp = (ListView) findViewById(R.id.lvEsp);
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
                ((TextView) convertView.findViewById(R.id.name)).setText(esp.getName());

                final View finalConvertView = convertView;
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final GraphView graph = (GraphView) finalConvertView.findViewById(R.id.graph);
                        final LineGraphSeries<DataPoint> series = new LineGraphSeries<>(esp.getDataPointArray("temp"));
                        graph.getViewport().setXAxisBoundsManual(true);
                        graph.addSeries(series);
                        Thread liveDataThread =  new Thread(new Runnable() {
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
                                        makeToast("Live streaming starts!" + esp.getName());
                                        while(true){
                                            try {
                                                    String line = br.readLine();
                                                    makeToast(line);
                                                    ESPData espData= ESPData.buildESPData(line);
                                                    esp.espDataArrayList.add(espData);
                                                    series.appendData(new DataPoint(series.getHighestValueX()+1,Double.parseDouble(espData.getTemp())),true,100);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                                break;
                                            }
                                        }
                                    } else {
                                        makeToast("Live streaming fails!" + esp.getName());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    makeToast("Live Stream failed!! Name:" + esp.getName(), Toast.LENGTH_LONG);
                                }
                            }
                        });
                        //show live data graph
                        if (graph.getVisibility() == View.VISIBLE) {
                            graph.setVisibility(View.INVISIBLE);
                            liveDataThread.interrupt();
                        } else {
                            graph.setVisibility(View.VISIBLE);
                            liveDataThread.start();
                        }
                    }
                });
                System.out.println("Showing ESP: " + esp);
                return convertView;
            }
        };
        lvEsp.setAdapter(ba);
    }

    @Override
    protected void onStop() {

        super.onStop();
    }
}
