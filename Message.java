package DistSyslab0;

import java.io.Serializable;

public class Message implements Serializable
{
  private String destination;
  private String source;
  private String kind;
  private Object data;
  private int sequenceNumber;
  private Boolean isDuplicate;

  public Message(String dest, String kind, Object data)
  {
    this.destination = dest;
    this.kind = kind;
    this.data = data;
  }
 
  public void SetSource(String source)
  {
    this.source = source;
  } 
  public void SetSeqNum(int sequenceNumber)
  {
    this.sequenceNumber = sequenceNumber;
  }
  public void SetDuplicate(Boolean dupe)
  {
    this.isDuplicate = dupe;
  }
}

