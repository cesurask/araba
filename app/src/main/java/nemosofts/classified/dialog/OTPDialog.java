package nemosofts.classified.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;

import java.util.Objects;

import nemosofts.classified.R;
import nemosofts.classified.activity.ProfileActivity;

public class OTPDialog {

    private final Dialog dialog;
    private final Activity activity;

    public OTPDialog(Activity activity) {
        this.activity = activity;
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_otp);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.iv_close).setOnClickListener(view -> dismissDialog());
        dialog.findViewById(R.id.tv_try).setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, ProfileActivity.class));
            dismissDialog();
        });
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
            activity.finish();
        }
    }
}
