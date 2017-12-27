package sunnyday.controller.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import sunnyday.common.model.GateErrCode;
import sunnyday.tools.util.ParamUtil;

/**
 * 从redis中读取gate_err_code表缓存信息
 * 
 * @author 71604057
 * @time 2017年6月10日 上午11:12:51
 */
@Service
public class GateErrCodeCache extends Cache {
	
	private static Map<String, GateErrCode> gateErrCode = null;
	private static Set<String> exceptional_code = new HashSet<String>();
	@SuppressWarnings("unchecked")
	@Override
	public boolean reloadCache() {
		gateErrCode = (Map<String, GateErrCode>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_GATE_ERROR_CODE);
		if(gateErrCode!=null&&!gateErrCode.isEmpty()){
			Set<String> tmp_set = new HashSet<String>();
			for(GateErrCode err :gateErrCode.values()){
				if(err!=null&&"0".equals(err.getIs_record_mobile())){
					tmp_set.add(err.getErr_desc());
				}
			}
			exceptional_code = tmp_set ;
		}
		return true;
	}

	public static GateErrCode getGateErrCode(String gateErrCodeKey) {
		if (gateErrCode == null) {
			gateErrCode = new HashMap<String, GateErrCode>();
			System.err.println("gate_err_code is null.");
		}
		return gateErrCode.get(gateErrCodeKey);
	}
	
	public static boolean isExceptionalCode(String code) {
		if (exceptional_code != null) {
			return exceptional_code.contains(code);
		}
		return  false;
	}
}
