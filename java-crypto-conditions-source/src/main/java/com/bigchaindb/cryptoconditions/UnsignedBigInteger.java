package com.bigchaindb.cryptoconditions;

import java.math.BigInteger;
import java.util.Arrays;


public class UnsignedBigInteger
{
  public UnsignedBigInteger() {}
  
  public static byte[] toUnsignedByteArray(BigInteger value)
  {
    byte[] signedValue = value.toByteArray();
    if (signedValue[0] == 0) {
      return Arrays.copyOfRange(signedValue, 1, signedValue.length);
    }
    
    return signedValue;
  }
  







  public static BigInteger fromUnsignedByteArray(byte[] value)
  {
    return new BigInteger(1, value);
  }
}
