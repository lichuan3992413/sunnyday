package sunnyday.api.send.cmpp2;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class CommonBean
{
  private ByteBuffer headerBuffer = ByteBuffer.allocate(12);
  private ByteBuffer bodyBuffer;
  private CMPPHeader cmppHeader;
  private boolean isConnected;
  private int max_speed_per_second;
  private long last_mark_time = 0L;
  private long last_send_time = 0L;
  private long current_time = 0L;
  private String ip;
  private boolean notReceiveSendBindRespMessage = false;
  private long sendBindMessageTimestamp = 0L;
  private long sendCheckTimestamp = 0L;
  private boolean is_send_connect = false;
  private int has_send_count;

  public boolean isNotReceiveSendBindRespMessage()
  {
    return this.notReceiveSendBindRespMessage;
  }

  public void setNotReceiveSendBindRespMessage(boolean notReceiveSendBindRespMessage)
  {
    this.notReceiveSendBindRespMessage = notReceiveSendBindRespMessage;
  }

  public long getSendBindMessageTimestamp() {
    return this.sendBindMessageTimestamp;
  }

  public void setSendBindMessageTimestamp(long sendBindMessageTimestamp) {
    this.sendBindMessageTimestamp = sendBindMessageTimestamp;
  }

  public long getSendCheckTimestamp() {
    return this.sendCheckTimestamp;
  }

  public void setSendCheckTimestamp(long sendCheckTimestamp) {
    this.sendCheckTimestamp = sendCheckTimestamp;
  }

  public int getMax_speed_per_second() {
    return this.max_speed_per_second;
  }

  public void setMax_speed_per_second(int max_speed_per_second) {
    this.max_speed_per_second = max_speed_per_second;
  }

  public long getLast_mark_time() {
    return this.last_mark_time;
  }

  public void setLast_mark_time(long last_mark_time) {
    this.last_mark_time = last_mark_time;
  }

  public long getLast_send_time() {
    return this.last_send_time;
  }

  public void setLast_send_time(long last_send_time) {
    this.last_send_time = last_send_time;
  }

  public long getCurrent_time() {
    return this.current_time;
  }

  public void setCurrent_time(long current_time) {
    this.current_time = current_time;
  }

  public int getHas_send_count() {
    return this.has_send_count;
  }

  public void setHas_send_count(int has_send_count) {
    this.has_send_count = has_send_count;
  }

  public void reset()
  {
    this.headerBuffer.clear();
    this.cmppHeader = null;
    this.bodyBuffer = null;
  }

  public boolean canSend(int sleep_time)
  {
    this.current_time = System.currentTimeMillis();
    if (this.current_time - this.last_send_time >= sleep_time) {
      this.last_send_time = this.current_time;
      return true;
    }
    return false;
  }

  public void send(int count)
  {
    this.has_send_count += count;
  }

  public void read(SocketChannel socketChannel)
    throws Exception
  {
    if ((this.cmppHeader == null) && (this.headerBuffer.hasRemaining()) && (socketChannel.read(this.headerBuffer) == -1)) {
      throw new Exception("invalid packet length");
    }
    if ((!this.headerBuffer.hasRemaining()) && (this.bodyBuffer == null)) {
      this.headerBuffer.flip();
      this.cmppHeader = new CMPPHeader(this.headerBuffer.array());

      if ((this.cmppHeader.getTotal_length() > 65536) || (this.cmppHeader.getTotal_length() < 0)) {
        throw new Exception("invalid packet length");
      }
      this.bodyBuffer = ByteBuffer.allocate(this.cmppHeader.getTotal_length() - 12);
    }

    if ((this.bodyBuffer != null) && (this.bodyBuffer.hasRemaining()) && (socketChannel.read(this.bodyBuffer) == -1))
      throw new Exception("invalid packet length");
  }

  public boolean isReady()
  {
    return (this.bodyBuffer != null) && (!this.bodyBuffer.hasRemaining());
  }

  public ByteBuffer getHeaderBuffer()
  {
    return this.headerBuffer;
  }

  public void setHeaderBuffer(ByteBuffer headerBuffer) {
    this.headerBuffer = headerBuffer;
  }

  public ByteBuffer getBodyBuffer() {
    return this.bodyBuffer;
  }

  public void setBodyBuffer(ByteBuffer bodyBuffer) {
    this.bodyBuffer = bodyBuffer;
  }

  public CMPPHeader getCmppHeader()
  {
    return this.cmppHeader;
  }

  public void setCmppHeader(CMPPHeader cmppHeader) {
    this.cmppHeader = cmppHeader;
  }

  public boolean isConnected() {
    return this.isConnected;
  }

  public void setConnected(boolean isConnected) {
    this.isConnected = isConnected;
  }

  public String getIp() {
    return this.ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public boolean getIs_send_connect() {
    return this.is_send_connect;
  }

  public void setIs_send_connect(boolean is_send_connect) {
    this.is_send_connect = is_send_connect;
  }
}