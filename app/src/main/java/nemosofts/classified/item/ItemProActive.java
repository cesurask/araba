package nemosofts.classified.item;

public class ItemProActive {

    private final Boolean dailyBumpUp;
    private final Boolean topAd;
    private final Boolean spotLight;

    public ItemProActive(Boolean dailyBumpUp, Boolean topAd, Boolean spotLight) {
        this.dailyBumpUp = dailyBumpUp;
        this.topAd = topAd;
        this.spotLight = spotLight;
    }

    public Boolean isDailyBumpUp() {
        return dailyBumpUp;
    }

    public Boolean isSpotLight() {
        return spotLight;
    }

    public Boolean isTopAd() {
        return topAd;
    }
}
