package sunnyday.controller.util;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import sunnyday.tools.util.CommonLogFactory;

/**
 * 
 * @author 1111182
 * @version release_20130909
 */
public class ResultUtil {
	private static Logger log = CommonLogFactory.getCommonLog(ResultUtil.class);
	public static <T> List<T> assemble(ResultSet rs, Class<T> bean){
		List<T> result = new ArrayList<T>();
		try{
			int r = rs.getMetaData().getColumnCount();
			while(rs.next()){
				T obj = bean.newInstance();
				for(int i = 1; i <= r; i++){
					switch (rs.getMetaData().getColumnType(i)){	
					case Types.BIGINT: treatAsLong(rs, obj, i); continue;
					case Types.INTEGER:treatAsInt(rs, obj, i); continue;
					//case Types.NUMERIC:treatAsLong(rs, obj, i); continue;
					case Types.NUMERIC:treatAsNumber(rs, obj, i); continue;
					case Types.TINYINT: treatAsInt(rs, obj, i); continue;
					case Types.FLOAT:
					case Types.DOUBLE: treatAsDouble(rs, obj, i); continue;
					case Types.VARCHAR:
					case Types.DATE:
					case Types.TIMESTAMP:
					case Types.TIME:
					default: treatAsString(rs, obj, i); continue;
					}
				}
				
				result.add(obj);
			}
		}catch(Exception e){
			log.error("",e);
		}
		return result;
	}
	public static <T> T assembleOneBean(ResultSet rs, Class<T> bean){
		T result = null;
		try{
			int r = rs.getMetaData().getColumnCount();
				T obj = bean.newInstance();
				for(int i = 1; i <= r; i++){
					switch (rs.getMetaData().getColumnType(i)){	
					
					case Types.BIGINT: treatAsLong(rs, obj, i); continue;
					case Types.INTEGER:
					case Types.TINYINT: treatAsInt(rs, obj, i); continue;
					// oracle常用numeric
					case Types.NUMERIC:treatAsNumber(rs, obj, i); continue;
					case Types.FLOAT:
					case Types.DOUBLE: treatAsDouble(rs, obj, i); continue;
					case Types.VARCHAR:
					case Types.DATE:
					case Types.TIMESTAMP:
					case Types.TIME:
					default: treatAsString(rs, obj, i); continue;
					}
				}
				result = obj;
		}catch(Exception e){
			log.error("",e);
		}
	
		return result;
	}
	
	private static void treatAsDouble(ResultSet rs, Object bean, int i) throws Exception{
		Method method = bean.getClass().getMethod("set" + FirstUpperCase(rs.getMetaData().getColumnLabel(i)), double.class);
		method.invoke(bean, rs.getDouble(i));
	}
	private static void treatAsLong(ResultSet rs, Object bean, int i) throws Exception{
		Method method = bean.getClass().getMethod("set" + FirstUpperCase(rs.getMetaData().getColumnLabel(i)), long.class);
		method.invoke(bean, rs.getLong(i));
	}

	private static void treatAsString(ResultSet rs, Object bean, int i) throws Exception{
		Method method = bean.getClass().getMethod("set" + FirstUpperCase(rs.getMetaData().getColumnLabel(i)), String.class);
		String str = "";
		if (rs.getString(i)!=null) {
			str=rs.getString(i);
		} 
		method.invoke(bean, str);
	}

	private static void treatAsInt(ResultSet rs, Object bean, int i) throws Exception {
		try {
			Method method = bean.getClass().getMethod("set" + FirstUpperCase(rs.getMetaData().getColumnLabel(i)), int.class);
			method.invoke(bean, rs.getInt(i));
		} catch (NoSuchMethodException e) {
			treatAsLong(rs, bean, i);
		}
		
	}

	private static void treatAsNumber(ResultSet rs, Object bean, int i) throws Exception{
		//System.out.println(i+" "+rs.getMetaData().getColumnName(i));
		if("price".equalsIgnoreCase(rs.getMetaData().getColumnName(i))||rs.getMetaData().getPrecision(i)==30){
			treatAsDouble(rs, bean, i);
		} else {
			try {
			Method method = bean.getClass().getMethod("set" + FirstUpperCase(rs.getMetaData().getColumnLabel(i)), int.class);
			method.invoke(bean, rs.getInt(i));
			} catch (NoSuchMethodException e) {
				treatAsLong(rs, bean, i);
			}
		}
	}
	public static String FirstUpperCase(String columnName) {
		// 因为oracle原因需要加上其他都用小写
		//String result = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
		String result = columnName.substring(0, 1).toUpperCase() + columnName.substring(1).toLowerCase();
		return result;
	}
	
}
