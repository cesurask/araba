package nemosofts.classified.item;

public class ItemSpotlight {

    private final String id;
    private final String userID;
    private final String title;
    private final String money;
    private final String cityName;
    private final String dateTime;
    private final String image1;
    private final String image2;
    private final String image3;

    public ItemSpotlight(String id, String userID, String title, String money, String cityName, String dateTime, String image1, String image2, String image3) {
        this.id = id;
        this.userID = userID;
        this.title = title;
        this.money = money;
        this.cityName = cityName;
        this.dateTime = dateTime;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userID;
    }

    public String getTitle() {
        return title;
    }

    public String getMoney() {
        return money;
    }

    public String getCityName() {
        return cityName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getImage1() {
        return image1;
    }

    public String getImage2() {
        return image2;
    }

    public String getImage3() {
        return image3;
    }

}
