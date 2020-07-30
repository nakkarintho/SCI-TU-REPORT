package com.coldzify.finalproject;

import android.location.Location;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.maps.android.PolyUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LocationHandle {
    public static final short UNKNOWN_LOCATION = -1;
    public static final short LC2_PLACE = 0;
    public static final short LC3_PLACE = 1;
    public static final short LC4_PLACE = 2;
    public static final short LC5_PLACE = 3;
    public static final short PARKING_LC2_PLACE = 4;
    public static final short PARKING_LC5_PLACE = 5;
    public static final short FOOD_CENTER_PLACE = 6;
    public static final short COVERWAY_A_PLACE = 7;
    public static final short COVERWAY_B_PLACE= 8;
    public static final short COVERWAY_C_PLACE = 9;
    public static final short COURTYARD_COMSCI_PLACE = 10;
    public static final short COURTYARD_STAT_PLACE = 11;
    public static final short CIRCLE_LC2_PLACE = 14;
    public  static final LatLng CIRCLE_LC2 = new LatLng(14.073974, 100.606280);
    public static List<LatLng> SCI_DEPARTMENT =new ArrayList<>(Arrays.asList(
            new LatLng(14.074690, 100.605381),
            new LatLng(14.074690, 100.608700),
            new LatLng(14.072186, 100.608700),
            new LatLng(14.072182, 100.605474)
    ));
    public  static List<LatLng> LC5 =new ArrayList<>(Arrays.asList(
            new LatLng(14.074272, 100.607202),
            new LatLng(14.074275, 100.607343),
            new LatLng(14.074464, 100.607349),
            new LatLng(14.074473, 100.608408),
            new LatLng(14.074312, 100.608424),
            new LatLng(14.074309, 100.608552),
            new LatLng(14.073313, 100.608558),
            new LatLng(14.073300, 100.608415),
            new LatLng(14.073127, 100.608421),
            new LatLng(14.073108, 100.607362),
            new LatLng(14.073294, 100.607346),
            new LatLng(14.073291, 100.607193)
    ));
    public  static List<LatLng> LC4 =new ArrayList<>(Arrays.asList(
            new LatLng(14.072940, 100.607206),
            new LatLng(14.072940, 100.608306),
            new LatLng(14.072170, 100.608281),
            new LatLng(14.072170, 100.607209)
    ));
    public  static List<LatLng> LC3 =new ArrayList<>(Arrays.asList(
            new LatLng(14.072910,100.605645),
            new LatLng(14.072915,100.606938),
            new LatLng(14.072170,100.606941),
            new LatLng(14.072170, 100.605646)
    ));
    public  static List<LatLng> LC2 =new ArrayList<>(Arrays.asList(
            new LatLng(14.073723, 100.605496),
            new LatLng(14.073717, 100.607055),
            new LatLng(14.073485, 100.607044),
            new LatLng(14.073485, 100.606340),
            new LatLng(14.073320, 100.606340),
            new LatLng(14.073320, 100.606251),
            new LatLng(14.073485, 100.606251),
            new LatLng(14.073485, 100.605712),
            new LatLng(14.073191, 100.605710),
            new LatLng(14.073187, 100.605483)
    ));
    public  static List<LatLng> PARKING_LC2 =new ArrayList<>(Arrays.asList(
            new LatLng(14.074538, 100.605471),
            new LatLng(14.074556, 100.606040),
            new LatLng(14.073888, 100.606040),
            new LatLng(14.073869, 100.605507)
    ));
    public  static List<LatLng> PARKING_LC5 =new ArrayList<>(Arrays.asList(
            new LatLng(14.074548, 100.606779),
            new LatLng(14.074536, 100.607133),
            new LatLng(14.073720, 100.607152),
            new LatLng(14.073720, 100.606786)
    ));
    public  static List<LatLng> FOOD_CENTER_LC4 =new ArrayList<>(Arrays.asList(
            new LatLng(14.072804, 100.608361),
            new LatLng(14.072797, 100.608606),
            new LatLng(14.072373, 100.608599),
            new LatLng(14.072371, 100.608349)
    ));
    public static List<LatLng> COVERWAY_A =new ArrayList<>(Arrays.asList(
            new LatLng(14.073015, 100.605423),
            new LatLng(14.073015, 100.605712),
            new LatLng(14.073120, 100.605820),
            new LatLng(14.073120, 100.606245),

            new LatLng(14.073038, 100.606245),
            new LatLng(14.073038, 100.605851),
            new LatLng(14.072935, 100.605731),
            new LatLng(14.072935, 100.605423)
    ));
    public  static List<LatLng> COVERWAY_B =new ArrayList<>(Arrays.asList(
            new LatLng(14.072910, 100.606246),
            new LatLng(14.072910, 100.606340),
            new LatLng(14.073278, 100.606340),
            new LatLng(14.073278, 100.606252)
    ));

    public static List<LatLng> COVERWAY_C =new ArrayList<>(Arrays.asList(
            new LatLng(14.073120, 100.606342),
            new LatLng(14.073120, 100.606744),
            new LatLng(14.073027, 100.606875),
            new LatLng(14.073027, 100.608569),

            new LatLng(14.072946, 100.608569),
            new LatLng(14.072946, 100.606850),
            new LatLng(14.073040, 100.606709),
            new LatLng(14.073038, 100.606342)
    ));
    public static List<LatLng> COURTYARD_COMSCI =new ArrayList<>(Arrays.asList(
            new LatLng(14.073480, 100.605714),
            new LatLng(14.073480, 100.606246),
            new LatLng(14.073124, 100.606246),
            new LatLng(14.073124, 100.605813),

            new LatLng(14.073040, 100.605714)

    ));
    public static List<LatLng> COURTYARD_STAT =new ArrayList<>(Arrays.asList(
            new LatLng(14.073483, 100.606344),
            new LatLng(14.073483, 100.607111),
            new LatLng(14.073030, 100.607111),
            new LatLng(14.073030, 100.606882),
            new LatLng(14.073124, 100.606744),
            new LatLng(14.073124, 100.606344)

    ));

    public  static List<List<LatLng>> placeArray = Arrays.asList(LC2,LC3,LC4,LC5,PARKING_LC2
            ,PARKING_LC5,
            FOOD_CENTER_LC4,
            COVERWAY_A,
            COVERWAY_B,
            COVERWAY_C,
            COURTYARD_COMSCI,
            COURTYARD_STAT
    );


    static int findPlace(final LatLng latLng){
        //If in known place
        int i = 0 ,ans = UNKNOWN_LOCATION;
        for(List<LatLng> place : placeArray){
            if(PolyUtil.containsLocation(latLng,place,true)){
                ans = i ;
                break;
            }
            i++;
        }
        //If in circle LC.2
        if(distance(CIRCLE_LC2,latLng) <= 26){
            ans = CIRCLE_LC2_PLACE;
        }

        return ans;
    }
    public static float distance(final LatLng v1, final LatLng v2){
        float dis[] = new float[1];
        Location.distanceBetween(v1.latitude,v1.longitude,v2.latitude,v2.longitude,dis);
        return dis[0];
    }
    static boolean isInSciDepartment(final LatLng latLng){
        return PolyUtil.containsLocation(latLng,SCI_DEPARTMENT,true);
    }
    public static void main(String[] args) {
        //System.out.println("distance  ? : "+LocationHandle.distance()));

    }
    public static boolean isInBR(int code){
        return code == LC2_PLACE || code == LC3_PLACE || code == LC4_PLACE || code == LC5_PLACE;
    }
    public static String locationCodeToString(int code){
        String place = "";
        switch(code) {
            case UNKNOWN_LOCATION:
                place = "ไม่สามารถระบุได้";
                break;
            case LC2_PLACE:
                place = "บร.2";
                break;
            case LC3_PLACE:
                place = "บร.3";
                break;
            case LC4_PLACE:
                place = "บร.4";
                break;
            case LC5_PLACE:
                place = "บร.5";
                break;
            case PARKING_LC2_PLACE:
                place = "ที่จอดรถบร.2";
                break;
            case PARKING_LC5_PLACE:
                place = "ที่จอดรถบร.5";
                break;
            case FOOD_CENTER_PLACE:
                place = "โรงอาหาร";
                break;
            case COVERWAY_A_PLACE:
                place = "โคฟเวอร์เวย์ 1";
                break;
            case COVERWAY_B_PLACE:
                place = "โคฟเวอร์เวย์ 2";
                break;
            case COVERWAY_C_PLACE:
                place = "โคฟเวอร์เวย์ 3";
                break;
            case COURTYARD_COMSCI_PLACE:
                place = "ลานกว้างภาคคอม";
                break;
            case COURTYARD_STAT_PLACE:
                place = "ลานกว้างภาคสถิติ";
                break;
            case CIRCLE_LC2_PLACE:
                place = "วงเวียนบร.2";
                break;
            default:
                place = "ไม่สามารถระบุได้";
        }

        return place;
    }
}
