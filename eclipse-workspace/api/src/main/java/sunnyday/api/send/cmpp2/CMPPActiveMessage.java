package sunnyday.api.send.cmpp2;

public class CMPPActiveMessage extends CMPPMessage
{
  public CMPPActiveMessage()
  {
    this.header = new CMPPHeader();
    this.header.setTotal_length(12);
    this.header.setCommand(8, CMPPSequence.createSequence());
  }

  public CMPPActiveMessage(CMPPHeader header, byte[] body) throws IllegalArgumentException {
    this.header = header;

    header = getCloneMsgHeader();
    header.setTotal_length(12);
    header.setCommand(8, CMPPSequence.createSequence());
  }

  public CMPPActiveMessage(CMPPHeader header)
  {
    this.header = header;
  }

  protected byte[] getMsgBody()
  {
    byte[] body = new byte[0];
    return body;
  }
}