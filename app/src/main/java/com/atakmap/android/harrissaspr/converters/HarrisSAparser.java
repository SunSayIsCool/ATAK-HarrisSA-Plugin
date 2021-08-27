package com.atakmap.android.harrissaspr.converters;

import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class HarrisSAparser {

    private String RMCstring = null;
    private String SAheader = null;
    private String SAend = null;

    public void init_sa(String input_buffer) {
        String[] arrSplit = input_buffer.split(",");
        SAheader = input_buffer.split("\\$|\\*")[0];
        RMCstring = input_buffer.split("\\$|\\*")[1];
        SAend = input_buffer.split("\\$|\\*")[2];
    }

    public String SA_header() {
        return SAheader;
    }

    public String RMC_string() {
        return RMCstring;
    }

    public String SA_end() {
        return SAend;
    }

    public String CALC_chksum() {
        int checksum = 0;
        for (int i = 0; i < RMCstring.length(); i++) {
            checksum = checksum ^ RMCstring.charAt(i);
        }
        return Integer.toHexString(checksum).toUpperCase();
    }

    public String RMC_chksum() {
        return SAend.substring(0, 2);
    }

    public Boolean CHKSUMM_pass() {
        if (CALC_chksum().equals(RMC_chksum())) return true;
        else return false;
    }

    public boolean isValid() {
        String[] arrSplit = RMCstring.split(",");
        return (arrSplit[2].charAt(0) == 'A');
    }

    public String Callsign() {
        if (SAend.substring(2).equals(SAheader.substring(1, SAheader.length() - 1))) {
            return SAend.substring(2);
        } else return null;
    }

    public Double Latitude() {
        String[] arrSplit = RMCstring.split(",");
        double dd = Double.parseDouble(arrSplit[3].substring(0, 2));
        double mm = Double.parseDouble(arrSplit[3].substring(2, 4));
        double ss = Double.parseDouble("0" + arrSplit[3].substring(4));
        double dddd = dd + mm / 60 + ss * 60 / 3600;
        switch (arrSplit[4].charAt(0)) {
            case 'N':
                dddd = dddd;
                break;
            case 'S':
                dddd = 0 - dddd;
                break;
        }
        return dddd;
    }

    public Double Longtitude() {
        String[] arrSplit = RMCstring.split(",");
        double dd = Double.parseDouble(arrSplit[5].substring(0, 3));
        double mm = Double.parseDouble(arrSplit[5].substring(3, 5));
        double ss = Double.parseDouble("0" + arrSplit[5].substring(5));
        double dddd = dd + mm / 60 + ss * 60 / 3600;
        switch (arrSplit[6].charAt(0)) {
            case 'E':
                dddd = dddd;
                break;
            case 'W':
                dddd = 0 - dddd;
                break;
        }
        return dddd;
    }

    public String Speed() {
        String[] arrSplit = RMCstring.split(",");
        float ms = Float.parseFloat(arrSplit[7]);
        ms = ms * 0.514444F;
        return Float.toString(ms);
    }

    public String Course() {
        String[] arrSplit = RMCstring.split(",");
        return arrSplit[8];
    }

    public String CotDateTime() {
        String[] arrSplit = RMCstring.split(",");
        String DateTime = arrSplit[9] + arrSplit[1];
        try {
            Date date = new SimpleDateFormat("ddMMyyHHmmss").parse(DateTime);
            DateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateTime;
    }

    public Boolean GeneralPass() {
        if ((CHKSUMM_pass()) && (isValid()) && (Callsign() != null)) return true;
        else return false;
    }
}
