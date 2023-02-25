package com.bigchaindb.cryptoconditions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class Sha256Condition
  extends ConditionBase
{
  private byte[] fingerprint;
  private static MessageDigest _DIGEST;
  
  protected Sha256Condition(long cost)
  {
    super(cost);
  }
  
  protected Sha256Condition(byte[] fingerprint, long cost) {
    super(cost);
    this.fingerprint = fingerprint;
    
    if (fingerprint.length != 32) {
      throw new IllegalArgumentException("Fingerprint must be 32 bytes.");
    }
  }

  protected abstract byte[] getFingerprintContents();

  public byte[] getFingerprint()
  {
    if (fingerprint == null) {
      fingerprint = getDigest(getFingerprintContents());
    }
    
    byte[] returnVal = new byte[fingerprint.length];
    System.arraycopy(fingerprint, 0, returnVal, 0, fingerprint.length);
    
    return returnVal;
  }
  

  private static byte[] getDigest(byte[] input)
  {
    if (_DIGEST == null) {
      try {
        _DIGEST = MessageDigest.getInstance("SHA-256");
      } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      }
    }
    
    return _DIGEST.digest(input);
  }
}
