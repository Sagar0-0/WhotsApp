package com.example.android.whotsapp.activities.dailog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.android.whotsapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jsibbold.zoomage.ZoomageView;

public class DialogReviewSendImage {
    private Context context;
    private Dialog dialog;
    private Bitmap bitmap;
    private ZoomageView image;
    private FloatingActionButton btnSend;

    public DialogReviewSendImage(Context context, Bitmap bitmap) {
        this.context = context;
        this.bitmap = bitmap;
        this.dialog = new Dialog(context);
        initialise();
    }

    private void initialise() {
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.activity_review_send_image);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);

        image = dialog.findViewById(R.id.image_view);
        btnSend = dialog.findViewById(R.id.btn_send);

    }

    public void show(final OnCallBack onCallBack) {
        dialog.show();
        image.setImageBitmap(bitmap);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallBack.OnButtonSendClick();
                dialog.dismiss();
            }
        });
    }

    public interface OnCallBack {
        void OnButtonSendClick();
    }
}
