package sunnyday.api.send.cmpp2;

import sunnyday.tools.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;
import org.slf4j.Logger;

public abstract class CMPPMessage
{
  public static final int ID_CMPP_CONNECT = 1;
  public static final int ID_CMPP_CONNECT_RESP = -2147483647;
  public static final int ID_CMPP_TERMINATE = 2;
  public static final int ID_CMPP_TERMINATE_RESP = -2147483646;
  public static final int ID_CMPP_SUBMIT = 4;
  public static final int ID_CMPP_SUBMIT_RESP = -2147483644;
  public static final int ID_CMPP_DELIVER = 5;
  public static final int ID_CMPP_DELIVER_RESP = -2147483643;
  public static final int ID_CMPP_ACTIVE = 8;
  public static final int ID_CMPP_ACTIVE_RESP = -2147483640;
  protected CMPPHeader header = null;
  protected static Logger log = CommonLogFactory.getLog(CMPPMessage.class);

  private static String getCommandIDName(int id) {
    switch (id) {
    case 1:
      return "ID_CMPP_CONNECT";
    case -2147483647:
      return "ID_CMPP_CONNECT_RESP";
    case 4:
      return "ID_CMPP_SUBMIT";
    case -2147483644:
      return "ID_CMPP_SUBMIT_RESP";
    case 5:
      return "ID_CMPP_DELIVER";
    case -2147483643:
      return "ID_CMPP_DELIVER_RESP";
    case 8:
      return "ID_CMPP_ACTIVE";
    case -2147483640:
      return "ID_CMPP_ACTIVE_RESP";
    case 2:
      return "ID_CMPP_TERMINATE";
    case -2147483646:
      return "ID_CMPP_TERMINATE_RESP";
    }

    return "NONE";
  }

  public int getCommand_id()
  {
    return this.header.getCommand_id();
  }

  public int getSequence_id()
  {
    return this.header.getSequence_id();
  }

  public void setSequence_id(int sequence)
  {
    this.header.setSequence_id(sequence);
  }

  public CMPPHeader getCloneMsgHeader()
  {
    return (CMPPHeader)this.header.clone();
  }

  protected abstract byte[] getMsgBody();

  public static CMPPMessage create(CMPPHeader header, byte[] buffer)
    throws IOException
  {
    switch (header.getCommand_id())
    {
    case 1:
      return new CMPPConnectMessage(header, buffer);
    case -2147483647:
      return new CMPPConnectRespMessage(header, buffer);
    case 2:
      return new CMPPTerminateMessage(header, buffer);
    case -2147483644:
      return new CMPPSubmitRespMessage(header, buffer);
    case 5:
      return new CMPPDeliverMessage(header, buffer);
    case 8:
      return new CMPPActiveMessage(header, buffer);
    case -2147483640:
      return new CMPPActiveRespMessage(header, buffer);
    }

    return null;
  }

  public ByteBuffer getMessage() throws Exception {
    ByteBuffer messageBuffer = ByteBuffer.allocate(this.header.getTotal_length());
    messageBuffer.put(this.header.getMsgHeader());
    messageBuffer.put(getMsgBody());
    messageBuffer.flip();
    return messageBuffer;
  }

  public static CMPPMessage read(InputStream in)
    throws Exception
  {
    CMPPHeader tmp = new CMPPHeader();
    tmp.readMsgHeader(in);

    byte[] buffer = (byte[])null;
    int body_length = tmp.getTotal_length() - 12;
    if (body_length > 0) {
      if (body_length > 2000) {
        throw new SocketException("the body length overflow: " + body_length);
      }
      buffer = new byte[body_length];
      int actual_length = read(in, buffer);

      if (body_length != actual_length) {
        log.debug("read: body_length=" + body_length + ", actual_length=" + actual_length);
        throw new IOException("can't get actual length of message body from the inputstream");
      }
    }

    if (log.isDebugEnabled()) {
      int id = tmp.getCommand_id();
      if (id != -2147483640) {
        log.debug("get command id(" + getCommandIDName(id) + "): " + tmp.getSequence_id());
      }
    }

    switch (tmp.getCommand_id()) {
    case -2147483644:
      return new CMPPSubmitRespMessage(tmp, buffer);
    case 5:
      return new CMPPDeliverMessage(tmp, buffer);
    case -2147483640:
      return new CMPPActiveRespMessage(tmp, buffer);
    case 8:
      return new CMPPActiveMessage(tmp);
    case -2147483647:
      return new CMPPConnectRespMessage(tmp, buffer);
    case -2147483646:
      return new CMPPTerminateRespMessage(tmp);
    }
    return null;
  }

  public void write(OutputStream out)
    throws Exception
  {
    byte[] tmp_head = this.header.getMsgHeader();
    byte[] tmp_body = getMsgBody();
    int length = tmp_head.length;
    if (tmp_body != null) length += tmp_body.length;

    byte[] message = new byte[length];

    System.arraycopy(tmp_head, 0, message, 0, 12);
    if (tmp_body != null)
    {
      System.arraycopy(tmp_body, 0, message, 12, tmp_body.length);
    }

    if (log.isDebugEnabled()) {
      int id = this.header.getCommand_id();
      if ((id != 8) && (id != -2147483640))
      {
        log.debug("send command id(" + getCommandIDName(id) + "): " + getSequence_id());
      }

    }

    out.write(message, 0, message.length);
    out.flush();
  }

  public static int read(InputStream in, byte[] buffer) throws IOException { int i = 0;
    int actual_length = 0;
    int read_bytes;
    do {
      i++;
      read_bytes = in.read(buffer, actual_length, buffer.length - 
        actual_length);
      actual_length += read_bytes;
    }while ((actual_length < buffer.length) && (read_bytes > 0));

    if (buffer.length != actual_length) {
      log.debug("read: body_length=" + buffer.length + ", actual_length=" + 
        actual_length);
      throw new IOException(
        "can't get actual length of message body from the inputstream");
    }
    return buffer.length;
  }

  public static int byte4ToInteger(byte[] b, int offset)
  {
    return (0xFF & b[offset]) << 24 | (0xFF & b[(offset + 1)]) << 16 | 
      (0xFF & b[(offset + 2)]) << 8 | 0xFF & b[(offset + 3)];
  }

  public static String byte8ToLong(byte[] b, int offset)
  {
    int q = (0xF0 & b[0]) >> 4;
    int w = (0xF & b[0]) << 1 | (0x80 & b[1]) >> 7;
    int e = (0x7C & b[1]) >> 2;
    int r = (0x3 & b[1]) << 4 | (0xF0 & b[2]) >> 4;
    int t = (0xF & b[2]) << 2 | (0xC0 & b[3]) >> 6;
    int y = (0x3F & b[3]) << 16 | (0xFF & b[4]) << 8 | 0xFF & b[5];
    int u = (0xFF & b[6]) << 8 | 0xFF & b[7];
    return q + w + e + r + t + "-" + y + "-" + u;
  }

  public static byte[] integerToByte(int n)
  {
    byte[] b = new byte[4];
    b[0] = (byte)(n >> 24);
    b[1] = (byte)(n >> 16);
    b[2] = (byte)(n >> 8);
    b[3] = (byte)n;
    return b;
  }
}