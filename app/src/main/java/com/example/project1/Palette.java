package com.example.project1;

public class Palette {
    // Instance variables are public so GSON can store the values
    // directly in them.
    public String id;
    public String title;
    public String imageUrl;
    public String userName;
    public String[] colors;

    public Palette()
    {
        id = "0";
        title = "";
        imageUrl = "";
        userName = "";
        colors = new String[]{ "000000", "0000000", "0000000", "0000000", "0000000"};
    }

    public String getTitle()
    {
        return title;
    }
    public String getImageURL() { return imageUrl; }

    @Override
    public String toString() {
        return title;
    }

    public String[] getColors()
    {
        return colors;
    }

    public String getOneColor(int i)
    {
        return colors[i];
    }
}
