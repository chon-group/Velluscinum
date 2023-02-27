package com.bigchaindb.cryptoconditions.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import com.bigchaindb.cryptoconditions.Condition;
import com.bigchaindb.cryptoconditions.ConditionType;
import com.bigchaindb.cryptoconditions.Fulfillment;
import com.bigchaindb.cryptoconditions.der.DEROutputStream;

public class PrefixSha256Fulfillment
  implements Fulfillment
{
  private PrefixSha256Condition condition;
  private Fulfillment subfulfillment;
  private long maxMessageLength;
  private byte[] prefix;
  
  public PrefixSha256Fulfillment(byte[] prefix, long maxMessageLength, Fulfillment subfulfillment)
  {
    this.prefix = new byte[prefix.length];
    System.arraycopy(prefix, 0, this.prefix, 0, prefix.length);
    
    this.maxMessageLength = maxMessageLength;
    

    this.subfulfillment = subfulfillment;
  }
  
  public ConditionType getType()
  {
    return ConditionType.PREFIX_SHA256;
  }
  
  public byte[] getPrefix() {
    byte[] prefix = new byte[this.prefix.length];
    System.arraycopy(this.prefix, 0, prefix, 0, this.prefix.length);
    return prefix;
  }
  
  public long getMaxMessageLenght() {
    return maxMessageLength;
  }
  
  public Fulfillment getSubfulfillment() {
    return subfulfillment;
  }
  
  public byte[] getEncoded()
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DEROutputStream out = new DEROutputStream(baos);
      out.writeTaggedObject(0, prefix);
      out.writeTaggedObject(1, BigInteger.valueOf(maxMessageLength).toByteArray());
      out.writeTaggedConstructedObject(2, subfulfillment.getEncoded());
      out.close();
      byte[] buffer = baos.toByteArray();
      

      baos = new ByteArrayOutputStream();
      out = new DEROutputStream(baos);
      out.writeTaggedConstructedObject(getType().getTypeCode(), buffer);
      out.close();
      
      return baos.toByteArray();
    }
    catch (IOException e) {
      throw new RuntimeException("DER Encoding Error", e);
    }
  }
  
  public PrefixSha256Condition getCondition()
  {
    if (condition == null)
    {
      condition = new PrefixSha256Condition(prefix, maxMessageLength, subfulfillment.getCondition());
    }
    return condition;
  }
  

  public boolean verify(Condition condition, byte[] message)
  {
    if (condition == null) {
      throw new IllegalArgumentException("Can't verify a PrefixSha256Fulfillment against an null condition.");
    }
    

    if (!(condition instanceof PrefixSha256Condition)) {
      throw new IllegalArgumentException("Must verify a PrefixSha256Fulfillment against PrefixSha256Condition.");
    }
    

    if (message.length > maxMessageLength) {
      throw new IllegalArgumentException("Message length exceeds maximum message length of " + maxMessageLength + ".");
    }
    

    if (!getCondition().equals(condition)) {
      return false;
    }
    
    Condition subcondition = subfulfillment.getCondition();
    byte[] prefixedMessage = Arrays.copyOf(prefix, prefix.length + message.length);
    System.arraycopy(message, 0, prefixedMessage, prefix.length, message.length);
    
    return subfulfillment.verify(subcondition, prefixedMessage);
  }
}
