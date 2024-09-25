package com.pecs.pecsi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.pecs.pecsi.Permissions.EngineData;

/**
 * Presets object class
 */
@SuppressWarnings("unused")
public class Presets {
    private static final String MAX_PRESET = "max";
    private static final String HIGH_PRESET = "high";
    private static final String MID_PRESET = "mid";
    private static final String LOW_PRESET = "low";
    private static final String VERYLOW_PRESET = "veryLow";
    private static final String ZEROSHARE_POLICY = "zeroShare";

    /**
     * Get the class constants
     * @param c Class name
     * @return List of class constants
     */
    private static List<String> getConsts(Class<?> c) {
        List<String> consts = new ArrayList<>();
        Field[] fields = c.getDeclaredFields();

        for (Field field : fields) {
            try {consts.add((String) field.get(null));}
            catch (IllegalAccessException e) {e.printStackTrace();}
        }

        return consts;
    }

    /**
     * 
     * @return Strings list of preset names
     */
    public static List<String> getList() {return getConsts(Presets.class);}
}
