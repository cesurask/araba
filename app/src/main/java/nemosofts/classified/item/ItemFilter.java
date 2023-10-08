package nemosofts.classified.item;

public class ItemFilter {

    private final String id;
    private final String name;

    public ItemFilter(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
