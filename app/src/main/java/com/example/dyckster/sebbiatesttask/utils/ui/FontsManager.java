package com.example.dyckster.sebbiatesttask.utils.ui;

import android.graphics.Typeface;

import com.example.dyckster.sebbiatesttask.SebbiaTestTaskApplication;

import java.util.ArrayList;
import java.util.HashMap;

public class FontsManager {
	
	public static class CustomFont {
		String name;
		String path;
		
		public CustomFont(String name, String path) {
			this.name = name;
			this.path = path;
		}
	}


    public static final CustomFont ROBOTO = new CustomFont("roboto", "fonts/Roboto-Regular.ttf");
    public static final CustomFont ROBOTO_BOLD = new CustomFont("roboto-bold", "fonts/Roboto-Bold.ttf");
    public static final CustomFont ROBOTO_BOLD_CONDENSED = new CustomFont("roboto-bold-condensed", "fonts/RobotoCondensed-Bold.ttf");
    public static final CustomFont ROBOTO_SLAB 		= new CustomFont("roboto-slab", "fonts/Roboto-Slab.ttf");
    public static final CustomFont ROBOTO_CONDENSED = new CustomFont("roboto-condensed", "fonts/RobotoCondensed-Regular.ttf");
    public static final CustomFont ROBOTO_MEDIUM    = new CustomFont("roboto-medium", "fonts/Roboto-Medium.ttf");
    public static final CustomFont ROBOTO_LIGHT    = new CustomFont("roboto-light", "fonts/Roboto-Light.ttf");
    public static final CustomFont ROBOTO_THIN    = new CustomFont("roboto-thin", "fonts/Roboto-Thin.ttf");

    private static HashMap<CustomFont, Typeface> cachedFonts = new HashMap<CustomFont, Typeface>();
    private static ArrayList<CustomFont> customFonts = new ArrayList<CustomFont>();
    
    static {
        customFonts.add(ROBOTO);
        customFonts.add(ROBOTO_BOLD);
    	customFonts.add(ROBOTO_SLAB);
        customFonts.add(ROBOTO_BOLD_CONDENSED);
        customFonts.add(ROBOTO_CONDENSED);
        customFonts.add(ROBOTO_MEDIUM);
        customFonts.add(ROBOTO_LIGHT);
        customFonts.add(ROBOTO_THIN);
    }
    
    public static Typeface getTypeface(String typefaceName) {
    	CustomFont font = null;
    	for (int i = 0; i < customFonts.size(); ++i) {
    		if (customFonts.get(i).name.equalsIgnoreCase(typefaceName)) {
    			font = customFonts.get(i);
    			break;
    		}
    	}
    	if (font == null) {
    		throw new RuntimeException("Font not found: " + typefaceName);
    	}
    	return getTypeface(font);
    }

    public static Typeface getTypeface(CustomFont typefaceName) {
    	if (!cachedFonts.containsKey(typefaceName))
    		cachedFonts.put(typefaceName, Typeface.createFromAsset(SebbiaTestTaskApplication.getInstance().getAssets(), typefaceName.path));
    	return cachedFonts.get(typefaceName);
    }
    
    public static Typeface getDefaultTypeface() {
        return getTypeface(ROBOTO);
    }
    
}
