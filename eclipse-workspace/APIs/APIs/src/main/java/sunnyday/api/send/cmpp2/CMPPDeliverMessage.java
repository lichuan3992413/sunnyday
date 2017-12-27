package sunnyday.api.send.cmpp2;

import java.io.UnsupportedEncodingException;

public class CMPPDeliverMessage extends CMPPMessage
{
  public static final int LEN_CMPP_DELIVER = 73;
  private byte[] body = null;
  private String report_desc;
  private int long_msg_id = 0;
  private int long_msg_count = 0;
  private int long_msg_sub_sn = 0;

  public int getLong_msg_id() {
    return this.long_msg_id;
  }

  public void setLong_msg_id(int long_msg_id)
  {
    this.long_msg_id = long_msg_id;
  }

  public int getLong_msg_count()
  {
    return this.long_msg_count;
  }

  public void setLong_msg_count(int long_msg_count)
  {
    this.long_msg_count = long_msg_count;
  }

  public int getLong_msg_sub_sn()
  {
    return this.long_msg_sub_sn;
  }

  public void setLong_msg_sub_sn(int long_msg_sub_sn)
  {
    this.long_msg_sub_sn = long_msg_sub_sn;
  }

  public CMPPDeliverMessage(CMPPHeader header, byte[] body) throws IllegalArgumentException
  {
    this.header = header;
    this.body = body;
  }

  public int isReport()
  {
    return this.body[63];
  }

  public String getReport_no1()
  {
    String result = "";
    if (isReport() == 1) {
      byte[] tmp = new byte[8];
      System.arraycopy(this.body, 65, tmp, 0, 8);
      result = CMPPMessage.byte8ToLong(tmp, 0);
    }
    return result;
  }

  public int getReport_state()
  {
    int result = -100;
    if (isReport() == 1)
      try {
        this.report_desc = new String(this.body, 73, 7, "ISO8859_1");
        if (("DELIVRD".equals(this.report_desc)) || ("ACCEPTD".equals(this.report_desc)))
          result = 0;
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException) {
      }
    return result;
  }

  public String getReport_desc()
  {
    return this.report_desc;
  }

  public void copyMsgID(byte[] dest)
  {
    System.arraycopy(this.body, 0, dest, 0, 8);
  }

  public String getSrcTermid()
  {
    return new String(this.body, 42, 21).trim();
  }

  public String getDestTermid()
  {
    return new String(this.body, 8, 21).trim();
  }

  public String getMsgContent()
  {
    String result = null;
    if (isReport() == 0)
      try {
        int format = getMsgFormat();
        int length = this.body[64] & 0xFF;

        if (format == 0) {
          if (this.body[65] == 5) {
            try {
              this.long_msg_id = this.body[68];
              this.long_msg_count = this.body[69];
              this.long_msg_sub_sn = this.body[70];
              result = new String(this.body, 71, length - 6, "ISO8859-1");
            }
            catch (Exception e) {
              e.printStackTrace();
            }
          }
          else
          {
            result = new String(this.body, 65, length, "ISO8859-1");
          }

        }
        else if (format == 15) {
          if (this.body[65] == 5) {
            try {
              this.long_msg_id = this.body[68];
              this.long_msg_count = this.body[69];
              this.long_msg_sub_sn = this.body[70];
              result = new String(this.body, 71, length - 6, "GBK");
            }
            catch (Exception e) {
              e.printStackTrace();
            }
          }
          else {
            result = new String(this.body, 65, length, "GBK");
          }

        }
        else if (this.body[65] == 5) {
          try {
            this.long_msg_id = this.body[68];
            this.long_msg_count = this.body[69];
            this.long_msg_sub_sn = this.body[70];
            result = new String(this.body, 71, length - 6, "UnicodeBigUnmarked");
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }
        else
          result = new String(this.body, 65, length, "UnicodeBigUnmarked");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
      }
    return result;
  }

  public int getMsgFormat()
  {
    return this.body[41];
  }

  protected byte[] getMsgBody()
  {
    return this.body;
  }
}