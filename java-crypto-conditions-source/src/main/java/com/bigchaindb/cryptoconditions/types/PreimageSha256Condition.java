package com.bigchaindb.cryptoconditions.types;

import com.bigchaindb.cryptoconditions.ConditionType;
import com.bigchaindb.cryptoconditions.Sha256Condition;
import com.bigchaindb.cryptoconditions.SimpleCondition;

public class PreimageSha256Condition extends Sha256Condition implements SimpleCondition
{
  private byte[] preimage;
  
  public PreimageSha256Condition(byte[] preimage)
  {
    super(calculateCost(preimage));
    this.preimage = new byte[preimage.length];
    System.arraycopy(preimage, 0, this.preimage, 0, preimage.length);
  }
  
  public PreimageSha256Condition(byte[] fingerprint, long cost) {
    super(fingerprint, cost);
  }
  
  public ConditionType getType()
  {
    return ConditionType.PREIMAGE_SHA256;
  }
  



  protected byte[] getFingerprintContents()
  {
    return preimage;
  }
  





  private static long calculateCost(byte[] preimage)
  {
    return preimage.length;
  }
}
