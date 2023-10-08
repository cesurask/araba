package nemosofts.classified.item;

public class ItemPostDatabase {

    private final String id;
    private final String userId;
    private final String title;
    private final String description;
    private final String money;
    private final String phone1;
    private final String phone2;
    private final String condition;
    private final String dateTime;
    private final String image1;

    private final String cat_id;
    private final String catName;
    private final String sub_cat_id;
    private final String subCatName;
    private final String city_id;
    private final String cityName;
    private final String totalViews;
    private final String totalShare;
    private final Boolean active;
    private boolean isFav;
    private final String sold_out;
    private Boolean expandable;

    public ItemPostDatabase(String id, String userId, String title, String description, String money, String phone1, String phone2, String condition, String dateTime,
                            String image1, String cat_id, String catName, String sub_cat_id, String subCatName, String city_id, String cityName, String totalViews,
                            String totalShare, Boolean active, boolean isFav, String sold_out) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.money = money;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.condition = condition;
        this.dateTime = dateTime;
        this.image1 = image1;
        this.cat_id = cat_id;
        this.catName = catName;
        this.sub_cat_id = sub_cat_id;
        this.subCatName = subCatName;
        this.city_id = city_id;
        this.cityName = cityName;
        this.totalViews = totalViews;
        this.totalShare = totalShare;
        this.active = active;
        this.isFav = isFav;
        this.sold_out = sold_out;
        this.expandable = false;
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

    public String getDescription() {
        return description;
    }

    public String getMoney() {
        return money;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getCondition() {
        return condition;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getImage1() {
        return image1;
    }

    public String getCatId() {
        return cat_id;
    }

    public String getCatName() {
        return catName;
    }

    public String getSubCatId() {
        return sub_cat_id;
    }

    public String getSubCatName() {
        return subCatName;
    }

    public String getCityId() {
        return city_id;
    }

    public String getCityName() {
        return cityName;
    }

    public String getTotalViews() {
        return totalViews;
    }

    public String getTotalShare() {
        return totalShare;
    }

    public Boolean isActive() {
        return active;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public String getSoldOut() {
        return sold_out;
    }

    public Boolean isExpandable(){
        return expandable;
    }
    public void setExpandable(Boolean expandable){
        this.expandable = expandable;
    }
}
