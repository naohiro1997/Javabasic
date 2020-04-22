package app;

import java.io.Serializable;

/**
 * 趣味情報を保持するクラス
 *
 * @author mano
 *
 */
public class Hobby implements Serializable {

	public Hobby() {
		// 何もしない
	}

	/** 趣味 */
	private String hobby;

	/** 趣味カテゴリ */
	private String hobbyCategory;


	public String getHobby() {
		return hobby;
	}


	public void setHobby(String hobby) {
		this.hobby = hobby;
	}


	public String getHobbyCategory() {
		return hobbyCategory;
	}


	public void setHobbyCategory(String hobbyCategory) {
		this.hobbyCategory = hobbyCategory;
	}


	@Override
	public String toString() {
		return "Hobby [hobby=" + hobby + ", hobbyCategory=" + hobbyCategory + "]";
	}

}
