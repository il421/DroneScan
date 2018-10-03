package com.dji.FPVDemo;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemAdapter extends BaseAdapter {

    LayoutInflater mInflater;
    JSONObject json;

    public ItemAdapter(Context c, JSONObject j) {
        json = j;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        try {
            return json.getJSONArray("actions").length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Object getItem(int i) {
        try {
            return json.getJSONArray("actions").getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = null;
        TextView directionText, actionText;
        TextView iniHeightText = null, maxHeightText = null, widthText = null, marginText = null;
        ImageButton buttonDelete, buttonEdit;

        try {
            // Get jsonActions object from actions
            JSONObject jsonObj = json.getJSONArray("actions").getJSONObject(i);

            if (jsonObj.get("name").equals("Fly")) {
                v = mInflater.inflate(R.layout.fly_move_list_item, null);
            } else {
                v = mInflater.inflate(R.layout.scan_move_list_item, null);
                iniHeightText = v.findViewById(R.id.iniHeightText);
                maxHeightText = v.findViewById(R.id.maxHeightText);
                widthText = v.findViewById(R.id.widthText);
                marginText = v.findViewById(R.id.marginText);
            }

            // Initialise delete and edit buttons
            buttonEdit = v.findViewById(R.id.buttonEdit);
            buttonEdit.setTag(i);
            buttonDelete = v.findViewById(R.id.buttonDelete);
            buttonDelete.setTag(i);

            directionText = v.findViewById(R.id.directionText);
            actionText = v.findViewById(R.id.actionText);

            // Set click on delete button
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag();
                    try {
                        json.getJSONArray("actions").remove(pos);
                        PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit().putString("FlyingJSON", json.toString()).apply();
                        ItemAdapter.this.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag();
                    EditDialogCreator dialogCreator = new EditDialogCreator(view.getContext(), json, ItemAdapter.this, false, pos);
                    dialogCreator.createDialog();
                }
            });

            // Set name in the 2nd column
            actionText.setText(jsonObj.getString("name"));

            // Check if action is fly it should be in different color and with different UI
            if (jsonObj.get("name").equals("Fly")) {
                // Check if first letter Y, for yaw have degrees symbol
                if (jsonObj.getString("direction").indexOf('y') == 0) {
                    directionText.setText(jsonObj.getString("metOrDeg") + "º " + jsonObj.getString("direction"));
                } else {
                    directionText.setText(jsonObj.getString("metOrDeg") + "m " + jsonObj.getString("direction"));
                }
            } else {
                // Layout if action is scan
                String side = jsonObj.get("side").toString().substring(0, 1).toUpperCase() + jsonObj.get("side").toString().substring(1);
                directionText.setText(side + " side");
                iniHeightText.setText("Initial height: " + jsonObj.getString("iniHeight") + "m");
                maxHeightText.setText("Maximum height: " + jsonObj.getString("maxHeight") + "m");
                widthText.setText("Width: " + jsonObj.getString("width") + "m");
                marginText.setText("Margin: " + jsonObj.getString("margin") + "m");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;
    }
}
