package sunnyday.api.send.cmpp2;

public class CMPPSubmitRespMessage extends CMPPMessage
{
  public static final int LEN_CMPP_SUBMITRESP = 9;
  private byte[] body = null;

  public CMPPSubmitRespMessage(CMPPHeader header, byte[] body) throws IllegalArgumentException
  {
    this.header = header;
    this.body = body;
    if (body.length != 9)
      throw new IllegalArgumentException("submit response message body: invalid size");
  }

  public String getSeq_no1()
  {
    byte[] tmp = new byte[8];
    System.arraycopy(this.body, 0, tmp, 0, 8);
    return CMPPMessage.byte8ToLong(tmp, 0);
  }

  public int getResult()
  {
    return this.body[8] & 0xFF;
  }

  protected byte[] getMsgBody()
  {
    return this.body;
  }
}