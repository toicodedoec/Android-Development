/**
 * This project displays a generic name of a drug, a user will pick a brand name
 * accordingly from a list of brand name provided.
 *
 * @author Kim Nguyen
 * @version 09-Jan-2017
 */

package projects.android.nguyen.kim.pharmacyTechPractice.controller;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import projects.android.nguyen.kim.pharmacyTechPractice.CommonConstants;
import projects.android.nguyen.kim.pharmacyTechPractice.R;
import projects.android.nguyen.kim.pharmacyTechPractice.logic.DrugRelatedLogic;

public class QuizActivity extends AppCompatActivity {

    private Cursor cursor;
    private long records;

    final int NO_GENERIC_ON_SCREEN = 5;
    final int GENERIC_COL_INDEX = 1;
    final int FUNCTION_COL_INDEX = 2;
    final int DIRECTION_COL_INDEX = 3;

    private Map<String, String> generatedDrugs = new HashMap<>();
    private Map<String, String> functionAndUsage = new HashMap<>();
    private String brandName = null;
    private String direction = null;
    private ListView drugView;
    private TextView genericView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("QuizActivity", "onCreate start!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        DrugRelatedLogic logic = new DrugRelatedLogic(getApplicationContext());
        drugView = (ListView) findViewById(R.id.drug_list);
        genericView = (TextView) findViewById(R.id.brand);

        records = logic.getNoRecords();

        if (records > 0) {
            cursor = logic.getEntries();
            generateListDrugs();

            // Shows hint when the user clicks on brand name TextView
            genericView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), functionAndUsage.get(brandName), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            finish();
            Log.d("QuizActivity", "No records: " + records);
            Toast.makeText(this, "database is empty", Toast.LENGTH_SHORT).show();
        }

        Log.d("QuizActivity", "onCreate end!");
    }

    /**
     * Generates a list of random generic and a random brand name to appear on the screen
     */
    private void generateListDrugs() {
        Log.d("QuizActivity", "generateListDrugs start!");

        int randNum;

        /* Generate a list of random brand names and its generic */
        if (records <= NO_GENERIC_ON_SCREEN) {
            while (cursor.moveToNext()) {
                generatedDrugs.put(cursor.getString(CommonConstants.KEY_COL_INDEX), cursor.getString(GENERIC_COL_INDEX));
            }
            Log.d("generatedDrugs", generatedDrugs.toString());
        } else {
            generatedDrugs.clear();
            while (generatedDrugs.size() != NO_GENERIC_ON_SCREEN) {
                // unsafe casting
                randNum = generateRandNumber((int) records);
                Log.d("QuizActivity", "randNum: " + randNum);
                cursor.moveToPosition(randNum);
                generatedDrugs.put(cursor.getString(CommonConstants.KEY_COL_INDEX), cursor.getString(GENERIC_COL_INDEX));
                functionAndUsage.put(cursor.getString(CommonConstants.KEY_COL_INDEX), cursor.getString(FUNCTION_COL_INDEX) +
                        "\n" + cursor.getString(DIRECTION_COL_INDEX));
            }
        }

        /* Generates a random brand name from the set of drug name */
        randNum = generateRandNumber(generatedDrugs.size());
        brandName = (String) generatedDrugs.keySet().toArray()[randNum];

        /* Gets the direction of usage according to the brand displayed */
        direction = functionAndUsage.get(brandName);

        Log.d("QuizActivity", "generateListDrugs end!");
    }

    /**
     * Generates a random number within the number of records
     *
     * @return the random number
     */
    private int generateRandNumber(int range) {
        Random randNo = new Random();
        return randNo.nextInt(range);
    }

    @Override
    protected void onResume() {
        Log.d("QuizActivity", "onResume start!");

        super.onResume();

        Log.d("onResume", "onResume start!");

        display();
        pickDrugName();
        Log.d("generatedBradOnResume", brandName);

        Log.d("QuizActivity", "onResume end!");
    }

    /**
     * Displays brand name and list of generic on the screen
     */
    protected void display() {
        Log.d("QuizActivity", "display start!");

        genericView.setText(brandName);

        /* Displays a list of random generic name on the screen */
        ArrayAdapter<String> drugAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, generatedDrugs.values().toArray(new String[0]));
        drugView.setAdapter(drugAdapter);

        /* Displays direction on the screen */
        TextView directionView = (TextView) findViewById(R.id.direction);
        directionView.setText(direction);

        Log.d("QuizActivity", "display end!");
    }

    /**
     * Gives feedback right or wrong when the user clicks on the generic name on the list
     */
    protected void pickDrugName() {
        Log.d("QuizActivity", "pickDrugName start!");

        drugView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int brand, long l) {
                Log.d("position", "the user click on: " + brand);
                Log.d("position", "the user click on: " + generatedDrugs.values().toArray()[brand]);
                Log.d("position", "the user click on: " + generatedDrugs.get(brandName));

                /* Compares the name displayed and the name picked from the list */
                if (generatedDrugs.get(brandName).equals(generatedDrugs.values().toArray()[brand])) {
                    Toast.makeText(QuizActivity.this, "You are awesome!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuizActivity.this, "You suck!", Toast.LENGTH_SHORT).show();
                }
                generateListDrugs();
                display();
            }
        });

        Log.d("QuizActivity", "pickDrugName end!");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("QuizActivity", "onSaveInstanceState start!");

        super.onSaveInstanceState(outState);
        outState.putString("brandName", brandName);
        outState.putString("direction", direction);
        outState.putSerializable("generatedDrugs", (Serializable) generatedDrugs);

        Log.d("generatedBradSave", brandName);
        Log.d("generatedDrugsSave", generatedDrugs.toString());

        Log.d("QuizActivity", "onSaveInstanceState end!");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d("QuizActivity", "onRestoreInstanceState start!");

        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey("brandName") && savedInstanceState.containsKey("generatedDrugs")) {
            brandName = savedInstanceState.getString("brandName");
            direction = savedInstanceState.getString("direction");
            generatedDrugs = (HashMap<String, String>) savedInstanceState.getSerializable("generatedDrugs");
            // generatedDrugs = (HashMap<String, String>) savedInstanceState.getSerializable("generatedDrugs");
            Log.d("onRestoreInstanceState", "onRestoreInstanceState start!");
        }
        Log.d("generatedBradRestore", brandName);
        Log.d("generatedDrugsRestore", generatedDrugs.toString());

        Log.d("QuizActivity", "onRestoreInstanceState end!");
    }

}