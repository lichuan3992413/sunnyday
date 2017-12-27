package sunnyday.api.send.cmpp2;

import java.io.UnsupportedEncodingException;

public class CMPPSubmitMessage extends CMPPMessage
{
  public static final int LEN_CMPP_SUBMIT = 147;
  private byte[] body = null;

  public CMPPSubmitMessage(String src_termid, String dest_termid, String msg_content, String service_id, String cmpp_user, int msg_format, String chargeTermId)
    throws IllegalArgumentException
  {
    int need_report = 1;
    int msg_level = 1;

    int fee_type = 1;
    int fee_user_type = 2;
    String fee_code = "0000";

    if (!chargeTermId.equals("00000000000")) {
      fee_user_type = 3;
    }

    byte[] msg_bytes = (byte[])null;
    try
    {
      if ((msg_format == 8) || (msg_format == 24))
        msg_bytes = msg_content.getBytes("UnicodeBigUnmarked");
      else if ((msg_format == 0) || (msg_format == 16))
        msg_bytes = msg_content.getBytes("ISO8859_1");
      else {
        msg_bytes = msg_content.getBytes("GBK");
      }

      this.body = new byte[147 + msg_bytes.length];
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException("unsupported encoding");
    }

    this.body[8] = 1;
    this.body[9] = 1;
    this.body[10] = (byte)need_report;
    this.body[11] = (byte)msg_level;

    byte[] tmp = service_id.getBytes();
    System.arraycopy(tmp, 0, this.body, 12, tmp.length);

    this.body[22] = (byte)fee_user_type;
    System.arraycopy(chargeTermId.getBytes(), 0, this.body, 23, chargeTermId.length());
    this.body[44] = 0;
    this.body[45] = 0;

    this.body[46] = (byte)msg_format;

    tmp = cmpp_user.getBytes();
    System.arraycopy(tmp, 0, this.body, 47, tmp.length);

    this.body[53] = 48;
    this.body[54] = (byte)(fee_type + 48);

    tmp = "000000".getBytes();
    System.arraycopy(tmp, 0, this.body, 55, 6);
    tmp = fee_code.getBytes();
    System.arraycopy(tmp, 0, this.body, 61 - tmp.length, tmp.length);

    System.arraycopy(src_termid.getBytes(), 0, this.body, 95, src_termid.length());
    this.body[116] = 1;
    System.arraycopy(dest_termid.getBytes(), 0, this.body, 117, dest_termid.length());

    this.body[''] = (byte)msg_bytes.length;
    System.arraycopy(msg_bytes, 0, this.body, 139, msg_bytes.length);

    this.header = new CMPPHeader();
    this.header.setTotal_length(this.body.length + 12);
    this.header.setCommand(4, CMPPSequence.createSequence());
  }

  public CMPPSubmitMessage(String src_termid, String dest_termid, String msg_content, String service_id, String cmpp_user, int total_count, int current_index, int sub_msg_id, int msg_format, String chargeTermId)
    throws IllegalArgumentException
  {
    int need_report = 1;
    int msg_level = 1;

    int fee_type = 1;
    int fee_user_type = 2;
    String fee_code = "0000";

    if (!chargeTermId.equals("00000000000")) {
      fee_user_type = 3;
    }

    byte[] tp_udhiHead = new byte[6];
    tp_udhiHead[0] = 5;
    tp_udhiHead[1] = 0;
    tp_udhiHead[2] = 3;

    tp_udhiHead[3] = (byte)sub_msg_id;
    tp_udhiHead[4] = (byte)total_count;
    tp_udhiHead[5] = (byte)current_index;

    byte[] msg_bytes = (byte[])null;
    byte[] tmp_msg_content = (byte[])null;
    try {
      if ((msg_format == 8) || (msg_format == 24))
        tmp_msg_content = msg_content.getBytes("UnicodeBigUnmarked");
      else if ((msg_format == 0) || (msg_format == 16))
        tmp_msg_content = msg_content.getBytes("ISO8859_1");
      else {
        tmp_msg_content = msg_content.getBytes("GBK");
      }

      int content_length = tmp_msg_content.length;
      msg_bytes = new byte[tp_udhiHead.length + content_length];
      System.arraycopy(tp_udhiHead, 0, msg_bytes, 0, tp_udhiHead.length);
      System.arraycopy(tmp_msg_content, 0, msg_bytes, 6, tmp_msg_content.length);

      this.body = new byte[147 + msg_bytes.length];
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException("unsupported encoding");
    }

    this.body[8] = (byte)total_count;
    this.body[9] = (byte)current_index;
    this.body[10] = (byte)need_report;
    this.body[11] = (byte)msg_level;

    byte[] tmp = service_id.getBytes();
    System.arraycopy(tmp, 0, this.body, 12, tmp.length);

    this.body[22] = (byte)fee_user_type;
    System.arraycopy(chargeTermId.getBytes(), 0, this.body, 23, chargeTermId.length());
    this.body[44] = 0;
    this.body[45] = 1;

    this.body[46] = (byte)msg_format;

    tmp = cmpp_user.getBytes();
    System.arraycopy(tmp, 0, this.body, 47, tmp.length);

    this.body[53] = 48;
    this.body[54] = (byte)(fee_type + 48);

    tmp = "000000".getBytes();
    System.arraycopy(tmp, 0, this.body, 55, 6);
    tmp = fee_code.getBytes();
    System.arraycopy(tmp, 0, this.body, 61 - tmp.length, tmp.length);

    System.arraycopy(src_termid.getBytes(), 0, this.body, 95, src_termid.length());
    this.body[116] = 1;
    System.arraycopy(dest_termid.getBytes(), 0, this.body, 117, dest_termid.length());

    this.body[''] = (byte)msg_bytes.length;
    System.arraycopy(msg_bytes, 0, this.body, 139, msg_bytes.length);

    this.header = new CMPPHeader();
    this.header.setTotal_length(this.body.length + 12);
    this.header.setCommand(4, CMPPSequence.createSequence());
  }

  protected byte[] getMsgBody()
  {
    return this.body;
  }
}