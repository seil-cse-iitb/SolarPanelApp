package model;

import com.jjoe64.graphview.series.DataPoint;

import java.io.Serializable;

/**
 * Created by seil on 12/4/18.
 */

public class ESPData implements Serializable {
    String sequence;
    String dd;
    String hh;
    String mm;
    String ss;
    String I1;

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getDd() {
        return dd;
    }

    public void setDd(String dd) {
        this.dd = dd;
    }

    public String getHh() {
        return hh;
    }

    public void setHh(String hh) {
        this.hh = hh;
    }

    public String getMm() {
        return mm;
    }

    public void setMm(String mm) {
        this.mm = mm;
    }

    public String getSs() {
        return ss;
    }

    public void setSs(String ss) {
        this.ss = ss;
    }

    public String getI1() {
        return I1;
    }

    public void setI1(String i1) {
        I1 = i1;
    }

    public String getV1() {
        return V1;
    }

    public void setV1(String v1) {
        V1 = v1;
    }

    public String getP1() {
        return P1;
    }

    public void setP1(String p1) {
        P1 = p1;
    }

    public String getI2() {
        return I2;
    }

    public void setI2(String i2) {
        I2 = i2;
    }

    public String getV2() {
        return V2;
    }

    public void setV2(String v2) {
        V2 = v2;
    }

    public String getP2() {
        return P2;
    }

    public void setP2(String p2) {
        P2 = p2;
    }

    public String getI3() {
        return I3;
    }

    public void setI3(String i3) {
        I3 = i3;
    }

    public String getV3() {
        return V3;
    }

    public void setV3(String v3) {
        V3 = v3;
    }

    public String getP3() {
        return P3;
    }

    public void setP3(String p3) {
        P3 = p3;
    }

    public String getI4() {
        return I4;
    }

    public void setI4(String i4) {
        I4 = i4;
    }

    public String getV4() {
        return V4;
    }

    public void setV4(String v4) {
        V4 = v4;
    }

    public String getP4() {
        return P4;
    }

    public void setP4(String p4) {
        P4 = p4;
    }

    public String getVbat() {
        return Vbat;
    }

    public void setVbat(String vbat) {
        Vbat = vbat;
    }

    public String getTemp() {
        return Temp;
    }

    public void setTemp(String temp) {
        Temp = temp;
    }

    public String getHumid() {
        return Humid;
    }

    public void setHumid(String humid) {
        Humid = humid;
    }

    public String getUV() {
        return UV;
    }

    public void setUV(String UV) {
        this.UV = UV;
    }

    public String getLW() {
        return LW;
    }

    public void setLW(String LW) {
        this.LW = LW;
    }

    public String getIR() {
        return IR;
    }

    public void setIR(String IR) {
        this.IR = IR;
    }

    public ESPData(String sequence, String dd, String hh, String mm, String ss, String i1, String v1, String p1, String i2, String v2, String p2, String i3, String v3, String p3, String i4, String v4, String p4, String vbat, String temp, String humid, String UV, String LW, String IR) {
        this.sequence = sequence;
        this.dd = dd;
        this.hh = hh;
        this.mm = mm;
        this.ss = ss;
        I1 = i1;
        V1 = v1;
        P1 = p1;
        I2 = i2;
        V2 = v2;
        P2 = p2;
        I3 = i3;
        V3 = v3;
        P3 = p3;
        I4 = i4;
        V4 = v4;
        P4 = p4;
        Vbat = vbat;
        Temp = temp;
        Humid = humid;
        this.UV = UV;
        this.LW = LW;
        this.IR = IR;
    }

    String V1;
    String P1;
    String I2;
    String V2;
    String P2;
    String I3;
    String V3;
    String P3;
    String I4;
    String V4;
    String P4;
    String Vbat;
    String Temp;
    String Humid;
    String UV;
    String LW;
    String IR;

    public ESPData() {
    }

    public static ESPData buildESPData(String line) {
        String[] split = line.split(",");
        ESPData espData = new ESPData();
        int i = 0;
        espData.setSequence(split[i++]);
//        espData.setDd(split[i++]);
//        espData.setHh(split[i++]);
//        espData.setMm(split[i++]);
//        espData.setSs(split[i++]);
//        espData.setI1(split[i++]);
//        espData.setV1(split[i++]);
//        espData.setP1(split[i++]);
//        espData.setI2(split[i++]);
//        espData.setV2(split[i++]);
//        espData.setP2(split[i++]);
//        espData.setI3(split[i++]);
//        espData.setV3(split[i++]);
//        espData.setP3(split[i++]);
//        espData.setI4(split[i++]);
//        espData.setV4(split[i++]);
//        espData.setP4(split[i++]);
//        espData.setVbat(split[i++]);
        espData.setTemp(split[i++]);
//        espData.setHumid(split[i++]);
//        espData.setUV(split[i++]);
//        espData.setLW(split[i++]);
//        espData.setIR(split[i++]);
        return espData;
    }
}
