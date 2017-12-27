package sunnyday.channel.thread;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import sunnyday.adapter.redis.DCAdapter;

@Repository
public class SendRAO extends CommonRAO {
	@Resource(name = "redisCli")
	protected DCAdapter dc;
	@Override
	protected DCAdapter getDc() {
		return dc;
	}
	
	
}
