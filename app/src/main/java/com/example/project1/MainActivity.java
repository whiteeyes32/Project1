package com.example.project1;

import android.app.DownloadManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
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

public class MainActivity extends AppCompatActivity {

    //declare view elements
    TextView textViewTitle;
    TextView textViewUsername;

    Button buttonPalettes;
    Button buttonPrevious;
    Button buttonDownload;
    Button buttonNext;
    Button buttonDelete;

    //blanks views used to display colors
    View color1;
    View color2;
    View color3;
    View color4;
    View color5;

    PaletteViewModel paletteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gets view elements
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewUsername = findViewById(R.id.textViewUsername);

        buttonPalettes = findViewById(R.id.buttonPalettes);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonDownload = findViewById(R.id.buttonDownload);
        buttonNext = findViewById(R.id.buttonNext);
        buttonDelete = findViewById(R.id.buttonDelete);

        color1 = findViewById(R.id.color1);
        color2 = findViewById(R.id.color2);
        color3 = findViewById(R.id.color3);
        color4 = findViewById(R.id.color4);
        color5 = findViewById(R.id.color5);

        paletteViewModel = new PaletteViewModel();

        //sets up button listeners
        buttonPalettes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPaletteAPI();
                buttonNext.setVisibility(View.INVISIBLE);
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
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deletes the current palette
                paletteViewModel.deletePalette();
                //checks if there is a previous palette available. Sets previous button invisible or visible accordingly
                if (paletteViewModel.isPreviousPalette())
                {
                    buttonPrevious.setVisibility(View.VISIBLE);
                }
                else
                {
                    buttonPrevious.setVisibility(View.INVISIBLE);
                }
                //checks if there is a previous palette available. Sets previous button invisible or visible accordingly
                if (paletteViewModel.isNextPalette())
                {
                    buttonNext.setVisibility(View.VISIBLE);
                }
                else
                {
                    buttonNext.setVisibility(View.INVISIBLE);
                }
                //if there are any palettes left, the current palette will not be null
                if (paletteViewModel.getCurrentPalette() != null) {
                    //sets the pallete view to the current palette
                    setPaletteView(paletteViewModel.getCurrentPalette());
                }
                //if it is null, clears the view.
                else
                {
                    clearPaletteView();
                }
            }
        });

        //sets next and previous buttons to be invisible. We don't need em yet.
        buttonPrevious.setVisibility(View.INVISIBLE);
        buttonNext.setVisibility(View.INVISIBLE);
    }

    //Gets a palette from the API and adds it to the PaletteViewModel's list.
    //I would have put this in the PaletteViewModel, but in order to to that, PaletteViewModel had to extend the Context class
    //I tried that. It didn't work. Much simpler and easier to put it here.
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
                            paletteViewModel.addPalette(newPalette);
                            setPaletteView(newPalette);
                            if (paletteViewModel.isPreviousPalette())
                            {
                                buttonPrevious.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        //textViewError.setText("ERROR Response: " + error.toString());
                    }
                });

        // Create a RequestQueue used to send web requests using Volley
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    //downloads an image of the palette
    private void downloadPalette()
    {
        String url ="";
        //protocol must be HTTPS in order to work on Android
        try
        {
            url = paletteViewModel.getCurrentPalette().imageUrl.replace("http", "https");

            //Makes sure we have a valid URL set
            if (url.contains("https")) {
                //following code obtained from this video: https://youtu.be/KAVn5OrY4Lo
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                String title = URLUtil.guessFileName(url, null, null);
                request.setTitle(title);
                request.setDescription("Palette Image Download");
                String cookie = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookie);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_PICTURES, title);

                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);
            }
        }
        catch (NullPointerException ex)
        {
            //Do nothing
        }
    }

    //Displays the previous palette in the list
    private void previousPalette()
    {
        //checks if there is a previous palette
        if (paletteViewModel.isPreviousPalette())
        {
            //sets up palette view with the previous palette
            setPaletteView(paletteViewModel.previousPalette());
            //sets the next button to visible
            buttonNext.setVisibility(View.VISIBLE);
            //checks again if there is a previous palette. If not, sets the previous button to invisible
            if (!paletteViewModel.isPreviousPalette())
            {
                buttonPrevious.setVisibility(View.INVISIBLE);
            }
        }
    }

    //Displays the next palette in the list
    private void nextPalette()
    {
        //checks if there is a next palette
        if (paletteViewModel.isNextPalette())
        {
            //sets up palette view with the next palette
            setPaletteView(paletteViewModel.nextPalette());
            //sets the previous button to visible
            buttonPrevious.setVisibility(View.VISIBLE);
            //checks again if there is a next palette. If not, sets the next button to invisible
            if (!paletteViewModel.isNextPalette())
            {
                buttonNext.setVisibility(View.INVISIBLE);
            }
        }
    }

    //Sets up the view using the current palette.
    private void setPaletteView(Palette palette)
    {
        //set the current palette to the current index in the list
        //paletteViewModel.getCurrentPalette() = palettes.get(currentIndex);
        //Occasionally, the palette will somehow be set up with fewer than 5 colors.
        //This isn't supposed to happen from the API, but if several palettes are requested in quick succession, something funky happens.
        //In this event, the palette is scrapped and a new one is retrieved.
        try {
            //set the title text
            textViewTitle.setText(palette.title);
            //set username text
            textViewUsername.setText(palette.userName);
            //get the colors in the palette from the current palette
            String[] currentColors = palette.getColors();

            //set the background color of the views
            color1.setBackgroundColor(Color.parseColor("#" + currentColors[0]));
            color2.setBackgroundColor(Color.parseColor("#" + currentColors[1]));
            color3.setBackgroundColor(Color.parseColor("#" + currentColors[2]));
            color4.setBackgroundColor(Color.parseColor("#" + currentColors[3]));
            color5.setBackgroundColor(Color.parseColor("#" + currentColors[4]));
        }
        //removes the bad palette from the list and generates a new one
        catch (ArrayIndexOutOfBoundsException ex)
        {
            paletteViewModel.removeLastPalette();
            getPaletteAPI();
        }
    }
    //clears the palette view if there are no palettes.
    private void clearPaletteView()
    {
        //set the title text
        textViewTitle.setText("");
        textViewUsername.setText("");

        //set the background color of the views to black
        color1.setBackgroundColor(Color.parseColor("#000000"));
        color2.setBackgroundColor(Color.parseColor("#000000"));
        color3.setBackgroundColor(Color.parseColor("#000000"));
        color4.setBackgroundColor(Color.parseColor("#000000"));
        color5.setBackgroundColor(Color.parseColor("#000000"));
    }
}