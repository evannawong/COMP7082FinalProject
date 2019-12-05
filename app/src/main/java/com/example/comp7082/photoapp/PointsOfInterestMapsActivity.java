package com.example.comp7082.photoapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class PointsOfInterestMapsActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LatLng currentLatLong;
    private List<Attraction> attractions = new ArrayList<Attraction>();
    private LocationManager locationManager;
    private RequestQueue requestQueue;
    private String selectedLocationName;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_of_interest_maps);
        databaseHelper = new DatabaseHelper(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        requestQueue = Volley.newRequestQueue(this);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            currentLatLong = new LatLng(location.getLatitude(), location.getLongitude());
        }
        getAttractions();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void getAttractions(){
        String latitude = String.valueOf(currentLatLong.latitude);
        String longitude = String.valueOf(currentLatLong.longitude);
        //String requesturl = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=tourist_attraction&location=" + latitude + "," + longitude + "&radius=500&key=AIzaSyBMIbgeUGgD-nx4dQ0JG0wkhEliwL2qEgI";
        //JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requesturl, null, new Response.Listener<JSONObject>() {
            //@Override
            //public void onResponse(JSONObject response) {
                String json = "{\"html_attributions\":[],\"results\":[{\"formatted_address\":\"Vancouver, BC V6G 1Z4, Canada\",\"geometry\":{\"location\":{\"lat\":49.30425839999999,\"lng\":-123.1442522},\"viewport\":{\"northeast\":{\"lat\":49.31845509999999,\"lng\":-123.13134515},\"southwest\":{\"lat\":49.28564750000002,\"lng\":-123.14855455}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_recreational-71.png\",\"id\":\"bb26e55c3f8cfb491e7ab7a8d4422fd04ea4a084\",\"name\":\"Stanley Park\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":3120,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/103867850905076094769\\\">Jak Pool<\\/a>\"],\"photo_reference\":\"CmRaAAAAa8tKHtwbwzSOKANDdtXyDhMXHJWVpgpmkB1t7KTZslOvEowVsvTY_lTkBJ2GBP_lx0z4GtSVEelFFtfzaSBOhSisHz9-BI_YB941_CD8jo3atopM4htTCUR9EznnKcaCEhCecJL3nZlVyYGZ_uK0Rh9MGhSO9EQO2yg1D7ZyBjInOR_kzYmjwg\",\"width\":4160}],\"place_id\":\"ChIJo-QmrYxxhlQRFuIJtJ1jSjY\",\"plus_code\":{\"compound_code\":\"8V34+P7 Vancouver, British Columbia\",\"global_code\":\"84XR8V34+P7\"},\"rating\":4.8,\"reference\":\"ChIJo-QmrYxxhlQRFuIJtJ1jSjY\",\"types\":[\"park\",\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":28334},{\"formatted_address\":\"305 Water St, Vancouver, BC V6B 1B9, Canada\",\"geometry\":{\"location\":{\"lat\":49.2844832,\"lng\":-123.1090011},\"viewport\":{\"northeast\":{\"lat\":49.28577322989272,\"lng\":-123.1076778201072},\"southwest\":{\"lat\":49.28307357010727,\"lng\":-123.1103774798927}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_business-71.png\",\"id\":\"4a4c7aa097fd71703e24a1f4f26992b41720991d\",\"name\":\"Gastown Vancouver Steam Clock\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":4032,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/104188067731561469285\\\">Juergen Doersam<\\/a>\"],\"photo_reference\":\"CmRaAAAAjFIkxN-9VlsMlPJ4GCgCEDEZXbjSIO-Bna0CuQSSLCOJVHUR-KbQ7cJq19aV4FVkXpIGYo-UwkbS8D_wNmCI71-ku636xIu5wR1lIVIQq2yt_x5kCf1dRngKoIH6b4KlEhBOIuD0I16iQ98WczDJkieLGhQrkHd9qw1IqNaLa_6brK82G4wjJg\",\"width\":2268}],\"place_id\":\"ChIJ36zlvedzhlQRAFUUd93cPgg\",\"plus_code\":{\"compound_code\":\"7VMR+Q9 Vancouver, British Columbia\",\"global_code\":\"84XR7VMR+Q9\"},\"rating\":4.4,\"reference\":\"ChIJ36zlvedzhlQRAFUUd93cPgg\",\"types\":[\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":10715},{\"formatted_address\":\"3820 Bayview St, Richmond, BC V7E 4R7, Canada\",\"geometry\":{\"location\":{\"lat\":49.1238268,\"lng\":-123.1842687},\"viewport\":{\"northeast\":{\"lat\":49.12534762989272,\"lng\":-123.1828617701073},\"southwest\":{\"lat\":49.12264797010727,\"lng\":-123.1855614298927}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_business-71.png\",\"id\":\"67e06c50745ba668be0d9a33bc20e5694205c534\",\"name\":\"Steveston Harbour\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":4032,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/106417702542448143417\\\">Rafael Sotomayor<\\/a>\"],\"photo_reference\":\"CmRaAAAA9MGg-lqmRkPluQZSs3TSG4_cox0J2OsyW-sjj50CvP01RUiwAYtaYctqPbr4sYbOMja4jE0GAlxOM82xOqruH-qf6l18YDaujLiwuV7m33FmaqAy1fuKqnkjl-Xa6IKzEhB71BpZ8GITteVMFD5xzTxhGhTx5FYKkydmtTrPS-CtHVkchI6L1Q\",\"width\":3024}],\"place_id\":\"ChIJ3dFLpNnhhVQRLC4LpphPOy0\",\"plus_code\":{\"compound_code\":\"4RF8+G7 Richmond, Greater Vancouver A, BC\",\"global_code\":\"84XR4RF8+G7\"},\"rating\":4.4,\"reference\":\"ChIJ3dFLpNnhhVQRLC4LpphPOy0\",\"types\":[\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":3162},{\"formatted_address\":\"110 Cambie St, Vancouver, BC V6B 2M8, Canada\",\"geometry\":{\"location\":{\"lat\":49.2846091,\"lng\":-123.1084744},\"viewport\":{\"northeast\":{\"lat\":49.28597402989271,\"lng\":-123.1072259201072},\"southwest\":{\"lat\":49.28327437010727,\"lng\":-123.1099255798927}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_business-71.png\",\"id\":\"ed2674e0ef2cafb8ca3d481554dee8f0fa4e34a9\",\"name\":\"Electric Harbour Tours\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":2320,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/103625901034574292200\\\">A Google User<\\/a>\"],\"photo_reference\":\"CmRaAAAAp0q4ZiYL_GnvFC4jP1XMqbfQmOZGpaMbK1Zeh6VzbY7D_YoXCPXQw3r4DWOSAycn8s7LsNh2S31Ej4JilRwHXU0RwnmTmemoMvQozpsTn2Gxc5zYoKWp0hUVKgtWWCOcEhCoNOPeg1NHcMo7KD52xKUeGhQ0Ai4FKfOHGqX9WtKmKOtArH5Vxg\",\"width\":4126}],\"place_id\":\"ChIJaQ5ctRtxhlQRb7dP8iEUJ-Y\",\"plus_code\":{\"compound_code\":\"7VMR+RJ Vancouver, British Columbia\",\"global_code\":\"84XR7VMR+RJ\"},\"rating\":4.5,\"reference\":\"ChIJaQ5ctRtxhlQRb7dP8iEUJ-Y\",\"types\":[\"tourist_attraction\",\"travel_agency\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":8},{\"formatted_address\":\"439 Seymour St, Vancouver, BC V6B 1P2, Canada\",\"geometry\":{\"location\":{\"lat\":49.28437599999999,\"lng\":-123.1134323},\"viewport\":{\"northeast\":{\"lat\":49.28571232989272,\"lng\":-123.1120614201073},\"southwest\":{\"lat\":49.28301267010728,\"lng\":-123.1147610798927}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_business-71.png\",\"id\":\"432f02dc93b21d218e0d0ef0d46fb63066ec3e42\",\"name\":\"Alley Oop\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":2289,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/114514818669976784092\\\">Roland Haynes<\\/a>\"],\"photo_reference\":\"CmRaAAAAvY0KilmzzcR5c2-_P3lJ1VRgoCWN48qJ50OOHxm7X3RnR1neYllKobt_65GVJlwgLCyfIrEzLZcoEa0mlYo3pk6T9ydiBefaLSNBZCiEoincdenpgKMJZ09c9mAze0XhEhAIPGay79po8gQ1203w2rCvGhSL4csiLIPSUVj2n_6SHFt8quHJZw\",\"width\":1717}],\"place_id\":\"ChIJnbL8jHhxhlQRhnKEirtW8B8\",\"plus_code\":{\"compound_code\":\"7VMP+QJ Vancouver, British Columbia\",\"global_code\":\"84XR7VMP+QJ\"},\"rating\":4,\"reference\":\"ChIJnbL8jHhxhlQRhnKEirtW8B8\",\"types\":[\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":151},{\"formatted_address\":\"209 Union St, Vancouver, BC V6A 3A1, Canada\",\"geometry\":{\"location\":{\"lat\":49.2777604,\"lng\":-123.0993048},\"viewport\":{\"northeast\":{\"lat\":49.27906302989271,\"lng\":-123.0979566201073},\"southwest\":{\"lat\":49.27636337010727,\"lng\":-123.1006562798927}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_business-71.png\",\"id\":\"380f2c2acf987295c57054c0dc9096e62b6f9a91\",\"name\":\"Jimi Hendrix Shrine\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":5312,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/117385652981884866706\\\">Daniel Topping<\\/a>\"],\"photo_reference\":\"CmRZAAAAl23k9rmA4pTRVYq_D85kyaZWbc7lTkZbWpeX10Bn6i0CEAdvm8hZcH4N5yePcGT187BNc1kRthUWomYt4UpdnDgK_qYGDWHrhUW8KWXAkqJ3_xNZGVYDuITsPhpw8Q2QEhDeuE1D13AZxJkwam1HvhP1GhRNpYeMsVlSG2PFML7OtTMvzQf7Ug\",\"width\":2988}],\"place_id\":\"ChIJL0j3BHlxhlQR1VzKh8wBM_A\",\"plus_code\":{\"compound_code\":\"7WH2+47 Vancouver, British Columbia\",\"global_code\":\"84XR7WH2+47\"},\"rating\":2.6,\"reference\":\"ChIJL0j3BHlxhlQR1VzKh8wBM_A\",\"types\":[\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":12},{\"formatted_address\":\"398 W 10th Ave, Vancouver, BC V5Y 1S3, Canada\",\"geometry\":{\"location\":{\"lat\":49.26213509999999,\"lng\":-123.1127501},\"viewport\":{\"northeast\":{\"lat\":49.26352912989272,\"lng\":-123.1113986701073},\"southwest\":{\"lat\":49.26082947010728,\"lng\":-123.1140983298928}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_business-71.png\",\"id\":\"522d4bb97f95507ebcfc193186c27a131eaf6bcc\",\"name\":\"Dad’s Cookies plaque\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":4032,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/100179790560042267644\\\">Berenice Gonzalez<\\/a>\"],\"photo_reference\":\"CmRaAAAAFVIE5c5_qqGrK9BrhYWMSd3L9yyZVBJygGt-N5wKwNC3pIuLksNZ1Uf4-bRpglooZ9fXpvqswpNTeph1RR193PTgknqHAxvqhI_YczeeXne0f_MLphQjqCJdh5w-sQj5EhAE8nAts9wC5Uud8dTitQyNGhRsJw2me3hxBPOPi1287imEOvW9gA\",\"width\":3024}],\"place_id\":\"ChIJQZFbm0pzhlQRRmuQJbauu70\",\"plus_code\":{\"compound_code\":\"7V6P+VV Vancouver, British Columbia\",\"global_code\":\"84XR7V6P+VV\"},\"rating\":5,\"reference\":\"ChIJQZFbm0pzhlQRRmuQJbauu70\",\"types\":[\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":1},{\"formatted_address\":\"1055 Canada Pl, Vancouver, BC V6C 0C3, Canada\",\"geometry\":{\"location\":{\"lat\":49.2893571,\"lng\":-123.1176645},\"viewport\":{\"northeast\":{\"lat\":49.29043587989273,\"lng\":-123.1164886701073},\"southwest\":{\"lat\":49.28773622010728,\"lng\":-123.1191883298927}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_business-71.png\",\"id\":\"6cf3f1d32ddcd41daca3321fdb68d4582762c9ca\",\"name\":\"Olympic Cauldron @ Jack Poole Plaza\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":3024,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/103725279120167160709\\\">Bunsen L.<\\/a>\"],\"photo_reference\":\"CmRaAAAA2W2ow_15eYzxsSktL-KgdxoZd1JItXxrVx0mrFhvK1XCiKfo1GoEzWgK4hXlYGlufMdaB4b3eyjtHVpX4voKokX8vPReyZYkLRRxawqABXHl5jDLzb7Fit6WGIPmLjBzEhBwZ3Uhrb-YJQOcHTtE_UWjGhQElkKs-J1bco29UaXc6W4SwYibIA\",\"width\":4032}],\"place_id\":\"ChIJn8PlcINxhlQRAyBYG83wzFM\",\"plus_code\":{\"compound_code\":\"7VQJ+PW Vancouver, British Columbia\",\"global_code\":\"84XR7VQJ+PW\"},\"rating\":4.4,\"reference\":\"ChIJn8PlcINxhlQRAyBYG83wzFM\",\"types\":[\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":747},{\"formatted_address\":\"1415 Johnston St, Vancouver, BC V6H 3S1, Canada\",\"geometry\":{\"location\":{\"lat\":49.27143840000001,\"lng\":-123.1329452},\"viewport\":{\"northeast\":{\"lat\":49.27276407989272,\"lng\":-123.1316378701073},\"southwest\":{\"lat\":49.27006442010727,\"lng\":-123.1343375298927}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_business-71.png\",\"id\":\"5eeb36e5f1d8e04dce80fca75d4151f724620996\",\"name\":\"Giants Murals\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":1365,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/109045140282519942443\\\">awoisoak<\\/a>\"],\"photo_reference\":\"CmRZAAAAzgUyk2HAo6dcNtMbs0uu8MBDVVU8PeGhxr0NeAetfZWuO396vw3sSd1EyGNk8lc_by5HcefUL8QgrsgrdxA2h9WikVh1Hcem1ZMOjpcCAV7bke8iTbCEMxM70T0wakD-EhDDEuxc5FnpIrieDJM6ZTMHGhTk_H5BrRXkPEnOk8ZtpJDzDA5BkQ\",\"width\":2048}],\"place_id\":\"ChIJNSGTN85zhlQR6rXdgRsaS7g\",\"plus_code\":{\"compound_code\":\"7VC8+HR Vancouver, British Columbia\",\"global_code\":\"84XR7VC8+HR\"},\"rating\":4.4,\"reference\":\"ChIJNSGTN85zhlQR6rXdgRsaS7g\",\"types\":[\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":17},{\"formatted_address\":\"1095 Ogden, Vancouver, BC V6J 1A3, Canada\",\"geometry\":{\"location\":{\"lat\":49.2775334,\"lng\":-123.147205},\"viewport\":{\"northeast\":{\"lat\":49.27887812989272,\"lng\":-123.1457169201072},\"southwest\":{\"lat\":49.27617847010728,\"lng\":-123.1484165798927}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_business-71.png\",\"id\":\"4572aaa99dcaaf701881759aa1104b6980e542a4\",\"name\":\"St. Roch\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":3036,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/104917251937954195760\\\">Jonathan Polak<\\/a>\"],\"photo_reference\":\"CmRaAAAACaUBCwLAMLg93jCKvQpQlKtcn4A3BHJe-H-Ywjj_bIaoSni96Y4jYR4ljpw7-IJlRS4caol-d-TxEbwnRbNocYLq68hdLpBif_wsR0H3YCbY3pLw3ShOnt-j4BTWRgVDEhAn98gchronCCRUFUFPEbh8GhRcQaGOj-6dl6rjb2yc_hBfs8WxSA\",\"width\":4048}],\"place_id\":\"ChIJ1Z9UgDRyhlQRDa5EUSNJuA8\",\"plus_code\":{\"compound_code\":\"7VH3+24 Vancouver, British Columbia\",\"global_code\":\"84XR7VH3+24\"},\"rating\":4.8,\"reference\":\"ChIJ1Z9UgDRyhlQRDa5EUSNJuA8\",\"types\":[\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":14},{\"formatted_address\":\"Vancouver, BC V6G 3E2, Canada\",\"geometry\":{\"location\":{\"lat\":49.2992464,\"lng\":-123.1208492},\"viewport\":{\"northeast\":{\"lat\":49.30058787989272,\"lng\":-123.1197512201073},\"southwest\":{\"lat\":49.29788822010728,\"lng\":-123.1224508798927}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_business-71.png\",\"id\":\"bcf907de43906925a29a037d44ba63a9b473fb7c\",\"name\":\"Totem Poles\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":3680,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/111316765866560120214\\\">A Google User<\\/a>\"],\"photo_reference\":\"CmRaAAAAAwDR27GGdkeFNM-I4n3e0ZSAD2x4AYqb-jEUpj70j7HCkyZsvk6edfiNzTy2RiT0tPiXs_0xl0hPqVNMqi5j9RpbvYe97vWsogM0MeKPh_9yTboiO5efu21FxALpzp73EhCDEgcfaQSX6TMwSIsWYT2fGhSQvBmPzNYGWjv_AZ186DplP3plKg\",\"width\":2760}],\"place_id\":\"ChIJc45T4pFxhlQRQQLHRz6u6j0\",\"plus_code\":{\"compound_code\":\"7VXH+MM Vancouver, British Columbia\",\"global_code\":\"84XR7VXH+MM\"},\"rating\":4.4,\"reference\":\"ChIJc45T4pFxhlQRQQLHRz6u6j0\",\"types\":[\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":4083},{\"formatted_address\":\"1814 Mast Tower Ln, Vancouver, BC V6H 3S4, Canada\",\"geometry\":{\"location\":{\"lat\":49.271297,\"lng\":-123.1364328},\"viewport\":{\"northeast\":{\"lat\":49.27270922989272,\"lng\":-123.1350849201072},\"southwest\":{\"lat\":49.27000957010728,\"lng\":-123.1377845798927}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_business-71.png\",\"id\":\"fef6acc39054fe5e97c09e9dc9056702ef907b2e\",\"name\":\"Vancouver Sport Fishing Center\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":3024,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/104314994775402277795\\\">Vancouver Sport Fishing Center<\\/a>\"],\"photo_reference\":\"CmRaAAAAE2owv8fs_M3PQotaNCjyepfJfJf_HBm1t-4D4jwZnACFHU4X9o56uJT2nRkkSsdc8Wroc2pawtWqk4IhqjUkXsBN7-Czajnk01sq2etGv-wNgby3NgwVBABNIEmYKko3EhDVh4jyG7wNQZ3IDQiIb_HJGhSAWffzUqkm1D9yMY-mXO4BYmSmAQ\",\"width\":4032}],\"place_id\":\"ChIJoW1jfM5zhlQRBID3jwG1mok\",\"plus_code\":{\"compound_code\":\"7VC7+GC Vancouver, British Columbia\",\"global_code\":\"84XR7VC7+GC\"},\"rating\":3.7,\"reference\":\"ChIJoW1jfM5zhlQRBID3jwG1mok\",\"types\":[\"tourist_attraction\",\"travel_agency\",\"point_of_interest\",\"store\",\"establishment\"],\"user_ratings_total\":6},{\"formatted_address\":\"4600 Cambie St, Vancouver, BC V5Z 2Z1, Canada\",\"geometry\":{\"location\":{\"lat\":49.241757,\"lng\":-123.1126193},\"viewport\":{\"northeast\":{\"lat\":49.24674145000001,\"lng\":-123.09962195},\"southwest\":{\"lat\":49.23406325,\"lng\":-123.12313375}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_recreational-71.png\",\"id\":\"0060681017b1c68a0907b632cfb3f3237628d9b4\",\"name\":\"Queen Elizabeth Park\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":4032,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/114197960197227430620\\\">Nick Brunner<\\/a>\"],\"photo_reference\":\"CmRaAAAAsmPanxqSsxVk3SG25W87ZTWkTMP82ns4-63Ck-8By_5fdGmzIDIRBR_CjWigxT6z7lwqC4zmMephIsjybxjGLJdMMwhqQzV7tLWkKXILM_woh4wdntuVdzKY87KpheHAEhCP0qdzMfmPgX80AoBjPJlCGhRbFdKJnEWDmge7X7J37RNN6SipQw\",\"width\":3024}],\"place_id\":\"ChIJIcZrTvVzhlQRiKTnD03vt7Q\",\"plus_code\":{\"compound_code\":\"6VRP+PX Vancouver, British Columbia\",\"global_code\":\"84XR6VRP+PX\"},\"rating\":4.6,\"reference\":\"ChIJIcZrTvVzhlQRiKTnD03vt7Q\",\"types\":[\"park\",\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":7935},{\"formatted_address\":\"7291 Moffatt Rd, Richmond, BC V6Y 1X9, Canada\",\"geometry\":{\"location\":{\"lat\":49.16044100000001,\"lng\":-123.144995},\"viewport\":{\"northeast\":{\"lat\":49.16178552989273,\"lng\":-123.1430047201073},\"southwest\":{\"lat\":49.15908587010728,\"lng\":-123.1457043798927}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_business-71.png\",\"id\":\"0a6f3a25de7dd637568ae80af1212286b8507bb4\",\"name\":\"tourism vancouver 温哥华旅游网\",\"opening_hours\":{\"open_now\":true},\"place_id\":\"ChIJlT1mnPELhlQRsF7CJlDufCw\",\"plus_code\":{\"compound_code\":\"5V64+52 Richmond, Greater Vancouver A, BC\",\"global_code\":\"84XR5V64+52\"},\"rating\":0,\"reference\":\"ChIJlT1mnPELhlQRsF7CJlDufCw\",\"types\":[\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":0},{\"formatted_address\":\"Vancouver, BC V6G 3E2, Canada\",\"geometry\":{\"location\":{\"lat\":49.2960965,\"lng\":-123.1279796},\"viewport\":{\"northeast\":{\"lat\":49.29767542989272,\"lng\":-123.1264602701073},\"southwest\":{\"lat\":49.29497577010728,\"lng\":-123.1291599298927}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_business-71.png\",\"id\":\"76b2125b9f5ee3eaed1b144580ef5f09e4743781\",\"name\":\"Vancouver Seawall\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":613,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/114425536168618619220\\\">Moshiur Rahman<\\/a>\"],\"photo_reference\":\"CmRaAAAAJf4Dcq5UT7goRvROsNH_ZvUrmwBvo2xEU2gbcFwhLxiYymlMVncx_goDCz_rXL9nssoURxRdP7jPA6R6zuflk8LTbtQdu-tZJXC1HTNTMnzD-X_iPnL_7CzP6AuruRYXEhAIbKQbqeLIwdbvBS-DXiSpGhQYdNQd9lJZRYBLwgrOIOtG98rUvQ\",\"width\":817}],\"place_id\":\"ChIJm9BY9o5xhlQRtZBMs5-fqrI\",\"plus_code\":{\"compound_code\":\"7VWC+CR Vancouver, British Columbia\",\"global_code\":\"84XR7VWC+CR\"},\"rating\":4.7,\"reference\":\"ChIJm9BY9o5xhlQRtZBMs5-fqrI\",\"types\":[\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":1354},{\"formatted_address\":\"6675 Commercial St, Vancouver, BC V5P 3P5, Canada\",\"geometry\":{\"location\":{\"lat\":49.2235036,\"lng\":-123.0704754},\"viewport\":{\"northeast\":{\"lat\":49.22506867989272,\"lng\":-123.0682644},\"southwest\":{\"lat\":49.22236902010728,\"lng\":-123.0712124}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_recreational-71.png\",\"id\":\"225fea60ebe319f761af90f461e29031bab7e3d1\",\"name\":\"Gordon Park\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":3024,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/106225432944363198312\\\">Kunal Kohly<\\/a>\"],\"photo_reference\":\"CmRaAAAA5C1cQpgzyNWtWm_SIy8L6XASVurRzn4Tcnq4XreDd6LDnqagf4roQ00eapb7XZytxS8mOTQEZaNkbWxaCu2K4bGXMp23B8nzNYJAG92-zVq4tThIT13y_gMYhGGEkFLIEhDWdMM41Qb1X5mTMcirI4PaGhQiKGbn9AISOtXCTlc57fkjGgOu-w\",\"width\":4032}],\"place_id\":\"ChIJ____Ezt0hlQRbH4nQIeSKys\",\"plus_code\":{\"compound_code\":\"6WFH+CR Vancouver, British Columbia\",\"global_code\":\"84XR6WFH+CR\"},\"rating\":4.2,\"reference\":\"ChIJ____Ezt0hlQRbH4nQIeSKys\",\"types\":[\"park\",\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":175},{\"formatted_address\":\"Vancouver Convention Centre West Building, 1055 Canada Pl, Vancouver, BC, Canada\",\"geometry\":{\"location\":{\"lat\":49.28941529999999,\"lng\":-123.1141654},\"viewport\":{\"northeast\":{\"lat\":49.29011149999999,\"lng\":-123.1132598701073},\"southwest\":{\"lat\":49.28732669999999,\"lng\":-123.1159595298927}}},\"icon\":\"https:\\/\\/maps.gstatic.com\\/mapfiles\\/place_api\\/icons\\/generic_business-71.png\",\"id\":\"d6bd3f7d2d38755827b30b9c9f129e7f9bc1c1f3\",\"name\":\"The Drop\",\"opening_hours\":{\"open_now\":true},\"photos\":[{\"height\":4032,\"html_attributions\":[\"<a href=\\\"https:\\/\\/maps.google.com\\/maps\\/contrib\\/111541226408519090315\\\">Kelvin Tang<\\/a>\"],\"photo_reference\":\"CmRaAAAA4ERa5VOPGv1aDUnYXTl0bYjXjdYVfBQXEVjpbhkrwmyHoti9jBUVf9ZECnDFP86jfVRjwdEATHQH1JhbCHA01V1m-M1ZgLX8P9LzF_XSlfPDg-R-W3om3kHd2LOHOffqEhDPPNnK1go1_xkD6xvIbw5sGhRVFWLEAxqiDYMTsBWkJCETL1fYlw\",\"width\":3024}],\"place_id\":\"ChIJI3nNNINxhlQRIwZf0xiQem4\",\"plus_code\":{\"compound_code\":\"7VQP+Q8 Vancouver, British Columbia\",\"global_code\":\"84XR7VQP+Q8\"},\"rating\":4.4,\"reference\":\"ChIJI3nNNINxhlQRIwZf0xiQem4\",\"types\":[\"tourist_attraction\",\"point_of_interest\",\"establishment\"],\"user_ratings_total\":275}],\"status\":\"OK\"}";
                try {
                    JSONObject response = new JSONObject(json);
                    JSONArray jsonArray = response.getJSONArray("results");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject geometry = jsonObject.getJSONObject("geometry");
                        JSONObject location =  geometry.getJSONObject("location");
                        Attraction attraction = new Attraction(
                                jsonObject.getString("name"),
                                jsonObject.getString("formatted_address"),
                                Float.valueOf(location.getString("lat")),
                                Float.valueOf(location.getString("lng"))
                        );
                        attractions.add(attraction);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            //}
//        },new Response.ErrorListener(){
//            @Override public void onErrorResponse(VolleyError error){
//                error.printStackTrace();
//            }
//        });

        //requestQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(googleMap != null){
            mMap = googleMap;
            mMap.setOnMarkerClickListener(this);
            for(Attraction attraction : attractions){
                try {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(attraction.getLatitude(), attraction.getLongitude()))
                            .title(attraction.getName()));

                    LatLng newPosition = new LatLng(attraction.getLatitude(), attraction.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(newPosition).zoom(10).build();

                    mMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        selectedLocationName = marker.getTitle();
        TextView pointOfInterest = findViewById(R.id.pointOfInterest);
        pointOfInterest.setText(marker.getTitle());
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    public void onAddToListClicked(View view) {
        if(selectedLocationName != null && selectedLocationName != ""){
            boolean insertData = databaseHelper.addData(selectedLocationName);

            if(insertData){
                Toast.makeText(getApplicationContext(), "Successfully added to favorites list", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Unable to add to list.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onViewFavoritesClicked(View view){
        Intent intent = new Intent(PointsOfInterestMapsActivity.this, FavoriteListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Latitude","lat: " + location.getLatitude() + " " + "long: " + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }
}
