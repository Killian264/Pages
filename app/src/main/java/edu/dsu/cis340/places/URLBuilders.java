package edu.dsu.cis340.places;

public class URLBuilders {
    // This is unsafe
    final static String APIKEY = "API_KEY";

    // A config file for some of this information would be better

    // Create URL's for API calls
    public static String LocationURLBuilder(String location){
        String baseURL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/";
        String returnType = "json";
        String inputType = "textquery";
        String fields = "name,photos,place_id";

        return baseURL + returnType + "?" + "key=" + APIKEY + "&input=" + location + "&inputtype=" + inputType + "&fields=" + fields;
    }
    public static String photoURLBuilder(String photoReference, int maxHeightPhoto, int maxWidthPhoto, int maxHeightScreen, int maxWidthScreen){
        String baseURL = "https://maps.googleapis.com/maps/api/place/photo?";
        int maxHeight = maxHeightPhoto > maxHeightScreen ? maxHeightScreen : maxHeightPhoto;
        int maxWidth = maxWidthPhoto > maxWidthScreen ? maxWidthScreen : maxWidthPhoto;

        return baseURL + "maxwidth=" + maxWidth + "&maxheight=" + maxHeight + "&photoreference=" + photoReference + "&key=" + APIKEY;
    }
}

