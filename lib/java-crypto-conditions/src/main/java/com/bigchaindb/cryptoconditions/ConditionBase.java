package com.bigchaindb.cryptoconditions;

import com.bigchaindb.cryptoconditions.der.DEROutputStream;
import com.bigchaindb.cryptoconditions.der.DERTags;
import com.google.api.client.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.Arrays;

public abstract class ConditionBase implements Condition {
  private long cost;
  private URI uri;
  private byte[] encoded;

  protected ConditionBase(long cost) {
    this.cost = cost;
  }

  public long getCost() {
    return this.cost;
  }

  public byte[] getEncoded() {
    if (this.encoded == null) {
      try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DEROutputStream out = new DEROutputStream(baos);
        out.writeTaggedObject(0, this.getFingerprint());
        out.writeTaggedObject(1, BigInteger.valueOf(this.getCost()).toByteArray());
        byte[] buffer;
        if (this instanceof CompoundCondition) {
          buffer = ConditionType.getEnumOfTypesAsBitString(((CompoundCondition)this).getSubtypes());
          out.writeTaggedObject(2, buffer);
        }

        out.close();
        buffer = baos.toByteArray();
        baos = new ByteArrayOutputStream();
        out = new DEROutputStream(baos);
        out.writeEncoded(DERTags.CONSTRUCTED.getTag() + DERTags.TAGGED.getTag() + this.getType().getTypeCode(), buffer);
        out.close();
        return baos.toByteArray();
      } catch (IOException var4) {
        throw new RuntimeException("DER Encoding Error.", var4);
      }
    } else {
      byte[] returnVal = new byte[this.encoded.length];
      System.arraycopy(this.encoded, 0, returnVal, 0, this.encoded.length);
      return returnVal;
    }
  }

  public URI getUri() {
    if (this.uri == null) {
      StringBuilder sb = new StringBuilder();
      sb.append("ni:///").append("sha-256;").append(Base64.encodeBase64URLSafeString(this.getFingerprint())).append("?").append("fpt=").append(this.getType().toString().toLowerCase()).append("&cost=").append(this.getCost());
      if (this instanceof CompoundCondition) {
        CompoundCondition cc = (CompoundCondition)this;
        if (cc.getSubtypes() != null && !cc.getSubtypes().isEmpty()) {
          sb.append("&subtypes=").append(ConditionType.getEnumOfTypesAsString(cc.getSubtypes()));
        }
      }

      this.uri = URI.create(sb.toString());
    }

    return this.uri;
  }
/*
  public int hashCode() {
    int prime = true;
    int result = 1;
    int typeCode = this.getType().getTypeCode();
    int result = 31 * result + (typeCode ^ typeCode >>> 32);
    result = 31 * result + Arrays.hashCode(this.getEncoded());
    return result;
  }*/

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof Condition)) {
      return false;
    } else {
      Condition other = (Condition)obj;
      if (this.getType() != other.getType()) {
        return false;
      } else if (this.getCost() != other.getCost()) {
        return false;
      } else {
        return Arrays.equals(this.getFingerprint(), other.getFingerprint());
      }
    }
  }

  public String toString() {
    return this.getUri().toString();
  }
}
