package com.bigchaindb.cryptoconditions;

import java.net.URI;

public abstract interface Condition
{
  public abstract ConditionType getType();
  
  public abstract byte[] getFingerprint();
  
  public abstract long getCost();
  
  public abstract byte[] getEncoded();
  
  public abstract URI getUri();
}
