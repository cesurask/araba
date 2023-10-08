package nemosofts.classified.utils.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.item.ItemAbout;
import nemosofts.classified.item.ItemCategory;
import nemosofts.classified.item.ItemCity;
import nemosofts.classified.item.ItemSubCategory;
import nemosofts.classified.utils.EncryptData;

public class DBHelper extends SQLiteOpenHelper {

    EncryptData encryptData;
    static String DB_NAME = "tbl_db.db";

    SQLiteDatabase db;
    final Context context;

    private static final String TAG_ID = "id";

    // Table Name
    private static final String TABLE_ABOUT = "about";
    public static final String TABLE_CAT = "cat";
    public static final String TABLE_CITY = "city";
    public static final String TABLE_SUB_CAT = "scat";

    private static final String TAG_ABOUT_EMAIL = "email";
    private static final String TAG_ABOUT_AUTHOR = "author";
    private static final String TAG_ABOUT_CONTACT = "contact";
    private static final String TAG_ABOUT_WEBSITE = "website";
    private static final String TAG_ABOUT_DESC = "description";
    private static final String TAG_ABOUT_DEVELOPED = "developed";
    private static final String TAG_ABOUT_PUB_ID = "ad_pub";
    private static final String TAG_ABOUT_START_APP_ID = "start_app_id";
    private static final String TAG_ABOUT_IRON_APP_ID = "iron_app_id";
    private static final String TAG_ABOUT_WORTISE_APP_ID = "wortise_app_id";
    private static final String TAG_ABOUT_BANNER_ID = "ad_banner";
    private static final String TAG_ABOUT_INTER_ID = "ad_inter";
    private static final String TAG_ABOUT_NATIVE_ID = "ad_native";
    private static final String TAG_ABOUT_IS_BANNER = "isbanner";
    private static final String TAG_ABOUT_IS_INTER = "isinter";
    private static final String TAG_ABOUT_IS_NATIVE = "isNative";
    private static final String TAG_ABOUT_CLICK = "click";
    private static final String TAG_ABOUT_IS_LANGUAGE = "islanguage";

    // Category
    private static final String TAG_CAT_ID = "cid";
    private static final String TAG_CAT_NAME = "cname";
    private static final String TAG_CAT_IMAGE = "image";

    // SubCategory
    private static final String TAG_SUB_CAT_ID = "sid";
    private static final String TAG_SUB_CAT_CID = "scid";
    private static final String TAG_SUB_CAT_NAME = "sname";
    private static final String TAG_SUB_CAT_IMAGE = "image";

    // City
    private static final String TAG_CITY_ID = "aid";
    private static final String TAG_CITY_NAME = "aname";

    private final String[] columns_cat = new String[]{TAG_ID, TAG_CAT_ID, TAG_CAT_NAME, TAG_CAT_IMAGE};
    private final String[] columns_sub_cat = new String[]{TAG_ID, TAG_SUB_CAT_ID, TAG_SUB_CAT_CID, TAG_SUB_CAT_NAME, TAG_SUB_CAT_IMAGE};
    private final String[] columns_city = new String[]{TAG_ID, TAG_CITY_ID, TAG_CITY_NAME};
    private final String[] columns_about = new String[]{TAG_ABOUT_EMAIL, TAG_ABOUT_AUTHOR, TAG_ABOUT_CONTACT, TAG_ABOUT_WEBSITE, TAG_ABOUT_DESC, TAG_ABOUT_DEVELOPED,
            TAG_ABOUT_PUB_ID, TAG_ABOUT_START_APP_ID, TAG_ABOUT_IRON_APP_ID, TAG_ABOUT_WORTISE_APP_ID,
            TAG_ABOUT_BANNER_ID, TAG_ABOUT_INTER_ID, TAG_ABOUT_NATIVE_ID, TAG_ABOUT_IS_BANNER, TAG_ABOUT_IS_INTER, TAG_ABOUT_IS_NATIVE,
            TAG_ABOUT_CLICK, TAG_ABOUT_IS_LANGUAGE};

    // Creating table about
    private static final String CREATE_TABLE_ABOUT = "create table " + TABLE_ABOUT + "(" +
            TAG_ABOUT_EMAIL + " TEXT, " +
            TAG_ABOUT_AUTHOR + " TEXT, " +
            TAG_ABOUT_CONTACT + " TEXT, " +
            TAG_ABOUT_WEBSITE + " TEXT, " +
            TAG_ABOUT_DESC + " TEXT, " +
            TAG_ABOUT_DEVELOPED + " TEXT, " +
            TAG_ABOUT_PUB_ID + " TEXT, " +
            TAG_ABOUT_START_APP_ID + " TEXT, " +
            TAG_ABOUT_IRON_APP_ID + " TEXT, " +
            TAG_ABOUT_WORTISE_APP_ID + " TEXT, " +
            TAG_ABOUT_BANNER_ID + " TEXT, " +
            TAG_ABOUT_INTER_ID + " TEXT, " +
            TAG_ABOUT_NATIVE_ID + " TEXT, " +
            TAG_ABOUT_IS_BANNER + " TEXT, " +
            TAG_ABOUT_IS_INTER + " TEXT, " +
            TAG_ABOUT_IS_NATIVE + " TEXT, " +
            TAG_ABOUT_CLICK + " TEXT, " +
            TAG_ABOUT_IS_LANGUAGE + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_CAT = "create table " + TABLE_CAT + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_CAT_ID + " TEXT," +
            TAG_CAT_NAME + " TEXT," +
            TAG_CAT_IMAGE + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_CITY = "create table " + TABLE_CITY + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_CITY_ID + " TEXT," +
            TAG_CITY_NAME + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_SUB_CAT = "create table " + TABLE_SUB_CAT + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_SUB_CAT_ID + " TEXT," +
            TAG_SUB_CAT_CID + " TEXT," +
            TAG_SUB_CAT_NAME + " TEXT," +
            TAG_SUB_CAT_IMAGE + " TEXT);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        encryptData = new EncryptData(context);
        this.context = context;
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_CAT);
            db.execSQL(CREATE_TABLE_CITY);
            db.execSQL(CREATE_TABLE_SUB_CAT);
            db.execSQL(CREATE_TABLE_ABOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Category ------------------------------------------------------------------------------------
    @SuppressLint("Range")
    public ArrayList<ItemCategory> getCategory(String limit) {
        ArrayList<ItemCategory> arrayList = new ArrayList<>();
        try {
            String OrderBY = TAG_ID + " ASC";
            if (limit != null){
                OrderBY = "RANDOM()";
            }
            Cursor cursor = db.query(TABLE_CAT, columns_cat, null, null, null, null, OrderBY, limit);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {

                    String imageBig = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_CAT_IMAGE)));

                    String cid = cursor.getString(cursor.getColumnIndex(TAG_CAT_ID));
                    String cname = cursor.getString(cursor.getColumnIndex(TAG_CAT_NAME));

                    ItemCategory itemCat = new ItemCategory(cid, cname, imageBig);
                    arrayList.add(itemCat);

                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public void addToCategoryList(ItemCategory itemCat) {
        if (itemCat != null){
            String imageBig = encryptData.encrypt(itemCat.getImage().replace(" ", "%20"));

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_CAT_ID, itemCat.getId());
            contentValues.put(TAG_CAT_NAME, itemCat.getName());
            contentValues.put(TAG_CAT_IMAGE, imageBig);
            db.insert(TABLE_CAT, null, contentValues);
        }
    }

    public void removeAllCategory() {
        try {
            db.delete(TABLE_CAT, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SubCategory ---------------------------------------------------------------------------------
    @SuppressLint("Range")
    public ArrayList<ItemSubCategory> getSubCategory(String catID) {
        ArrayList<ItemSubCategory> arrayList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_SUB_CAT, columns_sub_cat, TAG_SUB_CAT_CID + "=" + catID, null, null, null, TAG_ID + " ASC");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                String imageBig = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_SUB_CAT_IMAGE)));

                String id = cursor.getString(cursor.getColumnIndex(TAG_SUB_CAT_ID));
                String cat_id = cursor.getString(cursor.getColumnIndex(TAG_SUB_CAT_CID));
                String name = cursor.getString(cursor.getColumnIndex(TAG_SUB_CAT_NAME));

                ItemSubCategory itemSubCategory = new ItemSubCategory(id,cat_id, name, imageBig);
                arrayList.add(itemSubCategory);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return arrayList;
    }

    public void addToSubCategoryList(ItemSubCategory itemSubCategory) {
        if (itemSubCategory != null){
            String imageBig = encryptData.encrypt(itemSubCategory.getImage().replace(" ", "%20"));

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_SUB_CAT_ID, itemSubCategory.getId());
            contentValues.put(TAG_SUB_CAT_CID, itemSubCategory.getCatID());
            contentValues.put(TAG_SUB_CAT_NAME, itemSubCategory.getName());
            contentValues.put(TAG_SUB_CAT_IMAGE, imageBig);
            db.insert(TABLE_SUB_CAT, null, contentValues);
        }
    }

    public void removeAllSubCategory() {
        try {
            db.delete(TABLE_SUB_CAT, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // City ----------------------------------------------------------------------------------------
    @SuppressLint("Range")
    public ArrayList<ItemCity> getCity() {
        ArrayList<ItemCity> arrayList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_CITY, columns_city, null, null, null, null, TAG_ID + " ASC");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                String id = cursor.getString(cursor.getColumnIndex(TAG_CITY_ID));
                String name = cursor.getString(cursor.getColumnIndex(TAG_CITY_NAME));

                ItemCity itemCity = new ItemCity(id, name);
                arrayList.add(itemCity);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return arrayList;
    }

    public void addToCityList(ItemCity itemCity) {
        if (itemCity != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_CITY_ID, itemCity.getId());
            contentValues.put(TAG_CITY_NAME, itemCity.getName());
            db.insert(TABLE_CITY, null, contentValues);
        }
    }

    public void removeAllCity() {
        try {
            db.delete(TABLE_CITY, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // About ---------------------------------------------------------------------------------------
    public void addtoAbout() {
        try {
            db.delete(TABLE_ABOUT, null, null);

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_ABOUT_EMAIL, Callback.itemAbout.getEmail());
            contentValues.put(TAG_ABOUT_AUTHOR, Callback.itemAbout.getAuthor());
            contentValues.put(TAG_ABOUT_CONTACT, Callback.itemAbout.getContact());
            contentValues.put(TAG_ABOUT_WEBSITE, Callback.itemAbout.getWebsite());
            contentValues.put(TAG_ABOUT_DESC, Callback.itemAbout.getAppDesc());
            contentValues.put(TAG_ABOUT_DEVELOPED, Callback.itemAbout.getDevelopedBY());

            contentValues.put(TAG_ABOUT_PUB_ID, Callback.publisherAdID);
            contentValues.put(TAG_ABOUT_START_APP_ID, Callback.startappAppId);
            contentValues.put(TAG_ABOUT_IRON_APP_ID, Callback.ironAdsId);
            contentValues.put(TAG_ABOUT_WORTISE_APP_ID, Callback.wortiseAppId);

            contentValues.put(TAG_ABOUT_BANNER_ID, Callback.bannerAdID);
            contentValues.put(TAG_ABOUT_INTER_ID, Callback.interstitialAdID);
            contentValues.put(TAG_ABOUT_NATIVE_ID, Callback.nativeAdID);

            contentValues.put(TAG_ABOUT_IS_BANNER, Callback.isBannerAd);
            contentValues.put(TAG_ABOUT_IS_INTER, Callback.isInterAd);
            contentValues.put(TAG_ABOUT_IS_NATIVE, Callback.isNativeAd);

            contentValues.put(TAG_ABOUT_CLICK, Callback.interstitialAdShow);
            contentValues.put(TAG_ABOUT_IS_LANGUAGE, String.valueOf(Callback.isAppLanguage));

            db.insert(TABLE_ABOUT, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    public Boolean getAbout() {
        Cursor c = db.query(TABLE_ABOUT, columns_about, null, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {

                String email = c.getString(c.getColumnIndex(TAG_ABOUT_EMAIL));
                String author = c.getString(c.getColumnIndex(TAG_ABOUT_AUTHOR));
                String contact = c.getString(c.getColumnIndex(TAG_ABOUT_CONTACT));
                String website = c.getString(c.getColumnIndex(TAG_ABOUT_WEBSITE));
                String desc = c.getString(c.getColumnIndex(TAG_ABOUT_DESC));
                String developed = c.getString(c.getColumnIndex(TAG_ABOUT_DEVELOPED));

                Callback.publisherAdID = c.getString(c.getColumnIndex(TAG_ABOUT_PUB_ID));
                Callback.startappAppId = c.getString(c.getColumnIndex(TAG_ABOUT_START_APP_ID));
                Callback.ironAdsId = c.getString(c.getColumnIndex(TAG_ABOUT_IRON_APP_ID));
                Callback.wortiseAppId = c.getString(c.getColumnIndex(TAG_ABOUT_WORTISE_APP_ID));
                Callback.bannerAdID = c.getString(c.getColumnIndex(TAG_ABOUT_BANNER_ID));
                Callback.interstitialAdID = c.getString(c.getColumnIndex(TAG_ABOUT_INTER_ID));
                Callback.nativeAdID = c.getString(c.getColumnIndex(TAG_ABOUT_NATIVE_ID));
                Callback.isBannerAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_BANNER)));
                Callback.isInterAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_INTER)));
                Callback.isNativeAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_NATIVE)));
                Callback.interstitialAdShow = Integer.parseInt(c.getString(c.getColumnIndex(TAG_ABOUT_CLICK)));
                Callback.isAppLanguage = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_LANGUAGE)));

                Callback.itemAbout = new ItemAbout(email, author, contact, website, desc, developed);
            }
            c.close();
            return true;
        } else {
            if (c != null) {
                c.close();
            }
            return false;
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public synchronized void close () {
        if (db != null) {
            db.close();
            super.close();
        }
    }
}