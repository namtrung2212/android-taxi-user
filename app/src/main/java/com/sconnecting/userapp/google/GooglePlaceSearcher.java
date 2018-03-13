package com.sconnecting.userapp.google;

import android.annotation.TargetApi;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.DownloadHelper;
import com.sconnecting.userapp.location.LocationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;


public class GooglePlaceSearcher {

    public static String placeKey = "AIzaSyBArZgCF0ZcAyHsIqVXbnVg-LbT-ySi6L0";

    public Integer distance = 5000;

    public GooglePlaceSearcher(){

    }

    public interface SearchPlaceListener {

        public void onCompleted(List<GooglePlaceSuggestion> newSuggestions);
    }
    public void search(String strQuery,SearchPlaceListener listener){

        SearchPlacesTask searchPlacesTask = new SearchPlacesTask(listener);
        searchPlacesTask.execute(strQuery);


    }
    private class SearchPlacesTask extends AsyncTask<String, Void, String>{

        private SearchPlaceListener mSearchPlaceListener;

        public SearchPlacesTask(SearchPlaceListener listener){
            mSearchPlaceListener = listener;
        }
        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=" + placeKey;

            String input="";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=address";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input+"&"+types+"&"+sensor+"&"+key+"&language=vi&location="
                    + Double.toString(SCONNECTING.locationHelper.location.getLatitude())+ ","
                    + Double.toString(SCONNECTING.locationHelper.location.getLongitude()) + "" +
                    "&radius=" + distance;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

            try{
                // Fetching the data from we service
                data = DownloadHelper.downloadUrl(url);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            SearchPlacesParserTask parserTask = new SearchPlacesParserTask(mSearchPlaceListener);

            parserTask.execute(result);
        }
    }
    private class SearchPlacesParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        private SearchPlaceListener mSearchPlaceListener;
        JSONObject jObject;

        public SearchPlacesParserTask(SearchPlaceListener listener){
            mSearchPlaceListener = listener;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            GooglePlaceJSONParser placeJsonParser = new GooglePlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            List<GooglePlaceSuggestion> newSuggestions = new ArrayList<GooglePlaceSuggestion>();

            for (HashMap<String, String> map : result) {

                GooglePlaceSuggestion suggestion = new GooglePlaceSuggestion(map.get("description"), map.get("place_id"));
                newSuggestions.add(suggestion);

            }


            if(mSearchPlaceListener != null )
                mSearchPlaceListener.onCompleted(newSuggestions);

        }
    }

    public interface GetPlaceListener {

        public void onCompleted(LocationInfo locAddress);
    }
    public void getAddress(LatLng location, GetPlaceListener listener){

        GetPlaceTask getPlaceTask = new GetPlaceTask(listener);
        getPlaceTask.execute(location);

    }
    private class GetPlaceTask extends AsyncTask<LatLng, Void, LocationInfo> {

        GetPlaceListener mGetPlaceListener;

        public  GetPlaceTask(GetPlaceListener listener){
            mGetPlaceListener = listener;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected LocationInfo doInBackground(LatLng... jsonData) {

            LocationInfo result = new LocationInfo();
           // Geocoder geocoder = new Geocoder(AppDelegate.getApplication().getApplicationContext(), Locale.forLanguageTag("vi-VN"));

            List<Address> addresses = null;

            try {
             //   addresses = geocoder.getFromLocation(jsonData[0].latitude,jsonData[0].longitude,1);
                addresses = getStringFromLocation(jsonData[0].latitude,jsonData[0].longitude,Locale.forLanguageTag("vi-VN"));

            } catch (JSONException e) {

                e.printStackTrace();
                Log.e("getFromLocation", "ioException: " + e.getMessage(), null);

            } catch (ClientProtocolException e) {

                e.printStackTrace();
                Log.e("getFromLocation", "ioException: " + e.getMessage(), null);

            } catch (IOException e) {

                e.printStackTrace();
                Log.e("getFromLocation", "ioException: " + e.getMessage(), null);

            }


            if (addresses != null && addresses.size()  != 0) {

                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }

                String strAddress = TextUtils.join(System.getProperty("line.separator"),addressFragments);
                strAddress = strAddress.replace(", " + address.getCountryName(),"");
                strAddress = strAddress.replace("Unnamed Road, ","");
                result.Address = strAddress;
                result.CountryCode = address.getCountryCode();
                result.CountryName = address.getCountryName();
                result.Locality = address.getLocality();
                result.PostalCode = address.getPostalCode();
                result.Province = address.getAdminArea();
                return result;
            }

            return null;
        }

        @Override
        protected void onPostExecute(LocationInfo locAddress) {


            if(mGetPlaceListener != null )
                mGetPlaceListener.onCompleted(locAddress);

        }

        public  List<Address> getStringFromLocation(double lat, double lng,Locale locale)
                throws ClientProtocolException, IOException, JSONException {

            String address = String.format(Locale.ENGLISH,"http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
                            + locale.getCountry(), lat, lng);

            HttpGet httpGet = new HttpGet(address);
            HttpClient client = new DefaultHttpClient();
            StringBuilder stringBuilder = new StringBuilder();

            List<Address> retList = null;


            try {
                HttpResponse response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"));

                String line=null;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                stream.close();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            retList = new ArrayList<Address>();

            if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {

                    JSONObject result = results.getJSONObject(i);
                    Address addr = new Address(locale);
                    addr.setAddressLine(0, result.getString("formatted_address"));

                    JSONArray address_components = result.getJSONArray("address_components");

                    for (int j = 0; j < address_components.length(); j++) {


                        JSONObject component = address_components.getJSONObject(j);
                        JSONArray types =component.getJSONArray("types");

                        for (int k = 0; k < types.length(); k++) {

                            String strType = types.getString(k);
                            if(strType.trim().equals("country")) {
                                addr.setCountryName(component.getString("long_name"));
                                addr.setCountryCode(component.getString("short_name"));
                            }else if(strType.trim().equals("administrative_area_level_1")){
                                addr.setAdminArea(component.getString("long_name"));

                            }else if(strType.trim().equals("locality") || strType.trim().equals("administrative_area_level_2")){
                                addr.setLocality(component.getString("long_name"));
                            }
                        }

                    }
                    retList.add(addr);
                }
            }

            return retList;
        }
    }
}
