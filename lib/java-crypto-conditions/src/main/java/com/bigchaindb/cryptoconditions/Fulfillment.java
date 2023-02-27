package com.bigchaindb.cryptoconditions;

public abstract interface Fulfillment
{
  public abstract ConditionType getType();
  
  public abstract byte[] getEncoded();
  
  public abstract Condition getCondition();
  
  public abstract boolean verify(Condition paramCondition, byte[] paramArrayOfByte);
}
