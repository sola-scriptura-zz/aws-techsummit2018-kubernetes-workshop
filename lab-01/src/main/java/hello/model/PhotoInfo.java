package hello.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class PhotoInfo {
	
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
  
  private Integer img_id;
  private String locale;
  private String imgInfo;
  
  public Integer getId() {
	return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getImg_id() {
		return img_id;
	}
	public void setImg_id(Integer img_id) {
		this.img_id = img_id;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getImgInfo() {
		return imgInfo;
	}
	public void setImgInfo(String imgInfo) {
		this.imgInfo = imgInfo;
	}

    
}
