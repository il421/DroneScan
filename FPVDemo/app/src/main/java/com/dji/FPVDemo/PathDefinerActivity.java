package com.dji.FPVDemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class PathDefinerActivity extends AppCompatActivity implements DroneActionDialog.DroneActionDialogListener{

    private ListView actionsLv;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.path_definer);
        actionsLv = findViewById(R.id.actions_list);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public void openAddAction(View view) {
        DialogFragment addActionDialog = new DroneActionDialog();
        addActionDialog.show(this.getSupportFragmentManager(), "tag");

    }

    public void addAction() {

    }

    @Override
    public void onFinishActionDialog(String distance, String direction) {
        Toast.makeText(this, "PathDefinerActivity " + distance + " " + direction, Toast.LENGTH_SHORT).show();
    }

    public void startDroneScan(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
