package sunnyday.common.model;

import java.io.Serializable;

public class ChannelRoute  extends BaseBean implements Serializable {
	private static final long serialVersionUID = 6857489284685029633L;
	private int sn;
	private String node_name;// 所处节点名称,
	private String td_code;// 通道代码,
	private int action;// 0发送线程开启状态；1发送线程关闭状态,
	private int status;// 0启用；1废弃,
	private String operator;// 操作者,
	private String insert_time;// 插入时间,
	private String update_time;// 修改时间,
	
	

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public String getNode_name() {
		return node_name;
	}

	public void setNode_name(String node_name) {
		this.node_name = node_name;
	}

	public String getTd_code() {
		return td_code;
	}

	public void setTd_code(String td_code) {
		this.td_code = td_code;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}
	
	public void setAction(String action) {
		int tmp =0 ;
		try {
			tmp=Integer.parseInt(action);
		} catch (Exception e) {
		}
		this.action = tmp;
	}


	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	public void setStatus(String status) {
		int tmp =0 ;
		try {
			tmp=Integer.parseInt(status);
		} catch (Exception e) {
		}
		this.status = tmp;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getInsert_time() {
		return insert_time;
	}

	public void setInsert_time(String insert_time) {
		this.insert_time = insert_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

}
