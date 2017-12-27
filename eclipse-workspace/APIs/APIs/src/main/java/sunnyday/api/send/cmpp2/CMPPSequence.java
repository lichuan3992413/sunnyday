package sunnyday.api.send.cmpp2;

public class CMPPSequence
{
  private static final int MIN_SEQ = 0;
  private static final int MAX_SEQ = 2147483647;
  private static int seq_index;

  public static synchronized int createSequence()
  {
    if (seq_index == 2147483647) {
      seq_index = 0;
    }

    return ++seq_index;
  }
}