package com.example.shiru.nutritionplus;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.json.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonParser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "Lab12:Main";

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    /**
     * Run when our activity comes into view.
     *
     * @param savedInstanceState state that was saved by the activity last time it was paused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up a queue for our Volley requests
        requestQueue = Volley.newRequestQueue(this);

        // Load the main layout for our activity
        setContentView(R.layout.activity_main);

        // Attach the handler to our UI button
        final Button startAPICall = findViewById(R.id.startAPICall);
        final TextInputEditText searchBox = findViewById(R.id.searchBox);
        startAPICall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Start API button clicked");
                final String foodItem = searchBox.getText().toString();
                startAPICall(foodItem);
            }
        });

        // Make sure that our progress bar isn't spinning and style it a bit
        //ProgressBar progressBar = findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Make an API call to search up the food.
     * @param foodItem the food item to be looked up
     */
    void startAPICall(final String foodItem) {
        try {
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("query", foodItem);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    "https://trackapi.nutritionix.com/v2/natural/nutrients",
                    jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            try {
                                Log.d(TAG, response.toString(2));
                                apiCallDone(response);
                            } catch (Exception e) {
                                Log.e(TAG, "don't indent, apiCallDone not executed");
                                Log.d(TAG, response.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.w(TAG, error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("x-app-id", "cb62f9c5");
                    params.put("x-app-key", "88831702f2346577a3743d52cd50cd84");
                    params.put("x-remote-user-id", "0");
                    Log.d(TAG, params.toString());
                    return params;
                }
            };
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * display the json result in the text view.
     * @param response the json object response from startAPICall.
     */
    void apiCallDone(final JSONObject response) {
        try {
            final TextView jsonResult = findViewById(R.id.jsonResult);
            jsonResult.setText(getCalories(parseIt(response)));
        } catch (Exception e) {
            Log.e(TAG, "apiCallDone error");
            e.printStackTrace();
        }
    }
    /**
     * Parses the JSONObject and returns the JSONArray titled "foods".
     * @param response the Json string.
     * @return the IP address parsed into a string
     */
    JSONArray parseIt(JSONObject response) {
        try {
            return response.getJSONArray("foods");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * returns the calorie count.
     * @param array the parsed jsonObject
     * @return the IP address as a string
     */
    String getCalories(final JSONArray array) {
        try {
            return array.getJSONObject(0).getString("nf_calories");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
