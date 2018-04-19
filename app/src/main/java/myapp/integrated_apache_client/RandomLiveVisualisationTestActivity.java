package myapp.integrated_apache_client;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class RandomLiveVisualisationTestActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private LineGraphSeries<DataPoint> mSeries1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_live_visualisation_test);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        mSeries1 = new LineGraphSeries<>(generateData());
        graph.addSeries(mSeries1);
        graph.getViewport().setXAxisBoundsManual(true);
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                mSeries1.appendData(new DataPoint(mSeries1.getHighestValueX()+1, getRandom()), true, 100);
                mHandler.postDelayed(this, 200);
            }
        };
        mHandler.postDelayed(mTimer1, 1000);

    }

        @Override
        public void onResume() {
            super.onResume();
//            mTimer1 = new Runnable() {
//                @Override
//                public void run() {
//                    mSeries1.resetData(generateData());
//                    mHandler.postDelayed(this, 300);
//                }
//            };
//            mHandler.postDelayed(mTimer1, 300);

         }

        @Override
        public void onPause() {
            mHandler.removeCallbacks(mTimer1);
            super.onPause();
        }

        private DataPoint[] generateData() {
            int count = 30;
            DataPoint[] values = new DataPoint[count];
            for (int i=0; i<count; i++) {
                double x = i;
                double f = mRand.nextDouble()*0.15+0.3;
                double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
                DataPoint v = new DataPoint(x, y);
                values[i] = v;
            }
            return values;
        }

        double mLastRandom = 2;
        Random mRand = new Random();
        private double getRandom() {
            return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
        }

    public void updateGraph(View view) {
        mSeries1.resetData(generateData());
    }
}
