package model;

import com.jjoe64.graphview.series.DataPoint;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by seil on 12/4/18.
 */

public class ESPData implements Serializable {
    double[] data;
    public static HashMap<String, Integer> fieldIndexMap = new HashMap<>();

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

    public static ESPData buildESPData(String line) {
        String[] split = line.split(",");
        ESPData espData = new ESPData();
        int length = fieldIndexMap.size();
        espData.data = new double[length];
        int dataI=0,splitI=0;
        espData.data[dataI++] = Double.parseDouble(split[splitI++]);
        espData.data[dataI++] = Double.parseDouble(split[splitI].substring(0,2));
        espData.data[dataI++] = Double.parseDouble(split[splitI].substring(2,4));
        espData.data[dataI++] = Double.parseDouble(split[splitI].substring(4,6));
        espData.data[dataI++] = Double.parseDouble(split[splitI].substring(6,8));
        espData.data[dataI++] = Double.parseDouble(split[splitI++].substring(8,10));
        for (; dataI < length; splitI++,dataI++) {
            espData.data[dataI] = Double.parseDouble(split[splitI]);
        }
        return espData;
    }

    public double getData(String fieldName) {
        return data[fieldIndexMap.get(fieldName)];
    }
}
