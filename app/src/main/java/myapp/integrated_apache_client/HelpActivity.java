package myapp.integrated_apache_client;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_help);
        super.onCreate(savedInstanceState);
        ListView instructions= (ListView) findViewById(R.id.instructions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Help");
        String[] instructionsList = new String[]{
                "1. During wifi mode, reach near the circuits as close as possible. Note - Wifi data transfer timing - 7 AM to 8 PM",
                "2. Turn On the hotspot. Wait for 5 minutes(it will check for wifi once in 5 minutes)",
                "3. If any node is detected, keep pressing refresh button till the downloading starts.",
                "4. Downloaded file will be stored in \"Download\" folder of the internal storage ",
                "5. Please mail the data file by selecting them and share via gmail to/for any queries - 134420002@iitb.ac.in/santhoshjois@gmail.com"
        };

        instructions.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,instructionsList));
    }
}
