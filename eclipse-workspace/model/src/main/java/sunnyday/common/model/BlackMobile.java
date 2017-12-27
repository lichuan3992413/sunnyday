package sunnyday.common.model;

public class BlackMobile {

	private int sn;
	private String mobile;
	private String insert_time;
	private String operator;
	private int level;
	private String td_code;
	private String user_id;
	private int type;
	private int black_type;
	private int black_level;
	
	private String user_name;
	
	//模板id和name
	private String template_id;
	private String template_name;
	
	private String operator_name;
	

	public BlackMobile() {
		super();
	}

	public BlackMobile(String mobile, String operator, int level, int type,
			int black_type, int black_level, String user_id) {
		super();
		this.mobile = mobile;
		this.operator = operator;
		this.level = level;
		this.type = type;
		this.black_type = black_type;
		this.black_level = black_level;
		this.user_id = user_id;
	}

	public void setSn(int sn){
		this.sn=sn;
	}

	public int getSn(){
		return sn;
	}

	public void setMobile(String mobile){
		this.mobile=mobile;
	}

	public String getMobile(){
		return mobile;
	}

	public void setInsert_time(String insert_time){
		this.insert_time=insert_time;
	}

	public String getInsert_time(){
		return insert_time;
	}

	public void setOperator(String operator){
		this.operator=operator;
	}

	public String getOperator(){
		return operator;
	}

	public void setLevel(int level){
		this.level=level;
	}

	public int getLevel(){
		return level;
	}

	public void setTd_code(String td_code){
		this.td_code=td_code;
	}

	public String getTd_code(){
		return td_code;
	}

	public void setUser_id(String user_id){
		this.user_id=user_id;
	}

	public String getUser_id(){
		return user_id;
	}

	public void setType(int type){
		this.type=type;
	}

	public int getType(){
		return type;
	}

	public void setBlack_type(int black_type){
		this.black_type=black_type;
	}

	public int getBlack_type(){
		return black_type;
	}

	public void setBlack_level(int black_level){
		this.black_level=black_level;
	}

	public int getBlack_level(){
		return black_level;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

	public String getTemplate_name() {
		return template_name;
	}

	public void setTemplate_name(String template_name) {
		this.template_name = template_name;
	}
	
}
