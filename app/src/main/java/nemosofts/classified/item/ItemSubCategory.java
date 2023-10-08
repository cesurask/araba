package nemosofts.classified.item;

public class ItemSubCategory {

    private final String id;
    private final String cat_id;
    private final String name;
    private final String image;

    public ItemSubCategory(String id, String cat_id, String name, String image) {
        this.id = id;
        this.cat_id = cat_id;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getCatID() {
        return cat_id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
