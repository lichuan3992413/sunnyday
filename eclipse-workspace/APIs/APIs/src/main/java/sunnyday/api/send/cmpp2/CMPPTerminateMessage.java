package sunnyday.api.send.cmpp2;

public class CMPPTerminateMessage extends CMPPMessage
{
  public CMPPTerminateMessage()
  {
    this.header = new CMPPHeader();
    this.header.setTotal_length(12);
    this.header.setCommand(2, CMPPSequence.createSequence());
  }

  public CMPPTerminateMessage(CMPPHeader header, byte[] body) throws IllegalArgumentException {
    this.header = header;

    header = getCloneMsgHeader();
    header.setTotal_length(12);
    header.setCommand(2, CMPPSequence.createSequence());
  }

  protected byte[] getMsgBody()
  {
    return null;
  }
}