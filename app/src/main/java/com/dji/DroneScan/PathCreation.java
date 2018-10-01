package com.dji.DroneScan;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

public class PathCreation extends AppCompatActivity {

    static JSONObject json;
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_creation);

//        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().remove("FlyingJSON").apply();

        // TODO: Put this part off code into the MainActivity

        if (!PreferenceManager.getDefaultSharedPreferences(this).contains("FlyingJSON")) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("FlyingJSON", "{\"actions\":[],\"speed\":\"2\",\"height\":\"1.6\"}").apply();
            Log.d("TestJson", "Preference created");
        }

        // ---------------- END ------------------ //

        String jsonText = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("FlyingJSON", null);
        json = null;
        try {
            json = new JSONObject(jsonText);
        } catch (JSONException e) {
            Log.d("TestJson", "" + e);
        }

        Log.d("TestJson", "json" + json);

        ListView mListView = findViewById(R.id.mListView);

        // TODO: Add creation of new move
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject testobj = new JSONObject();
                    testobj.put("name", "Fly");
                    testobj.put("direction", "back");
                    testobj.put("metOrDeg", "4");
                    json.getJSONArray("actions").put(testobj);
                    saveJSONChanges(json);
                } catch (JSONException e) {
                    Log.d("TestJson", "" + e);
                }

                Log.d("TestJson", "json" + json);
            }
        });

        itemAdapter = new ItemAdapter(this, json);
        mListView.setAdapter(itemAdapter);
    }

    private void saveJSONChanges(JSONObject json) {
        try {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("FlyingJSON", json.toString()).apply();
            itemAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d("TestJson", "" + e);
        }
    }
}
