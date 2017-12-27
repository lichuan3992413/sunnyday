package sunnyday.controller.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import sunnyday.common.model.BackFileForm;
import sunnyday.common.model.SmsMessage;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.DateUtil;

public class FileUtil {
	private static Logger log = CommonLogFactory.getCommonLog("infoLog");

	@SuppressWarnings("unchecked")
	public static List<SmsMessage> readOneListFromFile(String filePath, boolean is_del) {
		File file = new File(filePath);
		List<SmsMessage> list = new ArrayList<SmsMessage>();
		if (file != null) {
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(file));
				list = (List<SmsMessage>) ois.readObject();
			} catch (Exception e) {
				log.error("readOneListFromFile", e);
			} finally {
				try {
					if (ois != null) {
						ois.close();
					}
				} catch (Exception e) {
					log.error("", e);
				}

				try {
					if (is_del && file != null) {
						boolean isDel = file.delete();
						log.debug("file{} delete {}!", file.getPath(), isDel);
					}
				} catch (Exception e2) {
					log.error("", e2);
				}
			}
		}
		return list;
	}

	public static BackFileForm writeList(String path, List<SmsMessage> list) {
		BackFileForm form = null;
		File file = getFileName(path, list.size());
		buildPathIfNeeded(file);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(list);
			oos.flush();
			form = new BackFileForm(file.getAbsolutePath(), list.size(), "history_data");
		} catch (Exception e) {
			log.error("writeList", e);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e1) {
				log.error("", e1);
			}
		}

		return form;
	}
	
	public static BackFileForm writeList(String path, List<SmsMessage> list,String data) {
		BackFileForm form = null;
		File file = getFileName(path, list.size());
		buildPathIfNeeded(file);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(list);
			oos.flush();
			form = new BackFileForm(file.getAbsolutePath(), list.size(), data);
		} catch (Exception e) {
			log.error("writeList", e);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e1) {
				log.error("", e1);
			}
		}

		return form;
	}

	private static File getFileName(String file_path, int file_size) {
		if (file_path.endsWith("/")) {
			file_path = file_path.substring(0, file_path.length() - 1);
		}
		return new File(file_path + "/" + DateUtil.getFilePrefix() + "_" + file_size + ".sms");
	}

	private static void buildPathIfNeeded(File f) {
		if (f != null) {
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
		}
	}

	public static void writeFile(String fileName, String content) {
		FileWriter writer = null;
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer = new FileWriter(fileName, true);
			writer.write(content);
		} catch (IOException e) {
			log.error("", e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				log.error("", e);
			}
		}
	}

	public static List<String> getFilePathList(String filePath) {
		List<String> list = new ArrayList<String>();
		File root = new File(filePath);
		if (root.exists()) {
			File[] files = root.listFiles();
			for (File f : files) {
				if (f.isFile()) {
					list.add(f.getPath());
				}
			}
		} else {
			root.mkdirs();
		}
		return list;
	}

	/**
	 * 返回指定文件夹中的所有文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static List<File> getFileList(String filePath) {
		List<File> list = new ArrayList<File>();
		File root = new File(filePath);
		if (root.exists()) {
			File[] files = root.listFiles();
			for (File f : files) {
				if (f.isFile()) {
					list.add(f);
				}
			}
		} else {
			root.mkdirs();
		}
		return list;
	}

	public static String getFilePathList(String filePath, String type) {
		File root = new File(filePath);
		if (root.exists()) {
			File[] files = root.listFiles();
			for (File f : files) {
				if (f.isFile()) {
					if (f.getName().endsWith(type)) {
						return f.getPath();
					}
				}
			}
		} else {
			System.err.println(filePath + "不存在该文件夹！！！");
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public static List<SmsMessage> readListFromFile(String filePath, boolean is_del) {
		File file = new File(filePath);
		List<SmsMessage> list = new ArrayList<SmsMessage>();
		if (file != null) {
			ObjectInputStream ois = null;
			FileInputStream fs = null;
			try {
				fs = new FileInputStream(file);
				ois = new ObjectInputStream(fs);
				list = (List<SmsMessage>) ois.readObject();
			} catch (Exception e) {
				log.error("readOneListFromFile", e);
			} finally {
				try {
					if (fs != null) {
						fs.close();
					}
					if (ois != null) {
						ois.close();
					}
				} catch (Exception e) {
					log.error("", e);
				}
				try {
					if (is_del) {
						boolean flag = file.delete();
						if(!flag){
							log.error("delete "+file.getAbsolutePath()+" "+flag);
						}
					}
				} catch (Exception e2) {
				}
			}
		}
		return list;
	}

	public static BackFileForm writeFileList(String path, List<SmsMessage> list) {
		BackFileForm form = null;
		File file = getFileName(path, list.size());
		buildPathIfNeeded(file);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(list);
			oos.flush();
			oos.close();
			form = new BackFileForm(file.getAbsolutePath(), list.size(), "submit_data");
		} catch (Exception e) {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e1) {
				log.error("", e1);
			}
			log.error("path: " + path + "; " + list, e);
		}

		return form;
	}
	
	
	public static int getVersionInt(String fileFullName) {
		int versionInt = 0;
		int indextV = fileFullName.indexOf("_") + 1;
		if (indextV > 1) {
			String versionStr = fileFullName.substring(indextV, fileFullName.indexOf("_") + 7);
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher matcher = pattern.matcher(versionStr);
			if (matcher.matches()) {
				versionInt = Integer.parseInt(versionStr);
			}
		}

		return versionInt;
	}
	
}
