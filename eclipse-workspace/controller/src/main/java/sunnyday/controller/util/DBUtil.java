package sunnyday.controller.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import sunnyday.tools.util.CommonLogFactory;

public class DBUtil {
	private static Logger log = CommonLogFactory.getCommonLog(ResultUtil.class);
	public static void freeConnection(Connection conn) {
		if(null!=conn){
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("",e);
			}
		}
	}

	public static void freeConnection(Connection conn,Statement st) {
		close(st);
		freeConnection(conn);
	}

	public static void freeConnection(Connection conn,Statement st,ResultSet rs) {
		close(rs);
		close(st);
		freeConnection(conn);
	}
	
	public static void close(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception e) {
			log.error("",e);
		}
	}

	public static void close(Statement st) {
		try {
			if (st != null)
				st.close();
		} catch (Exception e) {
			log.error("",e);
		}
	}
	
	public static <T> String getInsertSQL(List<T> objList, String tablename) throws Exception {

		if (tablename == null || tablename.equals("")) {
			throw new Exception("表名缺失");
		}
		
		String sql = "insert into " + tablename + " (";
	    StringBuilder param = new StringBuilder();
	    StringBuilder value = new StringBuilder();
	    
		for(int i = 0; i < objList.size(); i++){
			Object obj = objList.get(i);
			Map<String, Object> map = getMapByPOJO(obj);
			value.append("(");
			if(map!=null){
				   for(String key : map.keySet()){
				    	if(i == 0){
				    		param.append(key).append(",");
				    	}
				    	String type = obj.getClass().getDeclaredField(key).getType().getName();
				    	if(type.equals("java.lang.String")){
				    		value.append("'").append(map.get(key)).append("',");
				    	}else{
				    		value.append( map.get(key)).append(",");
				    	}
					}
			}
		    value.deleteCharAt(value.length() - 1);
		    value.append("),");
		}
		value.deleteCharAt(value.length() - 1);
		param.deleteCharAt(param.length() - 1);
	    sql += param + ") values " +  value;
	    
		return sql;
	}
	
	/**
	 * POJO转Map
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMapByPOJO(Object obj) {

		if (obj == null)
			return null;

		if ((obj instanceof Map))
			return ((Map<String, Object>) obj);

		Map<String, Object> map = new HashMap<String, Object>();

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass(), Object.class);
			// 获取属性数组
			PropertyDescriptor[] proDescArray = beanInfo.getPropertyDescriptors();

			if (proDescArray != null && proDescArray.length > 0) {
				for (PropertyDescriptor propDesc : proDescArray) {
					//获取属性类型
					//Class<?> proType = propDesc.getPropertyType();
					if (propDesc.getReadMethod() != null) {
						//获取属性值
						Object proValue = propDesc.getReadMethod().invoke(obj);
						if (proValue == null) {
							map.put(propDesc.getName().toLowerCase(), "");
						} else{
							map.put(propDesc.getName().toLowerCase(), proValue.toString());
						}
					}
				}
			}
			return map;
		} catch (Exception e) {
			log.error("",e);
		}
		return null;
	}
}
