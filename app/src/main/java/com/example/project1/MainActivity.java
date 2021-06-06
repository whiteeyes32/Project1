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
    Button buttonPrevious;
    Button buttonDownload;
    Button buttonNext;

    View color1;
    View color2;
    View color3;
    View color4;
    View color5;

    ArrayList<Palette> palettes;
    Palette currentPalette;
    String[] currentColors;
    int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gets view elements
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewError = findViewById(R.id.textViewError);

        buttonPalettes = findViewById(R.id.buttonPalettes);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonDownload = findViewById(R.id.buttonDownload);
        buttonNext = findViewById(R.id.buttonNext);

        color1 = findViewById(R.id.color1);
        color2 = findViewById(R.id.color2);
        color3 = findViewById(R.id.color3);
        color4 = findViewById(R.id.color4);
        color5 = findViewById(R.id.color5);

        //initializes variables
        palettes = new ArrayList<Palette>();
        currentColors = new String[5];
        //current index is set to -1.
        //We don't have any palettes as of launching the app, so when we get the first palette, the index is incremented to 0
        currentIndex = -1;

        //sets up button listeners
        buttonPalettes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPaletteAPI();
            }
        });
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPalette();
            }
        });
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousPalette();
            }
        });
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPalette();
            }
        });

        //sets next and previous buttons to be invisible. We don't need em yet.
        buttonPrevious.setVisibility(View.INVISIBLE);
        buttonNext.setVisibility(View.INVISIBLE);
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
                    //this API formats the json as a jsonArray for some reason. No idea why.
                    public void onResponse(JSONArray response) {
                        //initializes variables and sets up Gson
                        String jsonPalette = "";
                        String jsonColors = "";
                        Gson gson = new Gson();
                        //try-catch in case API call fails
                        try {
                            //gets the jsonPalette from the current item in the jsonArray
                            jsonPalette = response.get(0).toString();
                            // implementation 'com.google.code.gson:gson:2.8.6'
                            Palette newPalette = gson.fromJson(jsonPalette, Palette.class);
                            //adds the new palette to the list of palettes
                            palettes.add(newPalette);
                            //sets the current index to the last item in the palette list
                            currentIndex = palettes.size()-1;
                            //checks if we have as least 1 palettes in our list
                            if (currentIndex > 0)
                            {
                                //makes the previous button invisible
                                buttonPrevious.setVisibility(View.VISIBLE);
                            }
                            //makes the next button invisible
                            buttonNext.setVisibility(View.INVISIBLE);
                            //sets up palette view
                            setPaletteView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    //downloads an image of the palette
    private void downloadPalette()
    {
        //protocol must be HTTPS in order to work on Android
        String url = currentPalette.imageUrl.replace("http", "https");
    }

    //Displays the previous palette in the list
    private void previousPalette()
    {
        //Buttons are not disabled, only invisible, so we check to see if there is a previous item in the list of palettes
        if (currentIndex > 0)
        {
            //decrements the current index
            currentIndex--;
            //sets current palette
            currentPalette = palettes.get(currentIndex);
            //makes the next button visible
            buttonNext.setVisibility(View.VISIBLE);
            //checks to see if there is a previous item in the list
            if (currentIndex == 0)
            {
                //makes the previous button invisible if the current palette is the last item in the list
                buttonPrevious.setVisibility(View.INVISIBLE);
            }
            //sets up the palette view
            setPaletteView();
        }
        else
        {
            //makes sure the previous button is invisible in case it's visible for some reason.
            buttonPrevious.setVisibility(View.INVISIBLE);
        }
    }

    //Displays the next palette in the list
    private void nextPalette()
    {
        //Buttons are not disabled, only invisible, so we check to see if there is a next item in the list of palettes
        if (palettes.size() - 1 > currentIndex)
        {
            //increments the current index
            currentIndex++;
            //sets current palette
            currentPalette = palettes.get(currentIndex);
            //makes the previous button visible
            buttonPrevious.setVisibility(View.VISIBLE);
            //checks to see if there is a next item in the list
            if (currentIndex == palettes.size()-1)
            {
                //makes the next button invisible if the current palette is the last item in the list
                buttonNext.setVisibility(View.INVISIBLE);
            }
            //sets up the palette view
            setPaletteView();
        }
        else
        {
            //makes sure the next button is invisible in case it's visible for some reason.
            buttonNext.setVisibility(View.INVISIBLE);
        }
    }

    //Sets up the view using the current palette.
    private void setPaletteView()
    {
        //set the current palette to the current index in the list
        currentPalette = palettes.get(currentIndex);
        //Occasionally, the palette will somehow be set up with fewer than 5 colors.
        //This isn't supposed to happen from the API, but if several palettes are requested in quick succession, something funky happens.
        //In this event, the palette is scrapped and a new one is retrieved.
        try {
            //set the title text
            textViewTitle.setText(currentPalette.title);
            //get the colors in the palette from the current palette
            currentColors = currentPalette.getColors();

            //set the background color of the views
            color1.setBackgroundColor(Color.parseColor("#" + currentColors[0]));
            color2.setBackgroundColor(Color.parseColor("#" + currentColors[1]));
            color3.setBackgroundColor(Color.parseColor("#" + currentColors[2]));
            color4.setBackgroundColor(Color.parseColor("#" + currentColors[3]));
            color5.setBackgroundColor(Color.parseColor("#" + currentColors[4]));
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            //removes palette from list and decrements the index in case something happens. Calls for a new palette to be generated.
            palettes.remove(currentPalette);
            currentIndex--;
            getPaletteAPI();
        }
    }
}