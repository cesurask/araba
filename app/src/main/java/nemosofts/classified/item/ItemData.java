package nemosofts.classified.item;

public class ItemData {

    private final String id;
    private final String userId;
    private final String title;
    private final String catName;
    private final String money;
    private final String dateTime;
    private final String image;
    private final Boolean dailyBumpUp;
    private final Boolean topAd;
    private final String sold_out;

    public ItemData(String id, String userId, String title, String catName, String money, String dateTime, String image, Boolean dailyBumpUp, Boolean topAd, String sold_out) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.catName = catName;
        this.money = money;
        this.dateTime = dateTime;
        this.image = image;
        this.dailyBumpUp = dailyBumpUp;
        this.topAd = topAd;
        this.sold_out = sold_out;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getCatName() {
        return catName;
    }

    public String getMoney() {
        return money;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getImage() {
        return image;
    }

    public Boolean getDailyBumpUp() {
        return dailyBumpUp;
    }

    public Boolean getTopAd() {
        return topAd;
    }

    public String getSoldOut() {
        return sold_out;
    }
}