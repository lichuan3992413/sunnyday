package sunnyday.api.send.cmpp2;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CMPPConnectMessage extends CMPPMessage
{
  public static final int LEN_CMPP_CONNECT = 27;
  private String enter_code = null;
  private String passwd = null;
  private int version;

  public CMPPConnectMessage(String enter_code, String passwd, int version)
  {
    this.enter_code = enter_code;
    this.passwd = passwd;
    this.version = version;
    this.header = new CMPPHeader();
    this.header.setTotal_length(39);
    this.header.setCommand(1, CMPPSequence.createSequence());
  }

  public CMPPConnectMessage(CMPPHeader header, byte[] body) {
    this.header = header;
    if (body.length != 27)
      throw new IllegalArgumentException(
        "bind message body: invalid size");
  }

  protected byte[] getMsgBody()
  {
    byte[] buffer = new byte[27];

    byte[] spid = this.enter_code.getBytes();
    byte[] pass = this.passwd.getBytes();
    String timestamp = new SimpleDateFormat("MMddHHmmss").format(new Date());
    int length = spid.length + pass.length + 19;
    byte[] auth = new byte[length];
    int position = spid.length;
    System.arraycopy(spid, 0, auth, 0, position);
    position += 9;
    System.arraycopy(pass, 0, auth, position, pass.length);
    position += pass.length;
    System.arraycopy(timestamp.getBytes(), 0, auth, position, 10);
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(auth);
      md5.digest(buffer, 6, 16);
    }
    catch (Exception localException) {
    }
    System.arraycopy(spid, 0, buffer, 0, spid.length);

    buffer[22] = (byte)this.version;

    System.arraycopy(integerToByte(Integer.parseInt(timestamp)), 0, buffer, 23, 4);

    return buffer;
  }
}