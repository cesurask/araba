package nemosofts.classified.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApplicationUtil {

    @NonNull
    public static String responsePost(String url, RequestBody requestBody) {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(25000, TimeUnit.MILLISECONDS)
                .writeTimeout(25000, TimeUnit.MILLISECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @NonNull
    public static String toBase64(@NonNull String input) {
        byte[] encodeValue = Base64.encode(input.getBytes(), Base64.DEFAULT);
        return new String(encodeValue);
    }

    public static String format(Number number) {
        if (number != null){
            char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
            long numValue = number.longValue();
            int value = (int) Math.floor(Math.log10(numValue));
            int base = value / 3;
            if (value >= 3 && base < suffix.length) {
                return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
            } else {
                return new DecimalFormat("#,##0").format(numValue);
            }
        } else {
            return String.valueOf(0);
        }
    }

    @NonNull
    public static String readableFileSize(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static int getColumnWidth(int column, int grid_padding, @NonNull Context ctx) {
        Resources r = ctx.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, grid_padding, r.getDisplayMetrics());
        return (int) ((getScreenWidth(ctx) - ((column + 1) * padding)) / column);
    }

    public static int getScreenWidth(@NonNull Context ctx) {
        int columnWidth;
        WindowManager wm = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    @NonNull
    public static String formatCurrency(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(Double.parseDouble(amount));
    }
}
