package sunnyday.gateway.util;

import javax.servlet.http.HttpServletRequest;

public interface ISolver {
	public String doSolver(HttpServletRequest request);
	public String translateErrorCode(HSResponse response);
	public String getTargetName();

}
