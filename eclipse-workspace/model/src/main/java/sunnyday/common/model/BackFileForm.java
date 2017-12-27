package sunnyday.common.model;

import java.io.Serializable;

/**
 * 队列信息回写文件时使用的对象
 * 
 * @author 1307365
 *
 */
public class BackFileForm implements Serializable {
	private static final long serialVersionUID = 1252100834503863314L;

	public BackFileForm() {

	}

	@Override
	public String toString() {
		return "BackFileForm [file_path=" + file_path + ", file_size=" + file_size + ", file_type=" + file_type + "]";
	}

	/**
	 * @param file_path
	 * @param file_size
	 * @param file_type
	 */
	public BackFileForm(String file_path, int file_size, String file_type) {
		this.file_path = file_path;
		this.file_size = file_size;
		this.file_type = file_type;

	}

	/**
	 * 文件存储的路径
	 */
	private String file_path;
	/**
	 * 文件存储的对象个数
	 */
	private int file_size;
	/**
	 * 文件的类型
	 */
	private String file_type;

	public String getFile_path() {
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public int getFile_size() {
		return file_size;
	}

	public void setFile_size(int file_size) {
		this.file_size = file_size;
	}

	public String getFile_type() {
		return file_type;
	}

	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
