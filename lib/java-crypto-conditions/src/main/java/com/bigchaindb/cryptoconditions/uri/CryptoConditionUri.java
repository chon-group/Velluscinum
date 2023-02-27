package com.bigchaindb.cryptoconditions.uri;

import com.bigchaindb.cryptoconditions.Condition;
import com.bigchaindb.cryptoconditions.ConditionType;
import com.bigchaindb.cryptoconditions.types.Ed25519Sha256Condition;
import com.bigchaindb.cryptoconditions.types.PrefixSha256Condition;
import com.bigchaindb.cryptoconditions.types.PreimageSha256Condition;
import com.bigchaindb.cryptoconditions.types.RsaSha256Condition;
import com.bigchaindb.cryptoconditions.types.ThresholdSha256Condition;
import com.google.api.client.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CryptoConditionUri {
  public static final String CONDITION_REGEX_STRICT = "^ni://([A-Za-z0-9_-]?)/sha-256;([a-zA-Z0-9_-]{0,86})\\?(.+)$";

  public CryptoConditionUri() {
  }

  public static Condition parse(URI uri) throws URIEncodingException {
    if (!"ni".equals(uri.getScheme())) {
      throw new URIEncodingException("Serialized condition must start with 'ni:'");
    } else {
      Matcher m = Pattern.compile("^ni://([A-Za-z0-9_-]?)/sha-256;([a-zA-Z0-9_-]{0,86})\\?(.+)$").matcher(uri.toString());
      if (!m.matches()) {
        throw new URIEncodingException("Invalid condition format");
      } else {
        Map queryParams = null;

        try {
          queryParams = splitQuery(uri.getQuery());
        } catch (UnsupportedEncodingException var9) {
          throw new URIEncodingException("Invalid condition format");
        }

        if (!queryParams.containsKey("fpt")) {
          throw new URIEncodingException("No fingerprint type provided");
        } else {
          ConditionType type = ConditionType.fromString((String)((List)queryParams.get("fpt")).get(0));
          long cost = 0L;

          try {
            cost = Long.parseLong((String)((List)queryParams.get("cost")).get(0));
          } catch (NullPointerException | NumberFormatException var8) {
            throw new URIEncodingException("No or invalid cost provided");
          }

          byte[] fingerprint = Base64.decodeBase64(m.group(2));
          EnumSet<ConditionType> subtypes = null;
          if (type == ConditionType.PREFIX_SHA256 || type == ConditionType.THRESHOLD_SHA256) {
            if (!queryParams.containsKey("subtypes")) {
              throw new URIEncodingException("No subtypes provided");
            }

            subtypes = ConditionType.getEnumOfTypesFromString((String)((List)queryParams.get("subtypes")).get(0));
          }

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
              throw new URIEncodingException("No or invalid type provided");
          }
        }
      }
    }
  }

  private static Map<String, List<String>> splitQuery(String queryParams) throws UnsupportedEncodingException {
    Map<String, List<String>> query_pairs = new LinkedHashMap();
    String[] pairs = queryParams.split("&");
    String[] var3 = pairs;
    int var4 = pairs.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      String pair = var3[var5];
      int idx = pair.indexOf("=");
      String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
      if (!query_pairs.containsKey(key)) {
        query_pairs.put(key, new LinkedList());
      }

      String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
      ((List)query_pairs.get(key)).add(value);
    }

    return query_pairs;
  }

  public static class QueryParams {
    public static final String COST = "cost";
    public static final String TYPE = "fpt";
    public static final String SUBTYPES = "subtypes";

    public QueryParams() {
    }
  }
}
