package sunnyday.controller.util;

import java.sql.SQLException;

public class InsertSQL {
	private String sql;
	private StringBuilder sb;
	private String [] values;
	private int [] tmpValue;
	private boolean hasInsertData = false;
	private String tableName;
	
	public InsertSQL(String sql){
		if(sql == null){
			throw new NullPointerException("sql can be null");
		}else{
			this.sql = sql;
			String[] blocks = sql.split(" ");
			for(int i = 0; i < blocks.length; i++){
				if(blocks[i].equals("into")){
					this.tableName = blocks[i + 1];
					break;
				}
			}
			this.sb = new StringBuilder(sql.subSequence(0, sql.indexOf("values") + 6));
			String tmpStr = new String(sql);
			int i = 0;
			
			tmpStr = tmpStr.substring(tmpStr.indexOf("values") + 6);
			tmpStr = tmpStr.substring(tmpStr.indexOf("(") + 1, tmpStr.lastIndexOf(")"));
			
			values = tmpStr.split(",");
			
			for(String each: values){
				if(each.trim().equals("?")){
					i++;
				}
			}
			tmpValue = new int [i];
			int k = 0;
			for(int j = 0; j < values.length; j++){
				if(values[j].trim().equals("?")){
					tmpValue[k] = j;
					k++;
				}
			}
		}
	}
	
	
	public void setString(int index, String param){
		if(param == null){
			setNull(index);
		}else{
			param = this.replaceSpecialChar(param);
			values[tmpValue[index - 1]] = "'" + param + "'";
		}
	}
	private String replaceSpecialChar(String param) {
		String result = null;
		if(param != null){
			result = param.replace("'", "''");
			result = result.replace("\\", "\\\\");
		}
		return result;
	}
	public void setInt(int index, int param){
		values[tmpValue[index - 1]] = String.valueOf(param);
	}
	public void setLong(int index, long param){
		values[tmpValue[index - 1]] = String.valueOf(param);
	}
	public void setDouble(int index, double param){
		values[tmpValue[index - 1]] = String.valueOf(param);
	}
	public void setObject(int index, Object param){
		if(param != null){
			if(param.getClass().equals(Long.class)){
				setLong(index, (Long)param);
			}else if(param.getClass().equals(Integer.class)){
				setInt(index, (Integer)param);
			}else if(param.getClass().equals(String.class)){
				setString(index, (String)param);
			}else if(param.getClass().equals(Double.class)){
				setDouble(index, (Double)param);
			}else{
				setString(index, param.toString());
			}
		}else{
			setNull(index);
		}
	}
	public void setNull(int index){
		values[tmpValue[index - 1]] = "null";
	}
	
	public void addBatch() throws SQLException{
		validate();
		hasInsertData = true;
		sb.append("(");
		for(String each : values){
			sb.append(each).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("),");
	}
	private void validate() throws SQLException {
		for(int i = 0; i < tmpValue.length; i++){
			if(values[i] == null){	
				throw new SQLException("insert SQL has unknow param for index : " + (i + 1));
			}
		}
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getSql() {
		return sql;
	}
	public String getFinalSql(){
		String result = null;
		if(hasInsertData){
			result = sb.deleteCharAt(sb.length() - 1).toString(); 
		}else{
			result = "delete from " + tableName + " where 1 = 2";
		}
		return result;
	}
	
	public static void main(String [] a) throws Exception{
//		String sql = "insert into (id, name, age, department, insert_time,email) values (?,?,?,?,now(),'dongchen@qq.com')";
		 String sql = "insert into customer_sms_info (sn,customer_sn,customer_id,td_code,dest_terminal_id,msg_id,msg_content,insert_time,update_time,send_status," +
   		"response_status,fail_describe,plate_msg_id,move_flag,code,sub_sn,cell_code,charge_count,priority,price)" +
   		"values(?,475,'hongshu2','HFJ1CBY03SHFB',?,'msg_id'  ,?,now(),now(),1," +
   		"0,'fail_describe','plate_msg_id',0,  ' code',0,'cell_code',1,0,1)";    
		InsertSQL insql = new InsertSQL(sql);
		insql.setObject(1, (Object)new Exception());
//		insql.setString(1, "***mobile***");
//		insql.setInt(2, );
		insql.setString(2, "dongchen_msg_content");
//		insql.setLong(4, 100000000000l);
//		insql.setString(5, "dongchen");
//		insql.addBatch();
//		insql.setInt(3, 7777);
//		insql.addBatch();
		
		System.out.println(insql.getFinalSql());
	}
}
