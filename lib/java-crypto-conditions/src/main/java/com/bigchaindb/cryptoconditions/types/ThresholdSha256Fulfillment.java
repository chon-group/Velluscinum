package com.bigchaindb.cryptoconditions.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.bigchaindb.cryptoconditions.Condition;
import com.bigchaindb.cryptoconditions.ConditionType;
import com.bigchaindb.cryptoconditions.Fulfillment;
import com.bigchaindb.cryptoconditions.der.DEROutputStream;

public class ThresholdSha256Fulfillment implements Fulfillment
{
  private ThresholdSha256Condition condition;
  private Condition[] subconditions;
  private Fulfillment[] subfulfillments;
  
  public ThresholdSha256Fulfillment(Condition[] subconditions, Fulfillment[] subfulfillments)
  {
    this.subconditions = new Condition[subconditions.length];
    System.arraycopy(subconditions, 0, this.subconditions, 0, subconditions.length);
    

    this.subfulfillments = new Fulfillment[subfulfillments.length];
    System.arraycopy(subfulfillments, 0, this.subfulfillments, 0, subfulfillments.length);
  }
  

  public ConditionType getType()
  {
    return ConditionType.THRESHOLD_SHA256;
  }
  
  public int getThreshold() {
    return subfulfillments.length;
  }
  
  public Condition[] getSubconditions() {
    Condition[] subconditions = new Condition[this.subconditions.length];
    System.arraycopy(this.subconditions, 0, subconditions, 0, this.subconditions.length);
    return subconditions;
  }
  
  public Fulfillment[] getSubfulfillments() {
    Fulfillment[] subfulfillments = new Fulfillment[this.subfulfillments.length];
    System.arraycopy(this.subfulfillments, 0, subfulfillments, 0, this.subfulfillments.length);
    return subfulfillments;
  }
  
  public byte[] getEncoded()
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      for (int i = 0; i < subfulfillments.length; i++) {
        baos.write(subfulfillments[i].getEncoded());
      }
      baos.close();
      byte[] fulfillmentsBuffer = baos.toByteArray();
      

      baos = new ByteArrayOutputStream();
      DEROutputStream out = new DEROutputStream(baos);
      out.writeTaggedConstructedObject(0, fulfillmentsBuffer);
      out.close();
      fulfillmentsBuffer = baos.toByteArray();
      

      baos = new ByteArrayOutputStream();
      for (int i = 0; i < subconditions.length; i++) {
        baos.write(subconditions[i].getEncoded());
      }
      out.close();
      byte[] conditionsBuffer = baos.toByteArray();
      

      baos = new ByteArrayOutputStream();
      out = new DEROutputStream(baos);
      out.writeTaggedConstructedObject(1, conditionsBuffer);
      out.close();
      conditionsBuffer = baos.toByteArray();
      
      byte[] buffer = new byte[fulfillmentsBuffer.length + conditionsBuffer.length];
      System.arraycopy(fulfillmentsBuffer, 0, buffer, 0, fulfillmentsBuffer.length);
      System.arraycopy(conditionsBuffer, 0, buffer, fulfillmentsBuffer.length, conditionsBuffer.length);
      


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
  
  public ThresholdSha256Condition getCondition()
  {
    if (condition == null)
    {


      Condition[] allConditions = new Condition[subconditions.length + subfulfillments.length];
      System.arraycopy(subconditions, 0, allConditions, 0, subconditions.length);
      
      int j = subconditions.length;
      for (int i = 0; i < subfulfillments.length; i++) {
        allConditions[j] = subfulfillments[i].getCondition();
        j++;
      }
      condition = new ThresholdSha256Condition(subfulfillments.length, allConditions);
    }
    return condition;
  }
  

  public boolean verify(Condition condition, byte[] message)
  {
    if (condition == null) {
      throw new IllegalArgumentException("Can't verify a ThresholdSha256Fulfillment against an null condition.");
    }
    

    if (!(condition instanceof ThresholdSha256Condition)) {
      throw new IllegalArgumentException("Must verify a ThresholdSha256Fulfillment against ThresholdSha256Condition.");
    }
    

    if (!getCondition().equals(condition)) {
      return false;
    }
    
    for (int i = 0; i < subfulfillments.length; i++) {
      Condition subcondition = subfulfillments[i].getCondition();
      if (!subfulfillments[i].verify(subcondition, message)) {
        return false;
      }
    }
    
    return true;
  }
}
