package com.bigchaindb.cryptoconditions.der;

public enum DERTags
{
  BOOLEAN(1), 
  INTEGER(2), 
  BIT_STRING(3), 
  OCTET_STRING(4), 
  NULL(5), 
  OBJECT_IDENTIFIER(6), 
  EXTERNAL(8), 
  ENUMERATED(10), 
  SEQUENCE(16), 
  SEQUENCE_OF(16), 
  SET(17), 
  SET_OF(17), 
  NUMERIC_STRING(18), 
  PRINTABLE_STRING(19), 
  T61_STRING(20), 
  VIDEOTEX_STRING(21), 
  IA5_STRING(22), 
  UTC_TIME(23), 
  GENERALIZED_TIME(24), 
  GRAPHIC_STRING(25), 
  VISIBLE_STRING(26), 
  GENERAL_STRING(27), 
  UNIVERSAL_STRING(28), 
  BMP_STRING(30), 
  UTF8_STRING(12), 
  CONSTRUCTED(32), 
  APPLICATION(64), 
  TAGGED(128);
  
  private int tag;
  
  private DERTags(int tag) {
    this.tag = tag;
  }
  
  public int getTag() {
    return tag;
  }
}
