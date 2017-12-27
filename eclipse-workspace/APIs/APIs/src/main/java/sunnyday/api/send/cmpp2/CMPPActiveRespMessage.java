package sunnyday.api.send.cmpp2;

public class CMPPActiveRespMessage extends CMPPMessage
{
  public static final int LEN_CMPP_ACTIVERESP = 1;
  private byte[] body = null;

  public CMPPActiveRespMessage(CMPPHeader header, byte[] body)
  {
    this.header = header;
    this.body = body;
  }

  public CMPPActiveRespMessage(CMPPActiveMessage active)
  {
    this.header = active.getCloneMsgHeader();
    this.header.setTotal_length(13);
    this.header.setCommand_id(-2147483640);
    this.body = new byte[1];
    this.body[0] = 0;
  }

  protected byte[] getMsgBody()
  {
    return this.body;
  }
}