package nemosofts.classified.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Objects;

import nemosofts.classified.R;
import nemosofts.classified.asyncTask.LoadStatus;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.SuccessListener;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;

public class BlockShopDialog {

    private final Helper helper;
    private final SharedPref sharedPref;
    private Dialog dialog;
    private final Activity ctx;
    private final ProgressDialog progressDialog;

    public BlockShopDialog(Activity ctx) {
        this.ctx = ctx;
        helper = new Helper(ctx);
        sharedPref = new SharedPref(ctx);progressDialog = new ProgressDialog(ctx, R.style.ThemeDialog);
        progressDialog.setMessage(ctx.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

    }

    public void showDialog(String shop_id) {
        dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_block_shop);
        dialog.findViewById(R.id.tv_do_exit).setOnClickListener(view -> {
            dismissDialog();
            ctx.finish();
        });
        dialog.findViewById(R.id.tv_do_yes).setOnClickListener(view -> {
            if(sharedPref.isLogged()) {
                loadReportSubmit(shop_id);
            } else {
                helper.clickLogin();
            }
        });
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void loadReportSubmit(String shop_id) {
        if (helper.isNetworkAvailable()) {
            LoadStatus loadFav = new LoadStatus(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String reportSuccess, String message) {
                    if (success.equals("1")) {
                        if (reportSuccess.equals("1")) {
                            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ctx, ctx.getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                    dismissDialog();
                }
            }, helper.getAPIRequest(Callback.METHOD_SHOP_BY_BLOCKED, 0, shop_id, "", "", "", sharedPref.getUserId(), "", "", "", "", "", "", "", null, null));
            loadFav.execute();
        } else {
            Toast.makeText(ctx, ctx.getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
