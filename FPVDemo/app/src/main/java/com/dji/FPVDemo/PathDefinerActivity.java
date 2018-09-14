package com.dji.FPVDemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PathDefinerActivity extends AppCompatActivity implements DroneActionDetailsDlgListener{

    private ListView actionsLv;
    private List<String> actionsArray;
    private ArrayAdapter<String> arrayAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.path_definer);
        actionsLv = findViewById(R.id.actions_list);
        actionsArray = new ArrayList<String>();
        fillArrayList();
        populateArrayAdapter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public void openAddAction(View view) {
//        DialogFragment addActionDialog = new DroneActionDialog();
//        addActionDialog.show(this.getSupportFragmentManager(), "tag");

        DialogFragment addActionDialog = new DroneActionOptionsDialog();
        addActionDialog.show(this.getSupportFragmentManager(), DroneCommand.Yaw.toString());

    }

    public void openActionDetails() {
//        DialogFragment addActionDialog = new DroneActionDialog();
//        addActionDialog.show(this.getSupportFragmentManager(), "tag");
    }

    public void fillArrayList(){
        actionsArray.add("3m Right");
        actionsArray.add("3m Forward");
        actionsArray.add("4m Left");
        actionsArray.add("13m Up");
        actionsArray.add("1m Left");
        actionsArray.add("3m Right");
        actionsArray.add("3m Forward");
    }

    public void populateArrayAdapter() {
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                actionsArray );
        actionsLv.setAdapter(arrayAdapter);
    }

//    @Override
    public void onFinishActionDialog(String distance, String direction) {
        actionsArray.add(distance + " " + direction);
        populateArrayAdapter();
//        Toast.makeText(this, "PathDefinerActivity " + distance + " " + direction, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinishActionOptionsDlg(int position) {
//        Bundle args = new Bundle();
//        args.putString("droneCmd", "YAW");
//        getDetailsDlg.setArguments(args);
        DialogFragment getDetailsDlg = new DroneActionDialog();
        Log.v("PathDefinerAct","onFinishActionOptionsDlg " + position);
        switch (position) {
            case 0:
                // TAKEOFF

                break;
            case 1:
                // YAW
                getDetailsDlg.show(this.getSupportFragmentManager(), DroneCommand.Yaw.toString());
                break;
            case 2:
                // MOVE
                getDetailsDlg.show(this.getSupportFragmentManager(), DroneCommand.Move.toString());
                break;
            case 3:
                // SCAN
                getDetailsDlg.show(this.getSupportFragmentManager(), DroneCommand.Scan.toString());
                break;
            case 4:
                // LAND

                break;
            default:
                break;
        }
    }

    @Override
    public void onFinishActionDetailsDlg() {
        // lala
    }

    public void startDroneScan(View view) {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);

        Intent intent = new Intent(this, DroneController.class);
        startActivity(intent);
    }

}
