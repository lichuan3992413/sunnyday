package sunnyday.common.model;

import java.io.Serializable;


/**
 * 上行信息对象
 * @author dear
 *
 */
public class DeliverBean implements Serializable{
	/**
	 *  */
	private static final long serialVersionUID = 3944486259837633258L;
	private long sn;
	private String user_id;
	private String sp_number;
	private String mobile;
	private String msg_content;
	private int status;
	private int try_times;
	private long submitTime;
	private int sub_msg_id;
	private int pk_total;
	private int pk_number; 
	private int msg_format;
	private long reveive_time;
	private int send_status;
	private int send_type;
	private String ext_code;
	private String src_spNumner;//收时的上行接入号
    /**
     * 上行内容是否进行了加密
     * 1：加密
     * 0：不加密
     */
	private int is_encode;
	
	public int getIs_encode() {
		return is_encode;
	}
	public void setIs_encode(int is_encode) {
		this.is_encode = is_encode;
	}
	private String content;
	private int long_msg_id;
	private int long_msg_count;
	private int long_msg_sub_sn;
	
	@Deprecated
	public String getContent() {
		if(content==null||"".equals(content)){
			content = msg_content;
		}
		return content;
	}
	@Deprecated
	public void setContent(String content) {
		this.content = content;
		if(content!=null){
			this.msg_content = content;
		}
	}
	
	/**
	 * sub_msg_id
	 * @return
	 */
	@Deprecated
	public int getLong_msg_id() {
		if(long_msg_id==0){
			long_msg_id=sub_msg_id;
		}
		return long_msg_id;
	}
	/**
	 * sub_msg_id
	 * @return
	 */
	@Deprecated
	public void setLong_msg_id(int long_msg_id) {
		this.long_msg_id = long_msg_id;
		if(long_msg_id!=0){
			this.sub_msg_id = long_msg_id;
		}
		
	}
	/**
	 * @Deprecated pk_total
	 * @return
	 */
	@Deprecated
	public int getLong_msg_count() {
		if(long_msg_count==0){
			long_msg_count=pk_total;
		}
		return long_msg_count;
	}
	/**
	 * 
	 * @param pk_total
	 */
	@Deprecated
	public void setLong_msg_count(int long_msg_count) {
		this.long_msg_count = long_msg_count;
		if(long_msg_count!=0){
			this.pk_total = long_msg_count;
		}
		
	}
	/**
	 * pk_number
	 * @return
	 */
	@Deprecated
	public int getLong_msg_sub_sn() {
          if(long_msg_sub_sn==0){
        	  long_msg_sub_sn = pk_number;
		 }
		return long_msg_sub_sn;
	}
	/**
	 * pk_number
	 * @return
	 */
	@Deprecated
	public void setLong_msg_sub_sn(int long_msg_sub_sn) {
		this.long_msg_sub_sn = long_msg_sub_sn;
		if(long_msg_sub_sn!=0){
			this.pk_number = long_msg_sub_sn;
		}
		
	}
	
	@Override
	public String toString() {
		return "DeliverBean [user_id=" + user_id + ", sp_number=" + sp_number
				+ ", mobile=" + mobile + ", msg_content=" + msg_content
				+ ", status=" + status + ", try_times=" + try_times
				+ ", sub_msg_id=" + sub_msg_id + ", pk_total=" + pk_total
				+ ", pk_number=" + pk_number + ", msg_format=" + msg_format
				+ ", send_status=" + send_status + ", send_type=" + send_type
				+ ", src_spNumner=" + src_spNumner + ", content="+content+"]";
	}
	 
	public DeliverBean(String sp_number , String mobile , String msg_content , long sn , int try_times ,int long_msg_count, int long_msg_id , int long_msg_sub_sn,int msg_format){
		
		this.sp_number = sp_number;
		this.mobile = mobile;
		this.msg_content = msg_content;
		this.sn = sn;
		this.try_times = try_times;
		this.pk_total = long_msg_count;
		this.sub_msg_id = long_msg_id;
		this.pk_number = long_msg_sub_sn;
		this.msg_format = msg_format;
		
	}
	public int getSub_msg_id() {
		return sub_msg_id;
	}
	public void setSub_msg_id(int sub_msg_id) {
		this.sub_msg_id = sub_msg_id;
	}
	public int getPk_total() {
		return pk_total;
	}
	public void setPk_total(int pk_total) {
		this.pk_total = pk_total;
	}
	public int getPk_number() {
		return pk_number;
	}
	public void setPk_number(int pk_number) {
		this.pk_number = pk_number;
	}
	public long getReveive_time() {
		return reveive_time;
	}
	public void setReveive_time(long reveive_time) {
		this.reveive_time = reveive_time;
	}
	public DeliverBean(){			
	}
	public int getSend_type() {
		return send_type;
	}
	public void setSend_type(int send_type) {
		this.send_type = send_type;
	}
	public String getSrc_spNumner() {
		return src_spNumner;
	}
	public void setSrc_spNumner(String src_spNumner) {
		this.src_spNumner = src_spNumner;
	}
	public int getMsg_format() {
		return msg_format;
	}
	public void setMsg_format(int msg_format) {
		this.msg_format = msg_format;
	}
	public long getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(long submitTime) {
		this.submitTime = submitTime;
	}


	/**
	 * 状态报告的发送状态
	 * -1:客户不需要；0:初始状态 ；1:redis发送超时；2：状态报告发送超时
	 */
	public int getSend_status() {
		return send_status;
	}
	public void setSend_status(int send_status) {
		this.send_status = send_status;
	}
	public long getSn() {
		return sn;
	}
	public void setSn(long sn) {
		this.sn = sn;
	}
	public String getSp_number() {
		return sp_number;
	}
	public void setSp_number(String spNumber) {
		sp_number = spNumber;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getTry_times() {
		return try_times;
	}
	public void setTry_times(int tryTimes) {
		try_times = tryTimes;
	}
 
	public String getMsg_content() {
		if(msg_content==null){
			msg_content=this.content;
		}
		return msg_content;
	}
	public void setMsg_content(String msg_content) {
		this.msg_content = msg_content;
		if(msg_content!=null){
			this.content = msg_content;
		}
	}
	
	
 
	
	/**
	 * 0未发送
	 * 1成功发送
	 * 2发送失败
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setExt_code(String ext_code) {
		this.ext_code = ext_code;
	}
	public String getExt_code() {
		return ext_code;
	}

	
	
	
	
}
