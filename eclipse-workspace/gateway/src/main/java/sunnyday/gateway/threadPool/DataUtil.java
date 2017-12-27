package sunnyday.gateway.threadPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DataUtil {
	private static Log log = LogFactory.getLog(DataUtil.class);

	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String getDateString(long time) {
		return df.format(time);
	}

	public static String getCurrentDateString(String style) {
		return new SimpleDateFormat(style).format(new Date());
	}

	/**
	 * 积压文件
	 * @return
	 */
	public static synchronized String getFileName() {
		return System.currentTimeMillis() + "_" + (int) (Math.random() * 10000)+ ".sms";
	}

	/**
	 * 计费失败文件
	 * @return
	 */
	public static synchronized String getChangeFailFileName() {
		return System.currentTimeMillis() + "=" + (int) (Math.random() * 10000)+ ".sms";
	}

	public static boolean writeOneListToFile(String filePath, List<?> list) {

		File file = new File(filePath);

		if (file != null && list != null) {
			ObjectOutputStream oos = null;
			FileOutputStream fs = null;
			try {
				file.getParentFile().mkdirs();
				fs = new FileOutputStream(file);
				oos = new ObjectOutputStream(fs);
				oos.writeObject(list);

				return true;

			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log.error("", e);
				}
			} finally {
				try {
					if(fs!=null){
						fs.close();
					}
				} catch (Exception e2) {
				}
				try {
					if(oos!=null){
						oos.flush();
					}
					
				} catch (Exception e) {
				}

				try {
					if(oos!=null){
						oos.close();
					}
				} catch (Exception e) {
				}

			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static List<?> readOneListFromFile(String filePath) {

		File file = new File(filePath);
		List<Object> list = new ArrayList<Object>();
		if (file != null) {
			ObjectInputStream ois = null;
			FileInputStream fs = null;

			try {
				fs = new FileInputStream(file);
				ois = new ObjectInputStream(fs);
				list = (List<Object>) ois.readObject();
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log.error("", e);
				}
			} finally {
				try {
					if(fs!=null){
						fs.close();
					}
				} catch (Exception e2) {
				}
				try {
					if(ois!=null){
						ois.close();
					}
				} catch (Exception e) {
				}
			}
		}
		return list;
	}

}
