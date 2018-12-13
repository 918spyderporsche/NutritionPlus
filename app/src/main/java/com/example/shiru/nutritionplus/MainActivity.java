package com.example.shiru.nutritionplus;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.json.*;

import com.bumptech.glide.Glide;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
@TargetApi(24)
public class MainActivity extends AppCompatActivity {
    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "Lab12:Main";

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    private static final int DAILY_CALORIES = 2800;
    private static List<Double> COLLECTION_OF_FOOD = new LinkedList<>();
    private static double TEMP_CALORIES = 0.0;
    private static String[] nutrients = {
            "nf_calories",
            "nf_total_fat",
            "nf_saturated_fat",
            "nf_cholesterol",
            "nf_sodium",
            "nf_total_carbohydrate",
            "nf_dietary_fiber",
            "nf_sugars",
            "nf_protein",
            "nf_potassium"
    };

    private static Map<String, String> nutritionDict = new HashMap<>();
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

        // Make sure that our progress bar isn't spinning and style it a bit
        final ProgressBar waitingBar = findViewById(R.id.waiting);
        waitingBar.setVisibility(View.INVISIBLE);

        // handler for search button
        final Button startAPICall = findViewById(R.id.startAPICall);
        final TextInputEditText searchBox = findViewById(R.id.searchBox);
        startAPICall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Start API button clicked");
                final String foodItem = searchBox.getText().toString();
                if (foodItem.length() == 0) {
                    Context context = getApplicationContext();
                    CharSequence warning = "Please enter a food item";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, warning, duration);
                    toast.show();
                } else {
                    startAPICall(foodItem);
                    waitingBar.setVisibility(View.VISIBLE);
                }
            }
        });

        //handler for add button
        final Button add = findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d(TAG, "Add button clicked");
                    //CALORIES_PER_MEAL += TEMP_CALORIES;
                    COLLECTION_OF_FOOD.add(TEMP_CALORIES);
                    setMealProgress();
                } catch (Exception e) {
                    Log.e(TAG, "Add button error");
                    e.printStackTrace();
                }
            }
        });

        //handler for delete button
        final Button delete = findViewById(R.id.deleteButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d(TAG, "Delete button clicked");
                    if (COLLECTION_OF_FOOD.size() > 0) {
                        COLLECTION_OF_FOOD.remove(COLLECTION_OF_FOOD.size() - 1);
                        setMealProgress();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Delete button error");
                }
            }
        });

        //handler for nutrition button
        final Button fullNutritionButton = findViewById(R.id.fullNutritionButton);
        fullNutritionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final TextInputEditText searchBox = findViewById(R.id.searchBox);
                    String foodItem = searchBox.getText().toString();
                    if (foodItem.length() == 0) {
                        Context context = getApplicationContext();
                        CharSequence warning = "Please enter a food item";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, warning, duration);
                        toast.show();
                    } else {
                        openFullNutritionList();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "nutrition button error: " + e.getMessage());
                }
            }
        });
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
                    Log.e(TAG, "API call failed");
                    Log.w(TAG, error.toString());
                    final ProgressBar waitingBar = findViewById(R.id.waiting);
                    waitingBar.setVisibility(View.INVISIBLE);
                    Context context = getApplicationContext();
                    CharSequence warning = "No info is available for this food item";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, warning, duration);
                    toast.show();
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
            JSONObject parsed = parseIt(response);
            nutritionDict = generateNutritionDict(parsed);
            //setTotalFat(parsed);
            //setCholesterol(parsed);
            //setCarbohydrate(parsed);
            String calories = getCalories(parsed);
            showCalories(calories);
            setPerFoodProgress(calories);
            showImage(parseIt(response));
            TEMP_CALORIES = Double.parseDouble(calories);
            final ProgressBar waitingBar = findViewById(R.id.waiting);
            waitingBar.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.getMessage();
        }
    }
    /**
     * Parses the JSONObject and returns the JSONArray titled "foods".
     * @param response the Json string.
     * @return the IP address parsed into a string
     */
    JSONObject parseIt(JSONObject response) {
        try {
            return response.getJSONArray("foods").getJSONObject(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * returns the calorie count.
     * @param jsonObject the parsed jsonObject
     * @return the IP address as a string
     */
    String getCalories(final JSONObject jsonObject) {
        try {
            return jsonObject.getString("nf_calories");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    void showImage(JSONObject jsonObject) {
        try {
            String url = jsonObject.getJSONObject("photo").getString("thumb");
            final ImageView imageView = findViewById(R.id.imageView);
            Glide.with(this).load(url).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setPerFoodProgress(String calories) {
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        int percentage = (int) Double.parseDouble(calories) * 100 / (DAILY_CALORIES / 3);
        if (percentage > 100) {
            percentage = 100;
        }
        progressBar.setProgress(percentage, true);

        final TextView percentageDisplay = findViewById(R.id.percentageDisplay);
        String percent = Integer.toString(percentage) + "%";
        percentageDisplay.setText(percent);
    }

    void showCalories(String calories) {
        final TextView jsonResult = findViewById(R.id.jsonResult);
        String message = "calorie count: " + calories;
        jsonResult.setText(message);
    }

    void setMealProgress() {
        final ProgressBar percentPerMeal = findViewById(R.id.percentPerMeal);
        double calorieTotal = 0.0;
        for (int i = 0; i < COLLECTION_OF_FOOD.size(); i++) {
            calorieTotal += COLLECTION_OF_FOOD.get(i);
        }
        int percentageInt = (int) calorieTotal * 100 / (DAILY_CALORIES / 3);
        if (percentageInt > 100) {
            percentageInt = 100;
        }
        percentPerMeal.setProgress(percentageInt, true);

        final TextView percentageTotal = findViewById(R.id.percentageTotal);
        String percent = Integer.toString(percentageInt) + "%";
        percentageTotal.setText(percent);
    }

    void openFullNutritionList() {
        Intent intent = new Intent(this, FullNutritionList.class);
        startActivity(intent);
    }

    public Map<String, String> generateNutritionDict(JSONObject jsonObject) {
        Map<String, String> nutritionDict = new HashMap<>();
        String value;
        for (String nutrient : nutrients) {
            try {
                value = jsonObject.getString(nutrient);
            } catch (Exception e) {
                value = "no info available";
            }
            nutritionDict.put(nutrient, value);
        }
        return nutritionDict;
    }
    public static String[] getNutrients() { return nutrients; }
    public static Map<String, String> getNutritionDict() { return nutritionDict; }

    /*
    void setTotalFat(final JSONObject jsonObject) {
        try {
            TOTAL_FAT = jsonObject.getString("nf_total_fat");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setCholesterol(final JSONObject jsonObject) {
        try {
            CHOLESTEROL = jsonObject.getString("nf_cholesterol");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setCarbohydrate(final JSONObject jsonObject) {
        try {
            CARBOHYDRATE = jsonObject.getString("nf_total_carbohydrate");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTotalFat() { return TOTAL_FAT; }
    public static String getCholesterol() { return CHOLESTEROL; }
    public static String getCarbohydrate() { return CARBOHYDRATE; }


    private static String TOTAL_FAT;
    private static String CHOLESTEROL;
    private static String CARBOHYDRATE;
    */
}
