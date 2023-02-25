package com.bigchaindb.cryptoconditions.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

import com.bigchaindb.cryptoconditions.ConditionType;
import com.bigchaindb.cryptoconditions.Sha256Condition;
import com.bigchaindb.cryptoconditions.SimpleCondition;
import com.bigchaindb.cryptoconditions.UnsignedBigInteger;
import com.bigchaindb.cryptoconditions.der.DEROutputStream;
import com.bigchaindb.cryptoconditions.der.DERTags;

public class RsaSha256Condition extends Sha256Condition implements SimpleCondition
{
  private RSAPublicKey key;
  
  public RsaSha256Condition(RSAPublicKey key)
  {
    super(calculateCost(key));
    

    if (key.getPublicExponent().compareTo(RsaSha256Fulfillment.PUBLIC_EXPONENT) != 0) {
      throw new IllegalArgumentException("Public Exponent of RSA key must be 65537.");
    }
    
    if ((key.getModulus().bitLength() <= 1017) || (key.getModulus().bitLength() > 4096)) {
      throw new IllegalArgumentException("Modulus of RSA key must be greater than 128 bytes and less than 512 bytes.");
    }
    

    this.key = key;
  }
  
  public RsaSha256Condition(byte[] fingerprint, long cost) {
    super(fingerprint, cost);
  }
  
  public ConditionType getType()
  {
    return ConditionType.RSA_SHA256;
  }
  
  protected byte[] getFingerprintContents()
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DEROutputStream out = new DEROutputStream(baos);
      out.writeTaggedObject(0, UnsignedBigInteger.toUnsignedByteArray(key.getModulus()));
      out.close();
      byte[] buffer = baos.toByteArray();
      

      baos = new ByteArrayOutputStream();
      out = new DEROutputStream(baos);
      out.writeEncoded(DERTags.CONSTRUCTED.getTag() + DERTags.SEQUENCE.getTag(), buffer);
      out.close();
      return baos.toByteArray();
    }
    catch (IOException e) {
      throw new RuntimeException("DER Encoding Error", e);
    }
  }
  





  private static long calculateCost(RSAPublicKey key)
  {
    return (long) Math.pow(UnsignedBigInteger.toUnsignedByteArray(key.getModulus()).length, 2.0D);
  }
}
