package sunnyday.api.send.cmpp2;

import com.hskj.datacenter.cache.TdInfoCache;
import java.util.Map;

public class ChargeTermIdPool{
  private static Map<String, String> chargeTermidMap = null;

  public static String getChargeTermId(String sp_number, String td_code)
  {
    String chargeTermId = "00000000000";
    String sp_number_tmp = "";

    chargeTermidMap = TdInfoCache.getChargeTermidMap();
    if ((chargeTermidMap != null) && (chargeTermidMap.size() > 0)) {
      for (String key : chargeTermidMap.keySet()) {
        int index = key.indexOf("_");
        String td_codeStr = key.substring(0, index);
        if (td_code.equalsIgnoreCase(td_codeStr)) {
          String sp_numberStr = key.substring(index + 1);
          if ((!sp_number.contains(sp_numberStr)) || 
            (sp_numberStr.length() <= sp_number_tmp.length())) continue;
          sp_number_tmp = sp_numberStr;
          chargeTermId = (String)chargeTermidMap.get(key);
        }

      }

    }

    if ((chargeTermId != null) && (!chargeTermId.trim().equals(""))) {
      return chargeTermId;
    }
    return "00000000000";
  }
}