package com.bigchaindb.cryptoconditions.types;

import net.i2p.crypto.eddsa.EdDSAPublicKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.bigchaindb.cryptoconditions.ConditionType;
import com.bigchaindb.cryptoconditions.Sha256Condition;
import com.bigchaindb.cryptoconditions.SimpleCondition;
import com.bigchaindb.cryptoconditions.der.DEROutputStream;
import com.bigchaindb.cryptoconditions.der.DERTags;

public class Ed25519Sha256Condition extends Sha256Condition implements SimpleCondition
{
  private EdDSAPublicKey key;
  
  public Ed25519Sha256Condition(EdDSAPublicKey key)
  {
    super(calculateCost(key));
    

    this.key = key;
  }
  
  public Ed25519Sha256Condition(byte[] fingerprint, long cost) {
    super(fingerprint, cost);
  }
  
  public ConditionType getType()
  {
    return ConditionType.ED25519_SHA256;
  }
  
  protected byte[] getFingerprintContents()
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DEROutputStream out = new DEROutputStream(baos);
      out.writeTaggedObject(0, key.getA().toByteArray());
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
  





  private static long calculateCost(EdDSAPublicKey key)
  {
    return 131072L;
  }
}
