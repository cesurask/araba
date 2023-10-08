package nemosofts.classified.item;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemPostHome implements Serializable {

	String id, title, type, page;
	ArrayList<ItemSpotlight> arrayListSpotlight = new ArrayList<>();
	ArrayList<ItemCategory> arrayListCategories = new ArrayList<>();
	ArrayList<ItemData> arrayListPost = new ArrayList<>();

	public ItemPostHome(String id, String title, String type, String page) {
		this.id = id;
		this.type = type;
		this.title = title;
		this.page = page;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getType() {
		return type;
	}

	public String getSections() {
		return page;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<ItemSpotlight> getArrayListSpotlight() {
		return arrayListSpotlight;
	}
	public void setArrayListSpotlight(ArrayList<ItemSpotlight> arrayListSpotlight) {
		this.arrayListSpotlight = arrayListSpotlight;
	}

	public ArrayList<ItemCategory> getArrayListCategories() {
		return arrayListCategories;
	}
	public void setArrayListCategories(ArrayList<ItemCategory> arrayListCategories) {
		this.arrayListCategories = arrayListCategories;
	}

	public ArrayList<ItemData> getArrayListPost() {
		return arrayListPost;
	}
	public void setArrayListPost(ArrayList<ItemData> arrayListPost) {
		this.arrayListPost = arrayListPost;
	}
}