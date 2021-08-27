package com.atakmap.android.harrissaspr;

import android.app.Application;
import android.util.Log;

import com.atakmap.android.cot.CotMapComponent;
import com.atakmap.android.harrissaspr.converters.HarrisSAparser;
import com.atakmap.android.harrissaspr.converters.SprSAparser;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.cot.event.CotPoint;
import com.atakmap.coremap.maps.coords.GeoPoint;
import com.atakmap.coremap.maps.time.CoordinatedTime;

public class LocateMarker {
    public void placeMarker(Boolean share, String CombatID, String Alias, String sa_str, String type) {
        double lat;
        double lon;

        CotMapComponent cotMapComponent = new CotMapComponent();
        HarrisSAparser saparser = new HarrisSAparser();
        CoordinatedTime time = new CoordinatedTime();
        CotEvent generatedCOT = new CotEvent();
        CotDetail cotDetail = new CotDetail();
        CotDetail contact = new CotDetail();
        CotDetail track = new CotDetail();


        if ((saparser.CompareIsActualTime(time.getMilliseconds(), sa_str)) && (CombatID.equals(saparser.Callsign(sa_str)))) {
            // adding point parameters
            lat = saparser.Latitude(sa_str);
            lon = saparser.Longtitude(sa_str);
            generatedCOT.setUID("harristool-"+CombatID.toLowerCase());
            generatedCOT.setType(type);
            generatedCOT.setTime(time);
            generatedCOT.setStart(time);
            generatedCOT.setStale(time.addMinutes(10));
            generatedCOT.setHow("m-g");
            generatedCOT.setPoint(new CotPoint(lat, lon, 0 , 2,2));

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
            track.setAttribute("speed", saparser.Speed(sa_str));
            track.setAttribute("course",saparser.Course(sa_str));
            cotDetail.addChild(track);

            generatedCOT.setDetail(cotDetail);

            cotMapComponent.getInternalDispatcher().dispatch(generatedCOT);
            if (share) {
                cotMapComponent.getExternalDispatcher().dispatchToBroadcast(generatedCOT);
            }
        } else { Log.v(HarrisSaSprDropDownReceiver.class.getSimpleName(), "UDP Thread: SA data time too different");}
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
}
