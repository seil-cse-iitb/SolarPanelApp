package myapp.integrated_apache_client;

import com.jjoe64.graphview.series.DataPoint;

import java.io.Serializable;
import java.util.ArrayList;

import model.ESPData;

/**
 * Created by ABC on 9/13/2017.
 */

public class ESP implements Serializable {
    long id = (long) Math.random();
    String ipAddress;
    String macAddress;
    String name;
    long downloadedContentLength = 0;
    long maxContentLength = 0;
    boolean downloadStarted = false;
    boolean fileDownloaded = false;
    boolean isDynamicName = false;

    ArrayList<ESPData> espDataArrayList = new ArrayList<>();

    public DataPoint[] getDataPointArray(String fieldName) {
        int size = espDataArrayList.size();
        DataPoint[] dataPoints = new DataPoint[size];
        for (int x = 0; x < size; x++) {
            double y = espDataArrayList.get(x).getData(fieldName);
            dataPoints[x] = new DataPoint(x, y);
        }
        return dataPoints;
    }

    public void addDataPoint(ESPData data){
        espDataArrayList.add(data);
    }

    public boolean isDynamicName() {
        return isDynamicName;
    }

    public void setDynamicName(boolean dynamicName) {
        isDynamicName = dynamicName;
    }

    public boolean isFileDownloaded() {
        return fileDownloaded;
    }

    public void setFileDownloaded(boolean fileDownloaded) {
        this.fileDownloaded = fileDownloaded;
    }

    public long getDownloadedContentLength() {
        return downloadedContentLength;
    }

    public void setDownloadedContentLength(long downloadedContentLength) {
        this.downloadedContentLength = downloadedContentLength;
    }

    public long getMaxContentLength() {
        return maxContentLength;
    }

    public void setMaxContentLength(long maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    public ESP() {
    }

    @Override
    public String toString() {
        return "ESP{" +
                "ipAddress='" + ipAddress + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public ESP(String ipAddress, String macAddress) {
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        String[] ipArray = ipAddress.split("\\.");
        this.name = "Node (" + ipArray[3] + ")";
    }


    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
