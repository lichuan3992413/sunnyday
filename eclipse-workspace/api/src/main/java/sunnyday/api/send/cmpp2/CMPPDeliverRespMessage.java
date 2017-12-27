package sunnyday.api.send.cmpp2;

public class CMPPDeliverRespMessage extends CMPPMessage
{
  public static final int LEN_CMPP_DELIVERRESP = 9;
  private byte[] body = null;

  public CMPPDeliverRespMessage(CMPPDeliverMessage deliver) throws IllegalArgumentException {
    this.header = deliver.getCloneMsgHeader();
    this.header.setTotal_length(21);
    this.header.setCommand_id(-2147483643);
    this.body = new byte[9];
    deliver.copyMsgID(this.body);
    this.body[8] = 0;
  }

  protected byte[] getMsgBody()
  {
    return this.body;
  }
}