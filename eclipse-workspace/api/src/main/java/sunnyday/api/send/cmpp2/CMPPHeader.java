package sunnyday.api.send.cmpp2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import org.slf4j.Logger;

public class CMPPHeader
  implements Cloneable
{
  public static final int LEN_CMPP_HEADER = 12;
  private int total_length;
  private int command_id;
  private int sequence_id;

  public void setTotal_length(int len)
  {
    this.total_length = len;
  }

  public int getTotal_length()
  {
    return this.total_length;
  }

  public void setCommand(int id, int sequence)
  {
    this.command_id = id;
    this.sequence_id = sequence;
  }

  public void setCommand_id(int id)
  {
    this.command_id = id;
  }

  public int getCommand_id()
  {
    return this.command_id;
  }

  public int getSequence_id()
  {
    return this.sequence_id;
  }

  public void setSequence_id(int sequence)
  {
    this.sequence_id = sequence;
  }

  public CMPPHeader(byte[] buffer)
  {
    this.total_length = CMPPMessage.byte4ToInteger(buffer, 0);
    this.command_id = CMPPMessage.byte4ToInteger(buffer, 4);
    this.sequence_id = CMPPMessage.byte4ToInteger(buffer, 8);
  }

  public CMPPHeader()
  {
  }

  public byte[] getMsgHeader()
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream(12);
    out.write(CMPPMessage.integerToByte(this.total_length), 0, 4);
    out.write(CMPPMessage.integerToByte(this.command_id), 0, 4);
    out.write(CMPPMessage.integerToByte(this.sequence_id), 0, 4);
    return out.toByteArray();
  }

  public void readMsgHeader(InputStream in) throws IOException
  {
    byte[] buffer = new byte[12];
    int actual_length = CMPPMessage.read(in, buffer);
    if (12 != actual_length) {
      CMPPMessage.log.debug("head: actual_length=" + actual_length + ", LEN_CMPP_HEADER=" + 12);
      if (actual_length == -1) {
        throw new SocketException("get the end of the inputStream, maybe the connection is broken");
      }
      throw new IOException("can't get actual length of message header from the inputstream:" + actual_length);
    }

    this.total_length = CMPPMessage.byte4ToInteger(buffer, 0);
    this.command_id = CMPPMessage.byte4ToInteger(buffer, 4);
    this.sequence_id = CMPPMessage.byte4ToInteger(buffer, 8);
  }

  protected Object clone()
  {
    Object obj = null;
    try {
      obj = super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return obj;
  }
}