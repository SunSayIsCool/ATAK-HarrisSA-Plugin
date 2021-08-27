package com.atakmap.android.harrissaspr.converters;

import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class HarrisSAparser {
    public Boolean ChkPass(String input_str){
        return (CHKsumm(RMCstr(input_str)).equals(RMC_chksum(input_str)));
    }

    public String RMCstr(String strMain) {
        String[] arrSplit = strMain.split(",");
        return strMain.split("\\$|\\*")[1];
    }

    public String CHKsumm(String input_str) {
        int checksum = 0;
        for (int i = 0; i < input_str.length(); i++) {
            checksum = checksum ^ input_str.charAt(i);
        }
        return Integer.toHexString(checksum).toUpperCase();
    }

    public char Valid(String strMain) {
        String[] arrSplit = strMain.split(",");
        return arrSplit[2].charAt(0);
    }

    public String CotDateTime(String strMain) throws IOException {
        String[] arrSplit = strMain.split(",");
        String DateTime = arrSplit[9] + arrSplit[1];
        Calendar c = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat("ddMMyyHHmmss").parse(DateTime);
            DateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateTime;
    }

    public Boolean CompareIsActualTime(long coordTime, String saTime){
        String[] arrSplit = saTime.split(",");
        String DateTime = arrSplit[9] + arrSplit[1];
        Date date = new Date();
        long lngdate = 0;
        Calendar c = Calendar.getInstance();
        try {
            lngdate = new SimpleDateFormat("ddMMyyHHmmss").parse(DateTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int offsetInMilliseconds = TimeZone.getDefault().getOffset(date.getTime());
        Log.v("HarrisSaSprDropDown", "UDP Thread: " + String.valueOf(coordTime - lngdate - offsetInMilliseconds));
        if (Math.abs(coordTime - lngdate - offsetInMilliseconds) < 300000) { return true; }
        else { return false; }
    }

    public Double Latitude(String strMain) {
        String[] arrSplit = strMain.split(",");
        double dd = Double.parseDouble(arrSplit[3].substring(0, 2));
        double mm = Double.parseDouble(arrSplit[3].substring(2, 4));
        double ss = Double.parseDouble("0" + arrSplit[3].substring(4, arrSplit[3].length()));
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

    public Double Longtitude(String strMain) {
        String[] arrSplit = strMain.split(",");
        double dd = Double.parseDouble(arrSplit[5].substring(0, 3));
        double mm = Double.parseDouble(arrSplit[5].substring(3, 5));
        double ss = Double.parseDouble("0" + arrSplit[5].substring(5, arrSplit[5].length()));
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

    public String Speed(String strMain) {
        String[] arrSplit = strMain.split(",");
        float ms = Float.parseFloat(arrSplit[7]);
        ms = ms * 0.514444F;
        return Float.toString(ms);
    }

    public String Course(String strMain) {
        String[] arrSplit = strMain.split(",");
        return arrSplit[8];
    }

    public String Callsign(String strMain) {
        String[] arrSplit = strMain.split(",");
        String callsign_chksum = arrSplit[11].split("\\*")[1];
        return callsign_chksum.substring(2, callsign_chksum.length());
    }

    public String RMC_chksum(String strMain) {
        String[] arrSplit = strMain.split(",");
        String callsign_chksum = arrSplit[11].split("\\*")[1];
        String chksum = callsign_chksum.substring(0, 2);
        return callsign_chksum.substring(0, 2);
    }
}
