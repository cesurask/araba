package nemosofts.classified.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Objects;

import nemosofts.classified.R;

public class VerifyDialog {

    public VerifyDialog(Activity activity, String title, String message) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_verify);
        dialog.setCancelable(false);
        TextView tv_title = dialog.findViewById(R.id.tv_dialog_title);
        TextView tv_message = dialog.findViewById(R.id.tv_dialog_message);
        tv_title.setText(title);
        tv_message.setText(message);
        dialog.findViewById(R.id.iv_dialog_close).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.tv_dialog_done).setOnClickListener(view -> dialog.dismiss());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
