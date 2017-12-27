package sunnyday.common.model;

import java.io.Serializable;

/*
 *
 * 类描述：客户信息表 cust_info
 * 创建人：chenxiaoyi
 * 创建时间：2017/9/23 14:46
 * @version V1.0.0.T.1
 * -----------------------------------------
 * 修改记录(迭代更新)：chenxiaoyi-2017/9/23 14:46---(新建)
 *
 */
public class CustInfo  implements Serializable {

    //客户级别0-普通 1-单位
    public static final String CUST_TYPE_PERSONAL = "0";
    public static final String CUST_TYPE_COMPANY = "1";

    //--短信开户状态status[0]
    //‘N’－非注册用户
    public static final String STATUS_NO = "N";
    //‘0’－预开户，未确认；
    public static final String STATUS_NEED = "0";
    //’1’－已确认；
    public static final String STATUS_PASS = "1";
    //’2’－保留
    public static final String STATUS_STAY = "2";
    //‘3’－暂停服务
    public static final String STATUS_STOP = "3";
    //’4’－销户
    public static final String STATUS_CANCEL = "4";


    //--微信开户状态status[1]
    //‘0’ –暂停
    public static final String STATUS_WECHET_NEED = "0";
    //‘1’--开通
    public static final String STATUS_WECHET_PASS = "1";
    /**
     *
     */
    private static final long serialVersionUID = -466058153585132384L;


    //账号
    private String accno;
    //账号种类 1-借记卡 2-信用卡
    private String accType;
    //账号子类
    private String accSubType;
    //是否为支付卡标识
    private String primFlag;
    //客户号
    private String custNo;
    //开户行
    private String issueBank;
    //客户名称
    private String custName;
    //客户简称或是别名
    private String custAlias;
    //客户级别0-普通 1-单位
    private String custType;
    //客户级别 0-普通 1-优先 2-VIP
    private String custClass;
    //证件类别
    private String certType;
    //证件号
    private String certNo;
    //短信服务密码
    private String passwd;
    //生日
    private String birthday;
    //主手机号通讯公司 1-移动 2-联通 3-电信
    private String mobileType;
    //手机
    private String mobile;
    //建档日期
    private String inputTime;
    //有效日期
    private String expireDate;
    //免费有效期
    private String freeExpire;
    //建档方式 0-手工 1-短信 2-批量
    private String inputManner;
    //操作员号
    private String teller;
    //开户状态 N-非注册用户 0-预开户，未确认 1-已确认 2-保留 3-暂停服务 4-销户（0,1,2有效，其他废弃）
    private String status;
    //发送时间
    private String sendTime;
    //资料最后日期
    private String modiTime;
    //计费方式0:免费 1-标准计费方式
    private String feeSuite;
    //开户网点号
    private String mid;
    //通知底额
    private String iPara1;

    @Override
    public String toString() {
        return "CustInfo{" +
                "accno='" + accno + '\'' +
                ", accType='" + accType + '\'' +
                ", accSubType='" + accSubType + '\'' +
                ", primFlag='" + primFlag + '\'' +
                ", custNo='" + custNo + '\'' +
                ", issueBank='" + issueBank + '\'' +
                ", custName='" + custName + '\'' +
                ", custAlias='" + custAlias + '\'' +
                ", custType='" + custType + '\'' +
                ", custClass='" + custClass + '\'' +
                ", certType='" + certType + '\'' +
                ", certNo='" + certNo + '\'' +
                ", passwd='" + passwd + '\'' +
                ", birthday='" + birthday + '\'' +
                ", mobileType='" + mobileType + '\'' +
                ", mobile='" + mobile + '\'' +
                ", inputTime='" + inputTime + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", freeExpire='" + freeExpire + '\'' +
                ", inputManner='" + inputManner + '\'' +
                ", teller='" + teller + '\'' +
                ", status='" + status + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", modiTime='" + modiTime + '\'' +
                ", feeSuite='" + feeSuite + '\'' +
                ", mid='" + mid + '\'' +
                ", iPara1=" + iPara1 +
                '}';
    }

    public static String getCustTypePersonal() {
        return CUST_TYPE_PERSONAL;
    }

    public static String getCustTypeCompany() {
        return CUST_TYPE_COMPANY;
    }

    public static String getStatusNo() {
        return STATUS_NO;
    }

    public static String getStatusNeed() {
        return STATUS_NEED;
    }

    public static String getStatusPass() {
        return STATUS_PASS;
    }

    public static String getStatusStay() {
        return STATUS_STAY;
    }

    public static String getStatusStop() {
        return STATUS_STOP;
    }

    public static String getStatusCancel() {
        return STATUS_CANCEL;
    }

    public static String getStatusWechetNeed() {
        return STATUS_WECHET_NEED;
    }

    public static String getStatusWechetPass() {
        return STATUS_WECHET_PASS;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAccno() {
        return accno;
    }

    public void setAccno(String accno) {
        this.accno = accno;
    }

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public String getAccSubType() {
        return accSubType;
    }

    public void setAccSubType(String accSubType) {
        this.accSubType = accSubType;
    }

    public String getPrimFlag() {
        return primFlag;
    }

    public void setPrimFlag(String primFlag) {
        this.primFlag = primFlag;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getIssueBank() {
        return issueBank;
    }

    public void setIssueBank(String issueBank) {
        this.issueBank = issueBank;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustAlias() {
        return custAlias;
    }

    public void setCustAlias(String custAlias) {
        this.custAlias = custAlias;
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType;
    }

    public String getCustClass() {
        return custClass;
    }

    public void setCustClass(String custClass) {
        this.custClass = custClass;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getMobileType() {
        return mobileType;
    }

    public void setMobileType(String mobileType) {
        this.mobileType = mobileType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getInputTime() {
        return inputTime;
    }

    public void setInputTime(String inputTime) {
        this.inputTime = inputTime;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getFreeExpire() {
        return freeExpire;
    }

    public void setFreeExpire(String freeExpire) {
        this.freeExpire = freeExpire;
    }

    public String getInputManner() {
        return inputManner;
    }

    public void setInputManner(String inputManner) {
        this.inputManner = inputManner;
    }

    public String getTeller() {
        return teller;
    }

    public void setTeller(String teller) {
        this.teller = teller;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getModiTime() {
        return modiTime;
    }

    public void setModiTime(String modiTime) {
        this.modiTime = modiTime;
    }

    public String getFeeSuite() {
        return feeSuite;
    }

    public void setFeeSuite(String feeSuite) {
        this.feeSuite = feeSuite;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getIPara1() {
        return iPara1;
    }

    public void setIPara1(String iPara1) {
        this.iPara1 = iPara1;
    }
}
