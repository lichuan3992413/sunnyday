package sunnyday.api.send.cmpp2;

public class CMPPConnectRespMessage extends CMPPMessage
{
  public static final int LEN_CMPP_CONNECTRESP = 18;
  private byte[] body = null;

  public CMPPConnectRespMessage(CMPPHeader header, byte[] body) throws IllegalArgumentException
  {
    this.header = header;
    this.body = body;
    if (body.length != 18)
      throw new IllegalArgumentException("connect response message body: invalid size");
  }

  public int getConnect_status()
  {
    return this.body[0];
  }

  public int getVersion()
  {
    return this.body[17];
  }

  protected byte[] getMsgBody()
  {
    return this.body;
  }
}