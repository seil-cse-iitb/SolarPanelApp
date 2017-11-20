package myapp.integrated_apache_client;
import java.io.Serializable;

/**
 * Created by ABC on 9/13/2017.
 */

public class ESP implements Serializable {
    long id = (long)Math.random();
    String ipAddress;
    String macAddress;
    String name;
    int downloadedContentLength=0;
    int maxContentLength=0;
    boolean downloadStarted = false;

    public int getDownloadedContentLength() {
        return downloadedContentLength;
    }

    public void setDownloadedContentLength(int downloadedContentLength) {
        this.downloadedContentLength = downloadedContentLength;
    }

    public int getMaxContentLength() {
        return maxContentLength;
    }

    public void setMaxContentLength(int maxContentLength) {
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
        this.name = "Node("+ipArray[3]+")";
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