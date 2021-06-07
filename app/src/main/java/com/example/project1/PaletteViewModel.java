package com.example.project1;

import java.util.ArrayList;

public class PaletteViewModel {
    //list of palettes we have collected
    ArrayList<Palette> palettes;
    //the currently selected and viewed palette
    Palette currentPalette;
    //the colors on the current palette
    String[] currentColors;
    //the index of the currently selected palette
    int currentIndex;

    public PaletteViewModel()
    {
        //initializes variables
        palettes = new ArrayList<Palette>();
        currentColors = new String[5];
        currentPalette = new Palette();
        //current index is set to -1.
        //We don't have any palettes as of launching the app, so when we get the first palette, the index is incremented to 0
        currentIndex = -1;
    }

    //returns current palette
    public Palette getCurrentPalette()
    {
        return currentPalette;
    }

    //sets the current palette to the next palette in the list
    public Palette nextPalette()
    {
        //checks if there is a next palette
        if (palettes.size()-1 > currentIndex)
        {
            //increments current index
            currentIndex++;
            //sets current palette
            currentPalette = palettes.get(currentIndex);
        }
        return currentPalette;
    }

    //sets the current palette to the previous palette in the list
    public Palette previousPalette()
    {
        //checks if there is a previous palette
        if (currentIndex > 0)
        {
            //decrements the current index
            currentIndex--;
            //sets current palette
            currentPalette = palettes.get(currentIndex);
        }
        return currentPalette;
    }

    //add a new palette to the list
    public void addPalette(Palette palette)
    {
        //adds palette
        palettes.add(palette);
        //sets current index to the newest item in the list
        currentIndex = palettes.size() - 1;
        //sets current palette
        currentPalette = palettes.get(currentIndex);
    }

    //removes last palette in the list and decrements the index
    public void removeLastPalette()
    {
        //removes palette from list
        palettes.remove(palettes.size()-1);
        //decrements the index
        currentIndex--;
    }

    //removes the current palette from the list
    public void deletePalette()
    {
        //removes palette
        palettes.remove(currentPalette);
        //if this is the last palette in the list, decrement the index.
        if (currentIndex >= palettes.size())
        {
            currentIndex--;
        }
        //Kept getting out of bounds errors when the only palette in the list was deleted
        try
        {
            //sets the current palette to the one at the current index
            currentPalette = palettes.get(currentIndex);
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            //sets the current palette to null if the list is empty.
            currentPalette = null;
        }
    }

    //returns true if there is a next item in the list. False if not.
    public boolean isNextPalette()
    {
        return !(currentIndex + 1 >= palettes.size());
    }
    //returns true if there is a previous item in the list. False if not.
    public boolean isPreviousPalette()
    {
        return !(currentIndex <= 0);
    }
}
