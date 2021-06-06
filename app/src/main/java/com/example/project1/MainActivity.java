package com.example.project1;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView textViewTitle;
    TextView textViewError;
    Button buttonPalettes;
    View color1;
    View color2;
    View color3;
    View color4;
    View color5;

    ArrayList<Palette> palettes;
    Palette currentPalette;
    String[] currentColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewError = findViewById(R.id.textViewError);
        buttonPalettes = findViewById(R.id.buttonPalettes);
        color1 = findViewById(R.id.color1);
        color2 = findViewById(R.id.color2);
        color3 = findViewById(R.id.color3);
        color4 = findViewById(R.id.color4);
        color5 = findViewById(R.id.color5);

        buttonPalettes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPaletteAPI();
            }
        });

                palettes = new ArrayList<Palette>();
                currentColors = new String[5];
    }

    private void getPaletteAPI() {
        // ---- Remember to add the following permission to the AndroidManifest.xml file
        //      <uses-permission android:name="android.permission.INTERNET" />
        String url = "https://www.colourlovers.com/api/palettes/random?format=json";
        // Create a Volley web request to receive back a JSON object.
        // This requires two listeners for Async response, onResponse() and onErrorResponse()
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //parse the first object from the array
                        String jsonPalette = "";
                        String jsonColors = "";
                        Gson gson = new Gson();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                jsonPalette = response.get(i).toString();
                                // implementation 'com.google.code.gson:gson:2.8.6'
                                Palette newPalette = gson.fromJson(jsonPalette, Palette.class);
                                palettes.add(newPalette);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        currentPalette = palettes.get(palettes.size()-1);
                        textViewTitle.setText(currentPalette.title);
                        currentColors = currentPalette.getColors();
                        showColors();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        textViewError.setText("ERROR Response: " + error.toString());
                    }
                });

        // Create a RequestQueue used to send web requests using Volley
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    private void showColors()
    {

        color1.setBackgroundColor(Color.parseColor("#"+currentColors[0]));
        color2.setBackgroundColor(Color.parseColor("#"+currentColors[1]));
        color3.setBackgroundColor(Color.parseColor("#"+currentColors[2]));
        color4.setBackgroundColor(Color.parseColor("#"+currentColors[3]));
        color5.setBackgroundColor(Color.parseColor("#"+currentColors[4]));
    }
}