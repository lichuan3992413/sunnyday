package sunnyday.controller.cache;

import org.springframework.beans.factory.annotation.Autowired;

import sunnyday.controller.util.GateRAO;

public abstract class Cache {
	@Autowired
	protected GateRAO gateRAO;
	/**
	 * 重新加载缓存数据
	 * @return 当重新加载成功之后返回true, 如果出现任何问题返回false
	 */
	public abstract boolean reloadCache();
}
