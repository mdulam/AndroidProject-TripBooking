package com.example.homework07_parta;

import android.os.AsyncTask;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationAsync extends AsyncTask<String, Void, Location> {
    CreateTripActivity context;

    public LocationAsync(CreateTripActivity context) {
        this.context = context;
    }

    @Override
    protected Location doInBackground(String... strings) {
        HttpURLConnection connection = null;
        Location result = new Location();
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            String json = IOUtils.toString(connection.getInputStream(), "UTF8");
            JSONObject root = new JSONObject(json);
            JSONArray message = root.getJSONArray("candidates");
            JSONObject body = message.getJSONObject(0);
            result.setPlace(body.getString("formatted_address"));
            JSONObject location = body.getJSONObject("geometry");
            JSONObject loc = location.getJSONObject("location");
            result.setLatitude(loc.getString("lat"));
            result.setLongitude(loc.getString("lng"));
            JSONArray photoObj = body.getJSONArray("photos");
            JSONObject firstImage = photoObj.getJSONObject(0);
            String width = firstImage.getString("width");
            String PhotoRef = firstImage.getString("photo_reference");
            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth="+width+"&photoreference="+PhotoRef+"&key=AIzaSyAkU_HY5mNwpjqcHmd5Zei04afiy-Fpep4";
            result.setPhotoUrl(photoUrl);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }
    @Override

    protected void onPostExecute(Location obj) {
        super.onPostExecute(obj);
        context.setLocationDetails(obj);
    }
}
