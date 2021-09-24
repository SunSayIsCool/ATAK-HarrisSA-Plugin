package com.atakmap.android.harrissaspr.converters;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class SprSAparser {

    private String Echelon = null;
    private String RoleID = null;
    private String RMCstring = null;

    // Method for reversing significant values of input buffer array
    private static byte[] reverse (byte[] input, int start, int end) {
        byte chkArr[] = new byte[2];
        chkArr[0] = input[end];
        chkArr[1] = input[start];
        return chkArr;
    }

    //Method for checking is received SA is valid for RMC string extraction
    public Boolean saPass (byte[] inputBuffer){
        BigInteger one;
        // SApacket value (must be 49)
        one = new BigInteger(reverse(inputBuffer, 0,1));
        int SApacket = one.intValue();

        // SApayload must be equal to Total payload length
        one = new BigInteger(reverse(inputBuffer, 2,3));
        int SAPayload = one.intValue();

        // Destination = Host SA/PC application (must be 512)
        one = new BigInteger(reverse(inputBuffer, 4,5));
        int Destination = one.intValue();

        // Source = SA processor (must be 14)
        one = new BigInteger(reverse(inputBuffer, 6,7));
        int Source = one.intValue();

        // Total Length must be equal to SApayload
        int TotalLength = inputBuffer[8];

        // Echelon header must be 11
        int EchelonHeader = inputBuffer[9];

        // Echelon length for shifting to RoleID Field
        int EchelonLength = inputBuffer[10];

        // RoleID position
        int RoleidPosition = 9 + EchelonLength;

        // RoleID header must be 0
        int RoleidHeader = inputBuffer[RoleidPosition];

        // RoleID length for shifting to Datum Field
        int RoleidLength = inputBuffer[RoleidPosition+1];

        // Datum position
        int DatumPosition = RoleidPosition + RoleidLength;

        // Datum header must be 1
        int DatumHeader = inputBuffer[DatumPosition];

        // Datum length for shifting to RMC Field
        int DatumLength = inputBuffer[DatumPosition+1];

        // Datum = 1 means WGS84
        int DatumValue = inputBuffer[DatumPosition+2];

        // RMC position
        int RmcPosition = DatumPosition + DatumLength;

        // RMC header must be 3
        int RmcHeader = inputBuffer[RmcPosition];

        // RMC Length for shifting to Alert Field
        int RmcLength = inputBuffer[RmcPosition+1];

        // Checking condition for passing
        if ((SApacket == 49) && (SAPayload == TotalLength) && (Destination == 512) &&
                (Source == 14) && (EchelonHeader == 11) && (RoleidHeader == 0) &&
                (DatumHeader == 1) && (DatumValue == 1) && (RmcHeader == 3) ) {
            Echelon = new String(Arrays.copyOfRange(inputBuffer, 11, EchelonLength+9));
            RoleID = new String(Arrays.copyOfRange(inputBuffer, RoleidPosition+2, RoleidPosition+RoleidLength));
            RMCstring = new String(Arrays.copyOfRange(inputBuffer, RmcPosition+2, RmcPosition+RmcLength));
            return true;
        } else return false;
    }

    // Alert Flag searching
    public Boolean Alert (byte[] inputBuffer){

        byte[] alert = new byte[] {7,4,1};

        String input_str = new String(inputBuffer, StandardCharsets.UTF_8);
        String alert_str = new String(alert, StandardCharsets.UTF_8);

        int AlertPosition = input_str.indexOf(alert_str);
        // Checking condition for passing
        if (AlertPosition != -1 ) {
            return true;
        } else return false;
    }

    public boolean isValid() {
        String[] arrSplit = RMCstring.split(",");
        if (arrSplit[2].charAt(0) == 'A') {
            return true;
        } else return false;
    }

    public String CotDateTime() {
        String[] arrSplit = RMCstring.split(",");
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

    public Double Latitude() {
        String[] arrSplit = RMCstring.split(",");
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

    public Double Longtitude() {
        String[] arrSplit = RMCstring.split(",");
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

    public String RoleId () {
        return RoleID;
    }

    public String RMCstring() {
        return RMCstring;
    }

    // TODO add accuracy flag 0d = 13

}