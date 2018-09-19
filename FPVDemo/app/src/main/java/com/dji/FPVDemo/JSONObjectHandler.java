package com.dji.FPVDemo;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class JSONObjectHandler {

    private String filename;
    private String JSONObj;

    public JSONObjectHandler(String file) {
        this.filename = file;
    }

    public void writeBarcodeTypesJSON(ArrayList<Integer> types) {
//        JSONObject object = new JSONObject();
        JSONArray typesObj = new JSONArray();
        for (Integer num: types) {
            try {
                typesObj.put(num);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.JSONObj = typesObj.toString();
        Log.v("JSON String ", "" + this.JSONObj);
    }

    public void writeCameraSettings(ArrayList<Integer> cameraSettings) {
//        JSONObject object = new JSONObject();
        JSONArray typesObj = new JSONArray();
        for (Integer num: cameraSettings) {
            try {
                typesObj.put(num);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.JSONObj = typesObj.toString();
        Log.v("JSON String ", "" + this.JSONObj);
    }

    public void createAndSaveFile(Context context) {
        try {
            FileWriter file = new FileWriter(context.getFilesDir().getPath() + "/" + this.filename);
            file.write(this.JSONObj);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readJsonData(Context context) {
        try {
            File f = new File(context.getFilesDir().getPath() + "/" + this.filename);
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String mResponse = new String(buffer);
            Log.v("READ JSON: ", "" + mResponse);
            return mResponse;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
