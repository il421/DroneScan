package com.dji.FPVDemo;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONHandler {
    private JSONObject json = null;
    private JSONArray jsonActions = null;
    private String[] movementsArr = {};
    private String operatingHeigh;

    JSONHandler(Context c) {
        if (!PreferenceManager.getDefaultSharedPreferences(c).contains("FlyingJSON")) {
            PreferenceManager.getDefaultSharedPreferences(c).edit().putString("FlyingJSON", "{\"actions\":[],\"speed\":\"2\",\"height\":\"1.6\"}").apply();
        }
        try {
            String jsonText = PreferenceManager.getDefaultSharedPreferences(c).getString("FlyingJSON", null);
            json = new JSONObject(jsonText);
            jsonActions = json.getJSONArray("actions");
            operatingHeigh = "height" + json.getString("height");
        } catch (JSONException e) {
            Log.d("TestJson", "" + e);
        }
        fillMovementArr();
    }

    private void fillMovementArr() {
        for (int i = 0; i < jsonActions.length(); i++) {
            if (i == 0) {
                putNewValueIntoMovementArray("takeoff");
                putNewValueIntoMovementArray(operatingHeigh);
            }
            try {
                if (jsonActions.getJSONObject(i).getString("name").equals("Fly")) {
                    if (jsonActions.getJSONObject(i).getString("direction").indexOf('y') == 0) {
                        String side = getPattern("[r][i][g][h][t]", jsonActions.getJSONObject(i).getString("direction")) ? "R" : "L";
                        putNewValueIntoMovementArray(jsonActions.getJSONObject(i).getString("metOrDeg") + "yaw" + side);
                    } else {
                        putNewValueIntoMovementArray(jsonActions.getJSONObject(i).getString("metOrDeg") + jsonActions.getJSONObject(i).getString("direction"));
                    }
                } else {
                    String side = jsonActions.getJSONObject(i).getString("side").indexOf('l') == 0 ? "L" : "R";
                    String iniHeight = jsonActions.getJSONObject(i).getString("iniHeight");
                    String maxHeight = jsonActions.getJSONObject(i).getString("maxHeight");
                    String width = jsonActions.getJSONObject(i).getString("width");
                    String margin = jsonActions.getJSONObject(i).getString("margin");
                    putNewValueIntoMovementArray("align");
                    putNewValueIntoMovementArray("cornerCloser" + side);
                    putNewValueIntoMovementArray("height" + iniHeight);
                    putNewValueIntoMovementArray(side == "L" ? "right" + margin : "left" + margin);
                    putNewValueIntoMovementArray("scan" + side + width +  "width" + iniHeight + "initialHeight" + maxHeight + "maxHeight");
                    putNewValueIntoMovementArray("height" + iniHeight);
                    putNewValueIntoMovementArray("align");
                    putNewValueIntoMovementArray("cornerBack" + side);
                    if (i != jsonActions.length() - 1) {
                        putNewValueIntoMovementArray(operatingHeigh);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (i == jsonActions.length() - 1) {
                putNewValueIntoMovementArray("land");
            }
        }
    }

    public String[] getMovementsArr() {
        return movementsArr;
    }

    private void putNewValueIntoMovementArray(String str) {
        String[] tempArr = new String[movementsArr.length + 1];
        for (int i = 0; i < movementsArr.length; i++) {
            tempArr[i] = movementsArr[i];
        }
        tempArr[tempArr.length - 1] = str;
        movementsArr = tempArr;
    }

    private boolean getPattern(String pattern, String where) {
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(where);
        if (matcher.find()) {
            return true;
        }
        return false;
    }
}
