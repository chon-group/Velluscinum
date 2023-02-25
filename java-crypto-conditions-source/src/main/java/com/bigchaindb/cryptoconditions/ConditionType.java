package com.bigchaindb.cryptoconditions;

import java.util.EnumSet;
import java.util.Iterator;

public enum ConditionType {
  PREIMAGE_SHA256(0, "PREIMAGE-SHA-256", 128, 0),
  PREFIX_SHA256(1, "PREFIX-SHA-256", 64, 0),
  THRESHOLD_SHA256(2, "THRESHOLD-SHA-256", 32, 0),
  RSA_SHA256(3, "RSA-SHA-256", 16, 0),
  ED25519_SHA256(4, "ED25519-SHA-256", 8, 0);

  private final int typeCode;
  private final String name;
  private final int bitMask;
  private final int byteIndex;

  private ConditionType(int typeCode, String algorithmName, int bitMask, int byteIndex) {
    this.typeCode = typeCode;
    this.name = algorithmName;
    this.bitMask = bitMask;
    this.byteIndex = byteIndex;
  }

  public int getTypeCode() {
    return this.typeCode;
  }

  public String toString() {
    return this.name;
  }

  public int getMask() {
    return this.bitMask;
  }

  public int getByteIndex() {
    return this.byteIndex;
  }

  public boolean isBitSet(byte[] bitString) {
    return bitString.length - 2 >= this.byteIndex && (bitString[this.byteIndex + 1] & this.bitMask) == this.bitMask;
  }

  public static ConditionType valueOf(int typeCode) {
    Iterator var1 = EnumSet.allOf(ConditionType.class).iterator();

    ConditionType conditionType;
    do {
      if (!var1.hasNext()) {
        throw new IllegalArgumentException("Invalid Condition Type code.");
      }

      conditionType = (ConditionType)var1.next();
    } while(typeCode != conditionType.typeCode);

    return conditionType;
  }

  public static byte[] getEnumOfTypesAsBitString(EnumSet<ConditionType> types) {
    byte[] data = new byte[2];
    int lastUsedBit = -1;
    if (types.contains(PREIMAGE_SHA256)) {
      data[1] = (byte)(data[1] + PREIMAGE_SHA256.getMask());
      lastUsedBit = PREIMAGE_SHA256.getTypeCode();
    }

    if (types.contains(PREFIX_SHA256)) {
      data[1] = (byte)(data[1] + PREFIX_SHA256.getMask());
      lastUsedBit = PREFIX_SHA256.getTypeCode();
    }

    if (types.contains(THRESHOLD_SHA256)) {
      data[1] = (byte)(data[1] + THRESHOLD_SHA256.getMask());
      lastUsedBit = THRESHOLD_SHA256.getTypeCode();
    }

    if (types.contains(RSA_SHA256)) {
      data[1] = (byte)(data[1] + RSA_SHA256.getMask());
      lastUsedBit = RSA_SHA256.getTypeCode();
    }

    if (types.contains(ED25519_SHA256)) {
      data[1] = (byte)(data[1] + ED25519_SHA256.getMask());
      lastUsedBit = ED25519_SHA256.getTypeCode();
    }

    if (lastUsedBit > -1) {
      data[0] = (byte)(7 - lastUsedBit);
      return data;
    } else {
      return new byte[]{0};
    }
  }

  public static String getEnumOfTypesAsString(EnumSet<ConditionType> types) {
    String[] names = new String[types.size()];
    int i = 0;

    ConditionType conditionType;
    for(Iterator var3 = types.iterator(); var3.hasNext(); names[i++] = conditionType.toString().toLowerCase()) {
      conditionType = (ConditionType)var3.next();
    }

    return join(",", names);
  }

  static public String join(String conjunction, String[] list)
  {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (String item : list)
    {
      if (first)
        first = false;
      else
        sb.append(conjunction);
      sb.append(item);
    }
    return sb.toString();
  }

  public static ConditionType fromString(String typeName) {
    Iterator var1 = EnumSet.allOf(ConditionType.class).iterator();

    ConditionType conditionType;
    do {
      if (!var1.hasNext()) {
        throw new IllegalArgumentException("Invalid Condition Type name.");
      }

      conditionType = (ConditionType)var1.next();
    } while(!conditionType.name.equalsIgnoreCase(typeName));

    return conditionType;
  }

  public static EnumSet<ConditionType> getEnumOfTypesFromString(String subtypes) {
    EnumSet<ConditionType> types = EnumSet.noneOf(ConditionType.class);
    if (subtypes != null && !subtypes.trim().isEmpty()) {
      String[] names = subtypes.split(",");
      String[] var3 = names;
      int var4 = names.length;

      for(int var5 = 0; var5 < var4; ++var5) {
        String typeName = var3[var5];
        types.add(fromString(typeName));
      }

      return types;
    } else {
      return types;
    }
  }

  public static EnumSet<ConditionType> getEnumOfTypesFromBitString(byte[] bitStringData) {
    if (bitStringData.length > 2) {
      throw new IllegalArgumentException("Unknown types in bit string.");
    } else if (bitStringData.length == 1) {
      throw new IllegalArgumentException("Corrupt bit string.");
    } else {
      EnumSet<ConditionType> subtypes = EnumSet.noneOf(ConditionType.class);
      if (bitStringData.length == 0) {
        return subtypes;
      } else {
        int padBits = bitStringData[0];
        if (padBits < 3) {
          throw new IllegalArgumentException("Unknown types in bit string.");
        } else {
          ConditionType[] var3 = values();
          int var4 = var3.length;

          for(int var5 = 0; var5 < var4; ++var5) {
            ConditionType type = var3[var5];
            if (type.isBitSet(bitStringData)) {
              subtypes.add(type);
            }
          }

          return subtypes;
        }
      }
    }
  }
}

