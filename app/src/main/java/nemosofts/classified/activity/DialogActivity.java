package nemosofts.classified.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import java.util.Objects;

import nemosofts.classified.R;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.dialog.DModeDialog;
import nemosofts.classified.dialog.MaintenanceDialog;
import nemosofts.classified.dialog.UpgradeDialog;
import nemosofts.classified.dialog.VpnDialog;

public class DialogActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String from = getIntent().getStringExtra("from");
        switch (Objects.requireNonNull(from)) {
            case Callback.DIALOG_TYPE_UPDATE:
                new UpgradeDialog(this, new UpgradeDialog.UpgradeListener() {
                    @Override
                    public void onCancel() {
                        openMainActivity();
                    }

                    @Override
                    public void onDo() {

                    }
                });
                break;
            case Callback.DIALOG_TYPE_MAINTENANCE:
                new MaintenanceDialog(this);
                break;
            case Callback.DIALOG_TYPE_DEVELOPER:
                new DModeDialog(this);
                break;
            case Callback.DIALOG_TYPE_VPN:
                new VpnDialog(this);
                break;
            default:
                openMainActivity();
                break;
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(DialogActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_dialog;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}