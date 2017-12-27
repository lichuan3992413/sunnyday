package sunnyday.api.send.cmpp2;

import sunnyday.common.model.SmsMessage;

public class CmppMessageUtil{
  private SmsMessage smsMessage;
  private CMPPMessage cmppMessage;
  private long submit_time;
  private int try_times;

  public int getTry_times()
  {
    return this.try_times;
  }
  public void setTry_times(int tryTimes) {
    this.try_times = tryTimes;
  }

  public SmsMessage getSmsMessage() {
    return this.smsMessage;
  }
  public void setSmsMessage(SmsMessage smsMessage) {
    this.smsMessage = smsMessage;
  }
  public CMPPMessage getCmppMessage() {
    return this.cmppMessage;
  }
  public void setCmppMessage(CMPPMessage cmppMessage) {
    this.cmppMessage = cmppMessage;
  }
  public long getSubmit_time() {
    return this.submit_time;
  }
  public void setSubmit_time(long submit_time) {
    this.submit_time = submit_time;
  }
}