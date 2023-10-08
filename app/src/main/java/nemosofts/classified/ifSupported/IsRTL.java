package nemosofts.classified.ifSupported;

import android.app.Activity;
import android.view.View;
import android.view.Window;

import nemosofts.classified.callback.Callback;

public class IsRTL {
    public static void ifSupported(Activity mContext) {
        if (Boolean.TRUE.equals(Callback.isRTL)) {
            try {
                Window window = mContext.getWindow();
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
