package com.bigchaindb.cryptoconditions.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import com.bigchaindb.cryptoconditions.Condition;
import com.bigchaindb.cryptoconditions.ConditionType;
import com.bigchaindb.cryptoconditions.Fulfillment;
import com.bigchaindb.cryptoconditions.der.DEROutputStream;

public class PreimageSha256Fulfillment implements Fulfillment
{
  private PreimageSha256Condition condition;
  private byte[] preimage;
  
  public PreimageSha256Fulfillment(byte[] preimage)
  {
    this.preimage = new byte[preimage.length];
    System.arraycopy(preimage, 0, this.preimage, 0, preimage.length);
  }
  
  public ConditionType getType()
  {
    return ConditionType.PREIMAGE_SHA256;
  }
  
  public byte[] getPreimage() {
    byte[] preimage = new byte[this.preimage.length];
    System.arraycopy(this.preimage, 0, preimage, 0, this.preimage.length);
    return preimage;
  }
  
  public byte[] getEncoded()
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DEROutputStream out = new DEROutputStream(baos);
      out.writeTaggedObject(0, preimage);
      out.close();
      byte[] buffer = baos.toByteArray();
      

      baos = new ByteArrayOutputStream();
      out = new DEROutputStream(baos);
      out.writeTaggedConstructedObject(getType().getTypeCode(), buffer);
      out.close();
      
      return baos.toByteArray();
    }
    catch (IOException e) {
      throw new UncheckedIOException("DER Encoding Error", e);
    }
  }
  
  public PreimageSha256Condition getCondition()
  {
    if (condition == null) {
      condition = new PreimageSha256Condition(preimage);
    }
    return condition;
  }
  

  public boolean verify(Condition condition, byte[] message)
  {
    if (condition == null) {
      throw new IllegalArgumentException("Can't verify a PreimageSha256Fulfillment against an null condition.");
    }
    

    if (!(condition instanceof PreimageSha256Condition)) {
      throw new IllegalArgumentException("Must verify a PreimageSha256Fulfillment against PreimageSha256Condition.");
    }
    

    return getCondition().equals(condition);
  }
}
