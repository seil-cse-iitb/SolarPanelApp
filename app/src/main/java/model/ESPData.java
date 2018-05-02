package model;

import com.jjoe64.graphview.series.DataPoint;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by seil on 12/4/18.
 */

public class ESPData implements Serializable {
    double[] data;
    public static HashMap<String, Integer> fieldIndexMap = new HashMap<>();
    public Date date;
    static {
        int index = 0;
        fieldIndexMap.put("sequence", index++);
        fieldIndexMap.put("MM", index++);
        fieldIndexMap.put("dd", index++);
        fieldIndexMap.put("hh", index++);
        fieldIndexMap.put("mm", index++);
        fieldIndexMap.put("ss", index++);
        fieldIndexMap.put("I1", index++);
        fieldIndexMap.put("V1", index++);
        fieldIndexMap.put("P1", index++);
        fieldIndexMap.put("I2", index++);
        fieldIndexMap.put("V2", index++);
        fieldIndexMap.put("P2", index++);
        fieldIndexMap.put("I3", index++);
        fieldIndexMap.put("V3", index++);
        fieldIndexMap.put("P3", index++);
        fieldIndexMap.put("I4", index++);
        fieldIndexMap.put("V4", index++);
        fieldIndexMap.put("P4", index++);
        fieldIndexMap.put("Vbat", index++);
        fieldIndexMap.put("Temp", index++);
        fieldIndexMap.put("Humid", index++);
        fieldIndexMap.put("UV", index++);
        fieldIndexMap.put("LW", index++);
        fieldIndexMap.put("IR", index++);
    }
    private ESPData(){

    }
    public static ESPData buildESPData(String line) {
        String[] split = line.split(",");
        ESPData espData = new ESPData();
        int length = fieldIndexMap.size();
        espData.data = new double[length];
        int dataI=0,splitI=0;
        espData.data[dataI++] = Double.parseDouble(split[splitI++]);
        int MM = Integer.parseInt(split[splitI].substring(0,2));
        int dd = Integer.parseInt(split[splitI].substring(2,4));
        int hh = Integer.parseInt(split[splitI].substring(4,6));
        int mm = Integer.parseInt(split[splitI].substring(6,8));
        int ss = Integer.parseInt(split[splitI++].substring(8,10));
        espData.data[dataI++] = MM;
        espData.data[dataI++] = dd;
        espData.data[dataI++] = hh;
        espData.data[dataI++] = mm;
        espData.data[dataI++] = ss;
        for (; dataI < length; splitI++,dataI++) {
            espData.data[dataI] = Double.parseDouble(split[splitI]);
        }
        espData.date = new Date(2018,MM,dd,hh,mm,ss);        
        return espData;
    }

    public double getData(String fieldName) {
        return data[fieldIndexMap.get(fieldName)];
    }
}
