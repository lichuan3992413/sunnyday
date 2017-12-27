package sunnyday.controller.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import sunnyday.common.model.ErrCode;
import sunnyday.tools.util.ParamUtil;
@Service
public class ErrCodeCache extends Cache {
	private static Map<String, ErrCode> err_code = null;
	@SuppressWarnings("unchecked")
	@Override
	public boolean reloadCache() {
		Map<String, ErrCode> tmp = (Map<String, ErrCode>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_ERROR_CODE);
		if(tmp!=null&&!tmp.isEmpty()){
			err_code =  tmp;
		}
		return true;
	}

	public static ErrCode getErrCode(ErrCode.codeName codeName){
		return getErrCode(codeName.name());
	}

	public static ErrCode getErrCode(String codeName){
		if(err_code==null){
			err_code = new HashMap<String, ErrCode>();
		}
		return err_code.get(codeName);
	}
}
