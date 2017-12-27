package sunnyday.channel.model;

/*
 * 通道资源对象
 */
public class Channel {

	private int id;
	private String name;
	private int status;
	private int type;
	private String port_number;
	private int chs_short_words;
	private int eng_short_words;
	private int chs_long_words;
	private int eng_long_words;
	private int is_gate_add_sign;
	private String chs_sign;
	private String eng_sign;
	private int sign_position;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getPort_number() {
		return port_number;
	}
	public void setPort_number(String port_number) {
		this.port_number = port_number;
	}
	public int getChs_short_words() {
		return chs_short_words;
	}
	public void setChs_short_words(int chs_short_words) {
		this.chs_short_words = chs_short_words;
	}
	public int getEng_short_words() {
		return eng_short_words;
	}
	public void setEng_short_words(int eng_short_words) {
		this.eng_short_words = eng_short_words;
	}
	public int getChs_long_words() {
		return chs_long_words;
	}
	public void setChs_long_words(int chs_long_words) {
		this.chs_long_words = chs_long_words;
	}
	public int getEng_long_words() {
		return eng_long_words;
	}
	public void setEng_long_words(int eng_long_words) {
		this.eng_long_words = eng_long_words;
	}
	public int getIs_gate_add_sign() {
		return is_gate_add_sign;
	}
	public void setIs_gate_add_sign(int is_gate_add_sign) {
		this.is_gate_add_sign = is_gate_add_sign;
	}
	public String getChs_sign() {
		return chs_sign;
	}
	public void setChs_sign(String chs_sign) {
		this.chs_sign = chs_sign;
	}
	public String getEng_sign() {
		return eng_sign;
	}
	public void setEng_sign(String eng_sign) {
		this.eng_sign = eng_sign;
	}
	public int getSign_position() {
		return sign_position;
	}
	public void setSign_position(int sign_position) {
		this.sign_position = sign_position;
	}

}
