package sunnyday.controller.util;
/**
 * 加密解密执行返回结果
 * 
 * @author 1307365
 *
 */
public class EncodeResponse {
	
	private boolean success;
	
	@Override
	public String toString() {
		return "EncodeResponse [success=" + success + ", content=" + content
				+ "]";
	}
	/**
	 * 是否进行加解密成功
	 * @return
	 */
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	/**
	 * 加解密成功时返回的时处理后的值
	 * 加解密失败时，返回的是原始值
	 * @return
	 */
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	private String content  ;
}
