package nemosofts.classified.callback;

import java.io.Serializable;
import java.util.ArrayList;

import nemosofts.classified.item.ItemAbout;
import nemosofts.classified.BuildConfig;
import nemosofts.classified.item.ItemGallery;

public class Callback implements Serializable {

    private static final long serialVersionUID = 1L;

    // API URL
    public static String API_URL = BuildConfig.BASE_URL+"api.php";

    // TAG_API
    public static final String TAG_ROOT = BuildConfig.API_NAME;
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MSG = "MSG";

    // Method
    public static final String METHOD_APP_DETAILS = "app_details";

    public static final String METHOD_LOGIN = "user_login";
    public static final String METHOD_REGISTER = "user_register";
    public static final String METHOD_PROFILE = "user_profile";
    public static final String METHOD_ACCOUNT_DELETE = "account_delete";
    public static final String METHOD_EDIT_PROFILE = "edit_profile";
    public static final String METHOD_USER_IMAGES_UPDATE = "user_images_update";
    public static final String METHOD_FORGOT_PASSWORD = "forgot_pass";
    public static final String METHOD_NOTIFICATION = "get_notification";
    public static final String METHOD_REMOVE_NOTIFICATION = "remove_notification";

    public static final String METHOD_REPORT = "post_report";
    public static final String METHOD_GET_RATINGS = "get_rating";
    public static final String METHOD_RATINGS = "post_rating";

    public static final String METHOD_HOME = "get_home";
    public static final String METHOD_FILTER_LIST = "filter_list";
    public static final String METHOD_POST= "filter_post";
    public static final String METHOD_DO_BLOCK = "block_post";
    public static final String METHOD_POST_BY_BLOCKED = "post_blocked";
    public static final String METHOD_SHOP_BY_BLOCKED = "block_shop";
    public static final String METHOD_OTP_VERIFIED = "otp_verified";
    public static final String METHOD_SHARE = "post_share";
    public static final String METHOD_DETAILS = "post_details";
    public static final String METHOD_DO_FAV = "favourite_post";
    public static final String METHOD_POST_BY_FAV = "get_favourite";
    public static final String METHOD_VISIT_STORE = "visit_store";
    public static final String METHOD_PRO_TEST = "promotions_list";
    public static final String METHOD_DATABASE = "database";
    public static final String METHOD_REMOVE_GALLERY_IMAGE = "remove_gallery_img";

    public static final String METHOD_ADD_PRODUCT = "add_product";
    public static final String METHOD_EDIT_UPLOAD_POST = "edit_product";

    public static final String METHOD_PAY_DETAILS = "payment_settings";
    public static final String STRIPE_TOKEN_URL = "stripe_token_get";
    public static final String TRANSACTION_URL = "transaction_add";
    public static final String METHOD_BANK_PAY  = "transaction_bank";
    public static final String METHOD_BANK_DETAILS = "get_bank_details";

    public static final String METHOD_COMMENTS = "get_comments";
    public static final String METHOD_POST_COMMENTS = "user_comment";
    public static final String METHOD_DELETE_COMMENTS = "remove_comment";

    public static final String METHOD_DO_SOLD_OUT = "post_sold_out";
    public static final String METHOD_DO_TRASH = "post_trash";

    public static final String METHOD_SEND_CHAT = "add_chat";
    public static final String METHOD_GET_CHAT = "get_chat";
    public static final String METHOD_CHAT_POST = "get_chat_post";

    public static Boolean isProfileUpdate = false;

    public static final String LOGIN_TYPE_NORMAL = "Normal";
    public static final String LOGIN_TYPE_GOOGLE = "Google";

    // About Details
    public static ItemAbout itemAbout = new ItemAbout("","","","","","");

    public static int recentLimit = 10;

    public static Boolean isBannerAd = true, isInterAd = true, isNativeAd = true;

    public static int adCount = 0;
    public static int interstitialAdShow = 5, nativeAdShow = 6;

    public static String adNetwork = "admob";
    public static String ironAdsId = "";
    public static String startappAppId = "";
    public static String wortiseAppId = "";
    public static String publisherAdID = "";
    public static String bannerAdID = "";
    public static String interstitialAdID = "";
    public static String nativeAdID = "";

    public static final String AD_TYPE_ADMOB = "admob", AD_TYPE_FACEBOOK = "facebook",
            AD_TYPE_STARTAPP = "startapp", AD_TYPE_APPLOVIN = "applovins",
            AD_TYPE_IRONSOURCE = "iron", AD_TYPE_WORTISE = "wortise";

    public static Boolean isCustomAds = false;
    public static int customAdCount = 0, customAdShow = 12;
    public static String custom_ads_img = "", custom_ads_link = "";

    public static Boolean isRTL = false, isVPN = false, isAPK = false, isMaintenance = false,
            isScreenshot = false, isLogin = false, isGoogleLogin = false, isAppLanguage = false, isPurchases = false;

    public static Boolean isAppUpdate = false;
    public static int app_new_version = 1;
    public static String app_update_desc = "", app_redirect_url = "";

    public static final String DIALOG_TYPE_UPDATE = "upgrade", DIALOG_TYPE_MAINTENANCE = "maintenance",
            DIALOG_TYPE_DEVELOPER = "developer", DIALOG_TYPE_VPN = "vpn";


    //
    public static String currencyCode = "";
    public static Boolean payPal = false;
    public static Boolean payPalSandbox = false;
    public static String payPalClientId = "";
    public static Boolean stripe = false;
    public static String stripePublisherKey = "";
    public static Boolean payStack = false;
    public static String payStackPublicKey = "";
    public static Boolean bankPay = false;
    public static String bankName = "";
    public static String bankAccountNumber = "";
    public static String bankAccountDetails = "";

    // Fil
    public static String sort_by= "newest";
    public static String city_id= "";
    public static String city_name= "City";
    public static String cat_id= "";
    public static String cat_name= "Category";
    public static String scat_id= "";
    public static String scat_name= "";
    public static String search_item = "";

    public static String currency_code_promotions = "", currency_code = "";
    public static Boolean currency_position = true;

    public static ArrayList<ItemGallery> arrayList_banner = new ArrayList<>();
}