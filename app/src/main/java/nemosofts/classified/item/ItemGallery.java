package nemosofts.classified.item;

import java.io.File;

public class ItemGallery {

	private String id, image;
	private File file;

	public ItemGallery(String id, String image) {
		this.id = id;
		this.image = image;
	}

	public ItemGallery(String image) {
		this.image = image;
	}

	public ItemGallery(File file) {
		this.file = file;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public File getFile() {
		return file;
	}
}