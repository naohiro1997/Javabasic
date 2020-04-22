package app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 社員情報を保持するクラス
 *
 * @author mano
 *
 */
public class Employee implements Serializable {

	public Employee() {
		super();
	}

	/** 社員ID */
	private String id;

	/** 社員名 */
	private String name;

	/** イメージ */
	private String image;

	/** 住所 */
	private String address;

	/** 誕生日 */
	private String birthYmd;

	/** 出身大学名 */
	private String college;

	/** 専攻 */
	private String major;

	/** 資格 */
	private String license;

	/** 入社年度 */
	private String enterYmd;

	/** フリーコメント */
	private String comment;

	/** 部署 */
	private String department;

	/** コメントリスト */
	private List<String> commnentList = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBirthYmd() {
		return birthYmd;
	}

	public void setBirthYmd(String birthYmd) {
		this.birthYmd = birthYmd;
	}

	public String getCollege() {
		return college;
	}

	public void setCollege(String collegeNm) {
		this.college = collegeNm;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getEnterYmd() {
		return enterYmd;
	}

	public void setEnterYmd(String enterYmd) {
		this.enterYmd = enterYmd;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void addComment(String comment) {
		this.commnentList.add(comment);
	}

	public List<String> getCommnentList() {
		return commnentList;
	}

	public void setCommnentList(List<String> commnentList) {
		this.commnentList = commnentList;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + ", image=" + image + ", address=" + address + ", birthYmd=" + birthYmd + ", college=" + college + ", major=" + major + ", license=" + license
				+ ", enterYmd=" + enterYmd + ", comment=" + comment + ", department=" + department + ", commnentList=" + commnentList + "]";
	}

}
