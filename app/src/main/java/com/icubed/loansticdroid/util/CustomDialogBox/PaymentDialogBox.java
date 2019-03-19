package com.icubed.loansticdroid.util.CustomDialogBox;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.icubed.loansticdroid.R;

public class PaymentDialogBox extends Dialog implements View.OnClickListener {

    private TextView yes, no;
    private OnButtonClick onButtonClick;

    public PaymentDialogBox(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_collect_payment);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(onButtonClick != null) {
            switch (v.getId()) {
                case R.id.yes:
                    onButtonClick.onYesButtonClick();
                    break;
                case R.id.no:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }

    public void setOnYesClicked(OnButtonClick listener) {
        this.onButtonClick = listener;
    }

    public interface OnButtonClick {
        void onYesButtonClick();
    }
}
