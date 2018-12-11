package com.example.shiru.nutritionplus;

import android.annotation.TargetApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Map;
@TargetApi(24)
public class FullNutritionList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_nutrition_list);
        try {
            showNutritionInfo();
            //showTotalFat();
            //showCholesterol();
            //showCarbohydrate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void showNutritionInfo() {
        String[] nutrients = MainActivity.getNutrients();
        Map<String, String> nutritionDict = MainActivity.getNutritionDict();
        String message = "";

        for (int i = 0; i < nutrients.length; i++) {
            String header = "";
            String[] array = nutrients[i].split("_");
            if (array.length == 2) {
                header = array[1];
            } else if (array.length == 3) {
                header = array[1] + " " + array[2];
            }
            message += header + ": " + nutritionDict.getOrDefault(nutrients[i], "no info") + "\n";
        }
        final TextView nutritionInfo = findViewById(R.id.nutritionInfo);
        nutritionInfo.setSingleLine(false);
        nutritionInfo.setText(message);
    }

    /*
    void showTotalFat() {
        final TextView totalFat = findViewById(R.id.totalFat);
        String message = "total fat: " + MainActivity.getTotalFat();
        totalFat.setText(message);
    }
    void showCholesterol() {
        final TextView cholesterol = findViewById(R.id.cholesterol);
        String message = "cholesterol: " + MainActivity.getCholesterol();
        cholesterol.setText(message);
    }
    void showCarbohydrate() {
        final TextView carbohydrate = findViewById(R.id.carbohydrate);
        String message = "carbohydrate: " + MainActivity.getCarbohydrate();
        carbohydrate.setText(message);
    }
    */
}
