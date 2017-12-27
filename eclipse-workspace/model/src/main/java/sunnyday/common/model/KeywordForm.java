package sunnyday.common.model;

import java.io.Serializable;

public class KeywordForm implements Serializable{
 
	private static final long serialVersionUID = -504140688576270187L;
	private String td_code;
	private int level;
	private String word;
	
	public String getTd_code() {
		return td_code;
	}
	public void setTd_code(String td_code) {
		this.td_code = td_code;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	
}
