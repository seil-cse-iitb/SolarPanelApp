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
        fieldIndexMap.put("mm", index++);
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
        for (int i = 0; i < fieldIndexMap.size(); i++) {
            espData.data[i] = Double.parseDouble(split[i]);
        }
        return espData;
    }

    public double getData(String fieldName) {
        return data[fieldIndexMap.get(fieldName)];
    }
}
