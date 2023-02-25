package com.bigchaindb.cryptoconditions.types;

import net.i2p.crypto.eddsa.EdDSAPublicKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

import com.bigchaindb.cryptoconditions.Condition;
import com.bigchaindb.cryptoconditions.ConditionType;
import com.bigchaindb.cryptoconditions.Fulfillment;
import com.bigchaindb.cryptoconditions.der.DEROutputStream;

public class Ed25519Sha256Fulfillment implements Fulfillment
{
  private Ed25519Sha256Condition condition;
  private EdDSAPublicKey publicKey;
  private byte[] signature;
  private static MessageDigest _DIGEST;
  
  public Ed25519Sha256Fulfillment(EdDSAPublicKey publicKey, byte[] signature)
  {
    this.signature = new byte[signature.length];
    System.arraycopy(signature, 0, this.signature, 0, signature.length);
    this.publicKey = publicKey;
  }
  
  public ConditionType getType()
  {
    return ConditionType.ED25519_SHA256;
  }
  
  public EdDSAPublicKey getPublicKey() {
    return publicKey;
  }
  
  public byte[] getSignature() {
    byte[] signature = new byte[this.signature.length];
    System.arraycopy(this.signature, 0, signature, 0, this.signature.length);
    return signature;
  }
  
  public byte[] getEncoded()
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DEROutputStream out = new DEROutputStream(baos);
      out.writeTaggedObject(0, publicKey.getA().toByteArray());
      out.writeTaggedObject(1, signature);
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
  
  public Ed25519Sha256Condition getCondition()
  {
    if (condition == null) {
      condition = new Ed25519Sha256Condition(publicKey);
    }
    return condition;
  }
  

  public boolean verify(Condition condition, byte[] message)
  {
    if (condition == null) {
      throw new IllegalArgumentException("Can't verify a Ed25519Sha256Fulfillment against an null condition.");
    }
    

    if (!(condition instanceof Ed25519Sha256Condition)) {
      throw new IllegalArgumentException("Must verify a Ed25519Sha256Fulfillment against Ed25519Sha256Condition.");
    }
    

    if (!getCondition().equals(condition)) {
      return false;
    }
    try
    {
      Signature edDsaSigner = new net.i2p.crypto.eddsa.EdDSAEngine(getSha512Digest());
      edDsaSigner.initVerify(publicKey);
      edDsaSigner.update(message);
      return edDsaSigner.verify(signature);
    }
    catch (InvalidKeyException|SignatureException e) {
      e.printStackTrace(); }
    return false;
  }
  



  private static MessageDigest getSha512Digest()
  {
    if (_DIGEST == null) {
      try {
        _DIGEST = MessageDigest.getInstance("SHA-512");
      } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      }
    }
    
    return _DIGEST;
  }
}
