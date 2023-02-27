package com.bigchaindb.cryptoconditions.der;


import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.bigchaindb.cryptoconditions.Condition;
import com.bigchaindb.cryptoconditions.ConditionType;
import com.bigchaindb.cryptoconditions.Fulfillment;
import com.bigchaindb.cryptoconditions.types.Ed25519Sha256Condition;
import com.bigchaindb.cryptoconditions.types.Ed25519Sha256Fulfillment;
import com.bigchaindb.cryptoconditions.types.PrefixSha256Condition;
import com.bigchaindb.cryptoconditions.types.PrefixSha256Fulfillment;
import com.bigchaindb.cryptoconditions.types.PreimageSha256Condition;
import com.bigchaindb.cryptoconditions.types.PreimageSha256Fulfillment;
import com.bigchaindb.cryptoconditions.types.RsaSha256Condition;
import com.bigchaindb.cryptoconditions.types.RsaSha256Fulfillment;
import com.bigchaindb.cryptoconditions.types.ThresholdSha256Condition;
import com.bigchaindb.cryptoconditions.types.ThresholdSha256Fulfillment;

public class CryptoConditionReader {
  public CryptoConditionReader() {
  }

  public static Condition readCondition(byte[] buffer) throws DEREncodingException {
    return readCondition(buffer, 0, buffer.length);
  }

  public static Condition readCondition(byte[] buffer, int offset, int length) throws DEREncodingException {
    ByteArrayInputStream bais = new ByteArrayInputStream(buffer, offset, length);
    DERInputStream in = new DERInputStream(bais);

    Condition var6;
    try {
      Condition c = readCondition(in);
      var6 = c;
    } catch (IOException var15) {
      throw new UncheckedIOException(var15);
    } finally {
      try {
        in.close();
      } catch (IOException var14) {
        throw new UncheckedIOException(var14);
      }
    }

    return var6;
  }

  public static Condition readCondition(DERInputStream in) throws DEREncodingException, IOException {
    return readCondition(in, new AtomicInteger());
  }

  public static Condition readCondition(DERInputStream in, AtomicInteger bytesRead) throws DEREncodingException, IOException {
    int tag = in.readTag(bytesRead, new DERTags[]{DERTags.CONSTRUCTED, DERTags.TAGGED});
    ConditionType type = ConditionType.valueOf(tag);
    int length = in.readLength(bytesRead);
    AtomicInteger innerBytesRead = new AtomicInteger();
    byte[] fingerprint = in.readTaggedObject(0, length - innerBytesRead.get(), innerBytesRead).getValue();
    long cost = (new BigInteger(in.readTaggedObject(1, length - innerBytesRead.get(), innerBytesRead).getValue())).longValue();
    EnumSet<ConditionType> subtypes = null;
    if (type == ConditionType.PREFIX_SHA256 || type == ConditionType.THRESHOLD_SHA256) {
      subtypes = ConditionType.getEnumOfTypesFromBitString(in.readTaggedObject(2, length - innerBytesRead.get(), innerBytesRead).getValue());
    }

    bytesRead.addAndGet(innerBytesRead.get());
    switch(type) {
      case PREIMAGE_SHA256:
        return new PreimageSha256Condition(fingerprint, cost);
      case PREFIX_SHA256:
        return new PrefixSha256Condition(fingerprint, cost, subtypes);
      case THRESHOLD_SHA256:
        return new ThresholdSha256Condition(fingerprint, cost, subtypes);
      case RSA_SHA256:
        return new RsaSha256Condition(fingerprint, cost);
      case ED25519_SHA256:
        return new Ed25519Sha256Condition(fingerprint, cost);
      default:
        throw new DEREncodingException("Unrecogized tag: " + tag);
    }
  }

  public static Fulfillment readFulfillment(byte[] buffer) throws DEREncodingException {
    return readFulfillment(buffer, 0, buffer.length);
  }

  public static Fulfillment readFulfillment(byte[] buffer, int offset, int length) throws DEREncodingException {
    ByteArrayInputStream bais = new ByteArrayInputStream(buffer, offset, length);
    DERInputStream in = new DERInputStream(bais);

    Fulfillment var5;
    try {
      var5 = readFulfillment(in);
    } catch (IOException var14) {
      throw new UncheckedIOException(var14);
    } finally {
      try {
        in.close();
      } catch (IOException var13) {
        throw new UncheckedIOException(var13);
      }
    }

    return var5;
  }

  public static Fulfillment readFulfillment(DERInputStream in) throws DEREncodingException, IOException {
    return readFulfillment(in, new AtomicInteger());
  }

  public static Fulfillment readFulfillment(DERInputStream in, AtomicInteger bytesRead) throws DEREncodingException, IOException {
    int tag = in.readTag(bytesRead, new DERTags[]{DERTags.CONSTRUCTED, DERTags.TAGGED});
    ConditionType type = ConditionType.valueOf(tag);
    int length = in.readLength(bytesRead);
    if (length == 0) {
      throw new DEREncodingException("Encountered an empty fulfillment.");
    } else {
      AtomicInteger innerBytesRead = new AtomicInteger();
      switch(type) {
        case PREIMAGE_SHA256:
          byte[] preimage = in.readTaggedObject(0, length - innerBytesRead.get(), innerBytesRead).getValue();
          bytesRead.addAndGet(innerBytesRead.get());
          return new PreimageSha256Fulfillment(preimage);
        case PREFIX_SHA256:
          byte[] prefix = in.readTaggedObject(0, length - innerBytesRead.get(), innerBytesRead).getValue();
          long maxMessageLength = (new BigInteger(in.readTaggedObject(1, length - innerBytesRead.get(), innerBytesRead).getValue())).longValue();
          in.readTag(2, innerBytesRead, new DERTags[]{DERTags.CONSTRUCTED, DERTags.TAGGED});
          in.readLength(innerBytesRead);
          Fulfillment subfulfillment = readFulfillment(in, innerBytesRead);
          bytesRead.addAndGet(innerBytesRead.get());
          return new PrefixSha256Fulfillment(prefix, maxMessageLength, subfulfillment);
        case THRESHOLD_SHA256:
          List<Fulfillment> subfulfillments = new ArrayList();
          tag = in.readTag(innerBytesRead, new DERTags[]{DERTags.CONSTRUCTED, DERTags.TAGGED});
          length = in.readLength(innerBytesRead);
          if (tag == 0) {
            AtomicInteger subfulfillmentsBytesRead = new AtomicInteger();

            while(subfulfillmentsBytesRead.get() < length) {
              subfulfillments.add(readFulfillment(in, subfulfillmentsBytesRead));
            }

            innerBytesRead.addAndGet(subfulfillmentsBytesRead.get());
            in.readTag(1, innerBytesRead, new DERTags[]{DERTags.CONSTRUCTED, DERTags.TAGGED});
            length = in.readLength(innerBytesRead);
          } else if (tag != 1) {
            throw new DEREncodingException("Expected tag: 1, got: " + tag);
          }

          List<Condition> subconditions = new ArrayList();
          AtomicInteger subconditionsBytesRead = new AtomicInteger();

          while(subconditionsBytesRead.get() < length) {
            subconditions.add(readCondition(in, subconditionsBytesRead));
          }

          innerBytesRead.addAndGet(subconditionsBytesRead.get());
          bytesRead.addAndGet(innerBytesRead.get());
          return new ThresholdSha256Fulfillment((Condition[])subconditions.toArray(new Condition[subconditions.size()]), (Fulfillment[])subfulfillments.toArray(new Fulfillment[subfulfillments.size()]));
        case RSA_SHA256:
          BigInteger modulus = new BigInteger(in.readTaggedObject(0, length - innerBytesRead.get(), innerBytesRead).getValue());
          byte[] rsaSignature = in.readTaggedObject(1, length - innerBytesRead.get(), innerBytesRead).getValue();
          bytesRead.addAndGet(innerBytesRead.get());
          RSAPublicKeySpec rsaSpec = new RSAPublicKeySpec(modulus, RsaSha256Fulfillment.PUBLIC_EXPONENT);

          try {
            KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = rsaKeyFactory.generatePublic(rsaSpec);
            return new RsaSha256Fulfillment((RSAPublicKey)publicKey, rsaSignature);
          } catch (InvalidKeySpecException | NoSuchAlgorithmException var21) {
            throw new RuntimeException("Error creating RSA key.", var21);
          }
        case ED25519_SHA256:
          byte[] ed25519key = in.readTaggedObject(0, length - innerBytesRead.get(), innerBytesRead).getValue();
          byte[] ed25519Signature = in.readTaggedObject(1, length - innerBytesRead.get(), innerBytesRead).getValue();
          bytesRead.addAndGet(innerBytesRead.get());
          EdDSAPublicKeySpec ed25519spec = new EdDSAPublicKeySpec(ed25519key, EdDSANamedCurveTable.getByName("ed25519-sha-512"));
          EdDSAPublicKey ed25519PublicKey = new EdDSAPublicKey(ed25519spec);
          return new Ed25519Sha256Fulfillment(ed25519PublicKey, ed25519Signature);
        default:
          throw new DEREncodingException("Unrecogized tag: " + tag);
      }
    }
  }
}
