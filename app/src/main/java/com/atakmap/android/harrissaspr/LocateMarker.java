package com.atakmap.android.harrissaspr;

import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.atakmap.android.cot.CotMapComponent;
import com.atakmap.android.harrissaspr.converters.HarrisSAparser;
import com.atakmap.android.harrissaspr.converters.SprSAparser;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapData;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.maps.Marker;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.cot.event.CotPoint;
import com.atakmap.coremap.maps.coords.GeoPoint;
import com.atakmap.coremap.maps.time.CoordinatedTime;

import java.text.ParseException;

public class LocateMarker {
    public static void placeHhmpMarker(Boolean share, String CombatID, String Alias, String sa_str, String type) {
        double lat;
        double lon;

        CotMapComponent cotMapComponent = new CotMapComponent();
        CoordinatedTime time = new CoordinatedTime();
        CotEvent generatedCOT = new CotEvent();
        CotDetail cotDetail = new CotDetail();
        CotDetail contact = new CotDetail();
        CotDetail track = new CotDetail();
        HarrisSAparser saparser = new HarrisSAparser();
        saparser.init_sa(sa_str);
        if ((saparser.GeneralPass()) && (CombatID.equals(saparser.Callsign()))) {
            // adding point parameters
            lat = saparser.Latitude();
            lon = saparser.Longtitude();
            generatedCOT.setUID("harristool-" + CombatID.toLowerCase());
            generatedCOT.setType(type);

            generatedCOT.setTime(time);
            generatedCOT.setStart(time);
            generatedCOT.setStale(time.addMinutes(10));
            generatedCOT.setHow("m-g");
            generatedCOT.setPoint(new CotPoint(lat, lon, 0, 2, 2));

            // adding callsign detail
            contact.setElementName("contact");
            if (!Alias.equals("")) {
                contact.setAttribute("callsign", Alias);
            } else {
                contact.setAttribute("callsign", CombatID);
            }
            cotDetail.addChild(contact);

            // adding speed and course
            track.setElementName("track");
            track.setAttribute("speed", saparser.Speed());
            track.setAttribute("course", saparser.Course());
            cotDetail.addChild(track);

            generatedCOT.setDetail(cotDetail);

            cotMapComponent.getInternalDispatcher().dispatch(generatedCOT);
            if (share) {
                cotMapComponent.getExternalDispatcher().dispatchToBroadcast(generatedCOT);
            }
        } else {
            Log.v(HarrisSaSprDropDownReceiver.class.getSimpleName(), "UDP Thread: SA data time too different");
        }
    }

    public static void placeSprMarker(Boolean share, String CombatID, String Alias, byte[] sa_array, String type) {
        double lat;
        double lon;

        CotMapComponent cotMapComponent = new CotMapComponent();
        SprSAparser saparser = new SprSAparser();
        CoordinatedTime time = new CoordinatedTime();
        CotEvent generatedCOT = new CotEvent();
        CotDetail cotDetail = new CotDetail();
        CotDetail contact = new CotDetail();
        CotDetail track = new CotDetail();

        saparser.saPass(sa_array);
        if (CombatID.equals(saparser.RoleId())) {
            // adding point parameters
            lat = saparser.Latitude();
            lon = saparser.Longtitude();
            generatedCOT.setUID("harristool-" + CombatID.toLowerCase());
            generatedCOT.setType(type);
            generatedCOT.setTime(time);
            generatedCOT.setStart(time);
            generatedCOT.setStale(time.addMinutes(10));
            generatedCOT.setHow("m-g");
            generatedCOT.setPoint(new CotPoint(lat, lon, 0, 2, 2));

            // adding callsign detail
            contact.setElementName("contact");
            if (!Alias.equals("")) {
                contact.setAttribute("callsign", Alias);
            } else {
                contact.setAttribute("callsign", CombatID);
            }
            cotDetail.addChild(contact);

            // adding speed and course
            track.setElementName("track");
            track.setAttribute("speed", saparser.Speed());
            track.setAttribute("course", saparser.Course());
            cotDetail.addChild(track);

            generatedCOT.setDetail(cotDetail);

            cotMapComponent.getInternalDispatcher().dispatch(generatedCOT);
            if (share) {
                cotMapComponent.getExternalDispatcher().dispatchToBroadcast(generatedCOT);
            }
        }
    }

    public static void placeSprAlert(String CombatID, String Alias, byte[] sa_array) {

        double lat;
        double lon;

        CotMapComponent cotMapComponent = new CotMapComponent();
        SprSAparser saparser = new SprSAparser();
        CoordinatedTime time = new CoordinatedTime();
        CotEvent generatedCOT = new CotEvent();
        CotDetail cotDetail = new CotDetail();
        CotDetail contact = new CotDetail();
        CotDetail track = new CotDetail();

        saparser.saPass(sa_array);
        if (CombatID.equals(saparser.RoleId())) {
            // adding point parameters
            lat = saparser.Latitude();
            lon = saparser.Longtitude();
            generatedCOT.setUID("harristool-" + CombatID.toLowerCase() + "-alert-9-1-1");
            generatedCOT.setType("b-a-o-tbl");
            generatedCOT.setTime(time);
            generatedCOT.setStart(time);
            generatedCOT.setStale(time.addMinutes(10));
            generatedCOT.setHow("m-g");
            generatedCOT.setPoint(new CotPoint(lat, lon, 0, 2, 2));

            // adding callsign detail
            contact.setElementName("contact");
            if (!Alias.equals("")) {
                contact.setAttribute("callsign", "ALERT by " + Alias);
            } else {
                contact.setAttribute("callsign", "ALERT by " + CombatID);
            }
            cotDetail.addChild(contact);

            // adding speed and course
            track.setElementName("track");
            track.setAttribute("speed", saparser.Speed());
            track.setAttribute("course", saparser.Course());
            cotDetail.addChild(track);

            generatedCOT.setDetail(cotDetail);

            cotMapComponent.getInternalDispatcher().dispatch(generatedCOT);
            cotMapComponent.getExternalDispatcher().dispatchToBroadcast(generatedCOT);
        }
    }

    public static void placeHhmpSelf(MapView mapView, String sa_str) {
        Marker item = mapView.getMapView().getSelfMarker();
        HarrisSAparser saparser = new HarrisSAparser();
        if (item != null) {

            final MapData data = mapView.getMapView().getMapData();
            GeoPoint gp = new GeoPoint(saparser.Latitude(), saparser.Longtitude()); // decimal degrees
            data.putDouble("mockLocationSpeed", Double.parseDouble(saparser.Speed())); // speed in meters per second
            data.putDouble("mockLocationBearing", Double.parseDouble(saparser.Course()));
            data.putFloat("mockLocationAccuracy", 3f); // accuracy in meters
            data.putString("locationSourcePrefix", "mock");
            data.putBoolean("mockLocationAvailable", true);
            data.putString("mockLocationSource", "78xx HH/MP Radio");
            data.putBoolean("mockLocationCallsignValid", true);
            data.putParcelable("mockLocation", gp);
            data.putLong("mockLocationTime", SystemClock.elapsedRealtime());
            data.putLong("mockGPSTime", new CoordinatedTime().getMilliseconds()); // time as reported by the gps device
            data.putInt("mockFixQuality", 2);

            Intent gpsReceived = new Intent();

            gpsReceived.setAction("com.atakmap.android.map.WR_GPS_RECEIVED");
            AtakBroadcast.getInstance().sendBroadcast(gpsReceived);
        }
    }

    public static void placeSprSelf(MapView mapView, byte[] byteArray) {
        Marker item = mapView.getMapView().getSelfMarker();
        SprSAparser saparser = new SprSAparser();
        if ((item != null) && (byteArray != null)) {
            saparser.saPass(byteArray);

            final MapData data = mapView.getMapView().getMapData();
            GeoPoint gp = new GeoPoint(saparser.Latitude(), saparser.Longtitude()); // decimal degrees
            data.putDouble("mockLocationSpeed", Double.parseDouble(saparser.Speed())); // speed in meters per second
            data.putDouble("mockLocationBearing", Double.parseDouble(saparser.Course()));
            data.putFloat("mockLocationAccuracy", 3f); // accuracy in meters
            data.putString("locationSourcePrefix", "mock");
            data.putBoolean("mockLocationAvailable", true);
            data.putString("mockLocationSource", "7800S Radio");
            data.putBoolean("mockLocationCallsignValid", true);
            data.putParcelable("mockLocation", gp);
            data.putLong("mockLocationTime", SystemClock.elapsedRealtime());
            data.putLong("mockGPSTime", new CoordinatedTime().getMilliseconds()); // time as reported by the gps device
            data.putInt("mockFixQuality", 2);

            Intent gpsReceived = new Intent();

            gpsReceived.setAction("com.atakmap.android.map.WR_GPS_RECEIVED");
            AtakBroadcast.getInstance().sendBroadcast(gpsReceived);
        }

    }


}
