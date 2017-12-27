package sunnyday.api.send.cmpp2;

public class CMPPTerminateRespMessage extends CMPPMessage
{
  public CMPPTerminateRespMessage(CMPPHeader header)
  {
    this.header = header;
  }

  protected byte[] getMsgBody() {
    return null;
  }
}