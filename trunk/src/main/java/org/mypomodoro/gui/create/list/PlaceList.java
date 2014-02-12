package org.mypomodoro.gui.create.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mypomodoro.db.ActivitiesDAO;

/**
 * List of places of activities and reports
 *
 */
public class PlaceList {

    private static List<String> places = new ArrayList<String>();

    public static void initPlaces() {
        places = ActivitiesDAO.getInstance().getPlaces();
    }

    public static List<String> getPlaces() {
        if (places.size() > 1) {
            Collections.sort(places);
        }
        return places;
    }

    public static void addPlace(String place) {
        if (place.trim().length() > 0 && !places.contains(place)) {
            places.add(place);
        }
    }
}
