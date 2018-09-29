package com.dji.FPVDemo;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TipDialog extends DialogFragment {

    private static final String TAG = TipDialog.class.getName();
    public String titleParam;
    public String textParam;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tip, container, false);

        TextView getItClose;
        TextView dialogTitle;
        TextView dialogText;

        getItClose = view.findViewById(R.id.dialogClose);
        dialogTitle = view.findViewById(R.id.dialogTipTitle);
        dialogTitle.setText(titleParam);

        dialogText = view.findViewById(R.id.dialogTipText);
        dialogText.setText(textParam);

        getItClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Close dialog");
                getDialog().dismiss();
            }
        });

        return view;
    }
}
