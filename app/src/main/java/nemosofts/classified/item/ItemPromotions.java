package nemosofts.classified.item;

public class ItemPromotions {

    private final String id;

    private final String planName;
    private final String planDetails;
    private final String planDays;
    private final String planDuration;
    private final String planDurationType;
    private final String planPrice;
    private final String days;

    private final String planDays_2;
    private final String planDuration_2;
    private final String planDurationType_2;
    private final String planPrice_2;
    private final String days2;

    private final String planDays_3;
    private final String planDuration_3;
    private final String planDurationType_3;
    private final String planPrice_3;
    private final String days3;

    private final String promote_image;
    private Boolean expandable;

    private Boolean group_1;
    private Boolean group_2;
    private Boolean group_3;

    private String price;
    private String day;

    public ItemPromotions(String id, String planName, String planDetails, String planDays, String planDuration, String planDurationType, String planPrice, String days, String planDays_2, String planDuration_2, String planDurationType_2, String planPrice_2, String days2, String planDays_3, String planDuration_3, String planDurationType_3, String planPrice_3, String days3, String promote_image) {
        this.id = id;
        this.planName = planName;
        this.planDetails = planDetails;
        this.planDays = planDays;
        this.planDuration = planDuration;
        this.planDurationType = planDurationType;
        this.planPrice = planPrice;
        this.days = days;
        this.planDays_2 = planDays_2;
        this.planDuration_2 = planDuration_2;
        this.planDurationType_2 = planDurationType_2;
        this.planPrice_2 = planPrice_2;
        this.days2 = days2;
        this.planDays_3 = planDays_3;
        this.planDuration_3 = planDuration_3;
        this.planDurationType_3 = planDurationType_3;
        this.planPrice_3 = planPrice_3;
        this.days3 = days3;
        this.promote_image = promote_image;
        this.expandable = false;

        this.group_1 = false;
        this.group_2 = false;
        this.group_3 = false;

        this.price = "00.0";
        this.day = "0";

    }

    public String getId() {
        return id;
    }

    public String getPlanName() {
        return planName;
    }

    public String getPlanDetails() {
        return planDetails;
    }

    public String getPlanDays() {
        return planDays;
    }

    public String getPlanDuration() {
        return planDuration;
    }

    public String getPlanDurationType() {
        return planDurationType;
    }

    public String getPlanPrice() {
        return planPrice;
    }

    public String getDays() {
        return days;
    }

    public String getPlanDays_2() {
        return planDays_2;
    }

    public String getPlanDuration_2() {
        return planDuration_2;
    }

    public String getPlanDurationType_2() {
        return planDurationType_2;
    }

    public String getPlanPrice_2() {
        return planPrice_2;
    }

    public String getDays2() {
        return days2;
    }

    public String getPlanDays_3() {
        return planDays_3;
    }

    public String getPlanDuration_3() {
        return planDuration_3;
    }

    public String getPlanDurationType_3() {
        return planDurationType_3;
    }

    public String getPlanPrice_3() {
        return planPrice_3;
    }

    public String getDays3() {
        return days3;
    }

    public String getPromote_image() {
        return promote_image;
    }

    public Boolean isExpandable(){
        return expandable;
    }

    public void setExpandable(Boolean expandable){
        this.expandable = expandable;
    }

    public Boolean isGroup_1(){
        return group_1;
    }

    public void setGroup_1(Boolean group_1){
        this.group_1 = group_1;
    }

    public Boolean isGroup_2(){
        return group_2;
    }

    public void setGroup_2(Boolean group_2){
        this.group_2 = group_2;
    }

    public Boolean isGroup_3(){
        return group_3;
    }

    public void setGroup_3(Boolean group_3){
        this.group_3 = group_3;
    }

    public String isPrice() {
        return price;
    }

    public void setPrice(String price_1) {
        this.price = price_1;
    }

    public String isDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

}
