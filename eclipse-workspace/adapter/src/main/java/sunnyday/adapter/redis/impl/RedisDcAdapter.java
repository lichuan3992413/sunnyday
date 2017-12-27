package sunnyday.adapter.redis.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import sunnyday.adapter.redis.Chargable;
import sunnyday.adapter.redis.DCAdapter;
import sunnyday.adapter.redis.exception.NoSuchQueueException;
import sunnyday.tools.util.CommonLogFactory;

public class RedisDcAdapter implements DCAdapter {
	private Logger log_info = CommonLogFactory.getLog(RedisDcAdapter.class);
	private JedisPool pool = null;
	private String poolType =  null;
	private String redisServerIp = "";
	private String sentinels = "";
	private String pwd = null;//鉴权密码
	private int redisServerPort = 6379;
	private String masterName="mymaster";
	private int maxTotal = 2000;
	private int maxIdle = 100;
	private int maxWaitMillis = 10000;
	private boolean testOnBorrow = true;
	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	private final int COUNT = 5000;
	private JedisSentinelPool sentinelPool;
	private Map<Integer, String> poolInfoMap = new HashMap<Integer, String>();
	
	 
	public String getPoolType() {
		return poolType;
	}

	public void setPoolType(String poolType) {
		this.poolType = poolType;
	}

	public String getSentinels() {
		return sentinels;
	}

	public void setSentinels(String sentinels) {
		this.sentinels = sentinels;
	}

	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	public JedisSentinelPool getsentinelPool() {
		return sentinelPool;
	}

	public void setsentinelPool(JedisSentinelPool sentinelPool) {
		this.sentinelPool = sentinelPool;
	}

	 
	public RedisDcAdapter() {
		
	}

	public RedisDcAdapter(String redisServerIp, int redisServerPort) {
		this.redisServerIp = redisServerIp;
		this.redisServerPort = redisServerPort;
	}

	/**
	 * 
	 * @param redisServerIp
	 * @param redisServerPort
	 * @param maxTotal
	 * @param maxIdle
	 * @param maxWaitMillis
	 * @param testOnBorrow
	 */
	public RedisDcAdapter(String redisServerIp, int redisServerPort, int maxTotal, int maxIdle, int maxWaitMillis, boolean testOnBorrow) {
		this.redisServerIp = redisServerIp;
		this.redisServerPort = redisServerPort;
		this.maxTotal = maxTotal;
		this.maxIdle = maxIdle;
		this.maxWaitMillis = maxWaitMillis;
		this.testOnBorrow = testOnBorrow;
	}
	
	public RedisDcAdapter(String redisServerIp, int redisServerPort, int maxTotal, int maxIdle, int maxWaitMillis, String pwd,boolean testOnBorrow) {
		this.redisServerIp = redisServerIp;
		this.redisServerPort = redisServerPort;
		this.maxTotal = maxTotal;
		this.maxIdle = maxIdle;
		this.maxWaitMillis = maxWaitMillis;
		this.pwd = pwd;
		this.testOnBorrow = testOnBorrow;
	
	}

	public RedisDcAdapter(String sentinels,String poolType, String masterName, int maxTotal, int maxIdle, int maxWaitMillis, boolean testOnBorrow) {
		this.sentinels = sentinels;
		this.poolType = poolType;
		this.masterName=masterName;
		this.maxTotal = maxTotal;
		this.maxIdle = maxIdle;
		this.maxWaitMillis = maxWaitMillis;
		this.testOnBorrow = testOnBorrow;
	}
	
	public RedisDcAdapter(String sentinels,String poolType, String masterName, int maxTotal, int maxIdle, int maxWaitMillis,String pwd, boolean testOnBorrow) {
		this.sentinels = sentinels;
		this.poolType = poolType;
		this.masterName=masterName;
		this.maxTotal = maxTotal;
		this.maxIdle = maxIdle;
		this.maxWaitMillis = maxWaitMillis;
		this.pwd = pwd;
		this.testOnBorrow = testOnBorrow;
	}
	

	/**
	 * spring初始创建好所需的连接池
	 */
	public   void initPool() {
		if(poolType==null||"".equals(poolType)){
			log_info.error("poolType:["+poolType+"],配置错误！！！！");
		}else{
			if("sentinel".equals(poolType.trim())){
				if(sentinelPool==null){
					initSentinelPool();
				}
			} else if("single".equals(poolType)) {
				if(pool==null){
					JedisPool();
				}
			}else {
				log_info.error("poolType:["+poolType+"],配置错误！！！！");
			}
		}
	}
	
	
	public   void JedisPool() {
		pool = getPool();
		log_info.info("initPool ["+poolType+"] a pool hashCode:" + pool.hashCode()+", redisServerIp: "+redisServerIp+":"+redisServerPort);
	}
	
	public   void initSentinelPool() {
		sentinelPool = getSentinelPool(); 
		log_info.info("initSentinelPool ["+poolType+"] a pool hashCode:" + sentinelPool.hashCode()+", sentinels: "+sentinels);
	}
	

	private   JedisSentinelPool getSentinelPool() {
		if (sentinelPool == null) {
			Set<String> set = new HashSet<String>();
			// 设置3个哨兵
			String[] sentinel = sentinels.split(";");
			for (String ip :sentinel) {
				String[] address = ip.split(":");
				set.add(new HostAndPort(address[0],Integer.valueOf(address[1])).toString());
			}
			JedisPoolConfig config = new JedisPoolConfig();
			// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
			config.setMaxTotal(maxTotal);
			// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
			config.setMaxIdle(maxIdle);
			// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
			config.setMaxWaitMillis(maxWaitMillis);
			
			config.setMinIdle(maxIdle);
			
			// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
			config.setTestOnBorrow(testOnBorrow);
			 
			if(pwd!=null&&!"".equals(pwd)){
				sentinelPool = new JedisSentinelPool(masterName, set, config,maxWaitMillis,pwd);
			}else{
				sentinelPool = new JedisSentinelPool(masterName, set, config,maxWaitMillis);
			}
			poolInfoMap.put(sentinelPool.hashCode(), String.valueOf(sentinels));
		}
		return sentinelPool;
	}

	public  JedisPool getPool() {
		if (pool == null) {
			JedisPoolConfig config = new JedisPoolConfig();
			// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
			config.setMaxTotal(maxTotal);
			// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
			config.setMaxIdle(maxIdle);
			
			config.setMinIdle(maxIdle);
			// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
			config.setMaxWaitMillis(maxWaitMillis);
			// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
			config.setTestOnBorrow(testOnBorrow);
			 
			 
			if(pwd!=null&&!"".equals(pwd)){
				pool = new JedisPool(config, redisServerIp, redisServerPort,maxWaitMillis,pwd);
			}else{
				pool = new JedisPool(config, redisServerIp, redisServerPort,maxWaitMillis);
			}
			poolInfoMap.put(pool.hashCode(), String.valueOf(redisServerIp+":"+redisServerPort));
		}
		return pool;
	}

	 
	 
	public Jedis getJedis() {
		Jedis jedis = null;
		int code = 0;
		try {
			if(poolType==null||"".equals(poolType)){
				log_info.error("poolType:["+poolType+"],配置错误！！！！");
				return jedis;
			}
			if("sentinel".equals(poolType.trim())){
				if(sentinelPool==null){
					initSentinelPool();
				}
				code = sentinelPool.hashCode();
				jedis = sentinelPool.getResource();
			} else if("single".equals(poolType)) {
				if(pool==null){
					JedisPool();
				}
				code = pool.hashCode();
				jedis = pool.getResource();
			}else {
				log_info.error("poolType:["+poolType+"],配置错误！！！！");
				return jedis;
			}
			
		} catch (Exception e) {
			returnJedis(jedis);
			StringBuffer sb = new StringBuffer();
			sb.append("poolType:").append(poolType).append(",");
			log_info.error("get jedis from [" +poolInfoMap.get(code)  + "]; " + sb.toString(), e);
		}
		return jedis;
	}

	public void returnJedis(Jedis jedis) {
		if (jedis != null) {
			try {
				jedis.close();
			} catch (Exception e) {
				log_info.error("",e);
			}
		}

	}

	public String getRedisServerIp() {
		return redisServerIp;
	}

	public void setRedisServerIp(String redisServerIp) {
		this.redisServerIp = redisServerIp;
	}

	public int getRedisServerPort() {
		return redisServerPort;
	}

	public void setRedisServerPort(int redisServerPort) {
		this.redisServerPort = redisServerPort;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public void setMaxWaitMillis(int maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public void setPool(JedisPool pool) {
		this.pool = pool;
	}

	@SuppressWarnings("unchecked")
	public <T> T getQueueElement(String queueName) throws NoSuchQueueException {
		T result = null;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			byte[] tmp = jedis.rpop(queueName.getBytes());
			if (tmp != null) {
				try {
					result = (T) new ObjectInputStream(
							new ByteArrayInputStream(tmp)).readObject();
				} catch (IOException e) {
					log_info.error("",e);
				} catch (ClassNotFoundException e) {
					log_info.error("",e);
				}
			}
		} catch (Exception e) {
			log_info.error("getQueueElement,key=[" + queueName+ "] ", e);
		} finally {
			returnJedis(jedis);
		}
		return result;
	}

	public List<Object> getQueueElements(String queueName, int number)
			throws NoSuchQueueException {
		List<Object> result = new ArrayList<Object>();
		List<Object> tmp_list = new ArrayList<Object>();

		Jedis jedis = null;
		try {
			jedis = getJedis();
			long length = jedis.llen(queueName.getBytes());
			if (length > 0) {
				long getNumber = (length > number ? number : length);
				Pipeline pipeline = jedis.pipelined();
				for (int i = 0; i < getNumber; i++) {
					pipeline.rpop(queueName.getBytes());
				}
				tmp_list = pipeline.syncAndReturnAll();

			}

			for (int i = 0; tmp_list != null && i < tmp_list.size(); i++) {
				if (tmp_list.get(i) != null) {
					byte[] tmp = (byte[]) tmp_list.get(i);
					Object obj = Utils.toObject(tmp);
					if (obj != null) {
						result.add(obj);
					}
				}

			}
		} catch (Exception e) {
			log_info.error("getQueueElements,key=[" + queueName+ "]", e);
		} finally {
			returnJedis(jedis);
		}
		return result;
	}

	public <T> boolean addQueueElement(String queueName, T element) {
		boolean result = false;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			new ObjectOutputStream(bos).writeObject(element);
		} catch (IOException e) {
		}
		Jedis jedis = null;
		try {
			jedis = getJedis();
			long r = jedis.lpush(queueName.getBytes(), bos.toByteArray());
			if (r > 0) {
				result = true;
			}
		} catch (Exception e) {
			log_info.error("addQueueElement,key=[" + queueName+ "]", e);
		} finally {
			returnJedis(jedis);
		}

		return result;
	}

	public <T> boolean addQueueElements(String queueName, List<T> elements) {
		boolean result = false;
		if (elements != null && elements.size() > 0) {
			byte[][] total = new byte[elements.size()][];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			for (int i = 0; i < elements.size(); i++) {
				Object obj = elements.get(i);
				try {
					new ObjectOutputStream(bos).writeObject(obj);
					byte[] t = bos.toByteArray();
					bos.flush();
					bos.reset();
					total[i] = t;
				} catch (IOException e) {
					log_info.error("addQueueElements,key=[" + queueName + "]",
							e);
				}
			}
			Jedis jedis = null;
			try {
				jedis = getJedis();
				long r = jedis.lpush(queueName.getBytes(), total);
				if (r > 0) {
					result = true;
				}
			} catch (Exception e) {
				log_info.error("addQueueElements,key=[" + queueName
						+ "]", e);
			} finally {
				returnJedis(jedis);
			}
		}
		return result;
	}
	
	public <T> boolean addQueueElements(String queueName, T element) {
		boolean result = false;
		if (element != null ) {
			byte[][] total = new byte[1][];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Object obj = element;
			try {
				new ObjectOutputStream(bos).writeObject(obj);
				byte[] t = bos.toByteArray();
				bos.flush();
				bos.reset();
				total[0] = t;
			} catch (IOException e) {
				log_info.error("addQueueElements,key=[" + queueName + "]",e);
			}
			Jedis jedis = null;
			try {
				jedis = getJedis();
				long r = jedis.lpush(queueName.getBytes(), total);
				if (r > 0) {
					result = true;
				}
			} catch (Exception e) {
				log_info.error("addQueueElements,key=[" + queueName+ "]", e);
			} finally {
				returnJedis(jedis);
			}
		}
		return result;
	}

	public <T> boolean addHashQueueElements(String queueName,
			Map<String, T> hashValue) {
		boolean result = false;
		if (queueName != null && hashValue != null && hashValue.size() > 0) {
			Map<byte[], byte[]> tmp = new HashMap<byte[], byte[]>();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			for (String key : hashValue.keySet()) {
				try {
					new ObjectOutputStream(bos).writeObject(hashValue.get(key));
					byte[] t = bos.toByteArray();
					bos.flush();
					bos.reset();
					tmp.put(key.getBytes(), t);
				} catch (IOException e) {
					log_info.error("",e);
				}
			}
			Jedis jedis = null;
			try {
				jedis = getJedis();
				String r = jedis.hmset(queueName.getBytes(), tmp);
				if (r != null && r.equalsIgnoreCase("OK")) {
					result = true;
				}
			} catch (Exception e) {
				log_info.error("addHashQueueElements,key=[" + queueName
						+ "]", e);
			} finally {
				returnJedis(jedis);
			}
		}
		return result;
	}

	
	public boolean delHashQueueElements(String queueName, Set<String> fields) {
		boolean result = false;
		if (queueName != null && fields != null && fields.size() > 0) {
			byte[][] tmp = new byte[fields.size()][];
			int i = 0;
			for (String each : fields) {
				tmp[i] = each.getBytes();
				i++;
			}
			Jedis jedis = null;
			try {
				jedis = getJedis();
				long r = jedis.hdel(queueName.getBytes(), tmp);
				if (r == fields.size()) {
					result = true;
				}
			} catch (Exception e) {
				log_info.error("delHashQueueElements,key=[" + queueName
						+ "]", e);
			} finally {
				returnJedis(jedis);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	
	public <T> List<T> getHashQueueValues(String queueName, Set<String> fields) {
		List<T> result = new ArrayList<T>();
		if (queueName != null && fields != null && fields.size() > 0) {
			byte[][] tmp = new byte[fields.size()][];
			int i = 0;
			for (String each : fields) {
				tmp[i] = each.getBytes();
				i++;
			}
			Jedis jedis = null;
			try {
				jedis = getJedis();
				List<byte[]> r = jedis.hmget(queueName.getBytes(), tmp);
				if (r != null) {
					for (byte[] each : r) {
						if (null == each)continue;
						result.add((T) Utils.toObject(each));
					}
				}
			} catch (Exception e) {
				log_info.error("getHashQueueValues,key=[" + queueName
						+ "]", e);
			} finally {
				returnJedis(jedis);
			}
		}
		return result;
	}

	
	public long getQueueSize(String queueName) throws NoSuchQueueException {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.llen(queueName);
		} catch (Exception e) {
			log_info.error(
					"getQueueSize,key=[" + queueName + "]", e);
		} finally {
			returnJedis(jedis);
		}
		return result;
	}

	
	public List<Object> chargingByBatch(
			Collection<? extends Chargable> chargeList) {
		List<Object> result = null;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			Pipeline pipeline = jedis.pipelined();
			for (Chargable each : chargeList) {
				pipeline.hincrByFloat("balance:" + each.getUser_id(),
						"cur_balance", each.getCharge_sum() * -1.0);
			}
			result = pipeline.syncAndReturnAll();
		} catch (Exception e) {
			log_info.error("chargingByBatch", e);
		} finally {
			returnJedis(jedis);
		}
		return result;
	}

	
	public List<Object> rechargingByBatch(
			Collection<? extends Chargable> rechargeList) {
		List<Object> result = null;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			Pipeline pipeline = jedis.pipelined();
			for (Chargable each : rechargeList) {
				pipeline.hincrByFloat("balance:" + each.getUser_id(),
						"cur_balance", each.getCharge_sum());
			}
			result = pipeline.syncAndReturnAll();
		} catch (Exception e) {
			log_info.error("rechargingByBatch", e);
		} finally {
			returnJedis(jedis);
		}
		return result;
	}

	
	public Object getCacheData(String key) {
		Object result = null;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			byte[] tmp = jedis.get(key.getBytes());
			if (tmp != null) {
				try {
					System.out.println("temp:"+tmp);
					result = (Object) new ObjectInputStream( new ByteArrayInputStream(tmp)).readObject();
				} catch (IOException e) {
					log_info.error("",e);
				} catch (ClassNotFoundException e) {
					log_info.error("是我是我",e);
				}
			}
		} catch (Exception e) {
			log_info.error( "getCacheData,key=[" + key + "]", e);
		} finally {
			returnJedis(jedis);
		}
		return result;
	}

	
	public List<Object> getCacheData(List<String> keys) {
		List<Object> result = new ArrayList<Object>();
		Jedis jedis = null;

		byte[][] total = new byte[keys.size()][];
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			total[i] = key.getBytes();
		}
		try {
			jedis = getJedis();
			List<byte[]> tmp = jedis.mget(total);
			for (byte[] each : tmp) {
				ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream(each));
				Object tmpObj = ois.readObject();
				result.add(tmpObj);
				ois.close();
			}
		} catch (Exception e) {
			log_info.error( "getCacheData,key=[" + keys + "]", e);
		} finally {
			returnJedis(jedis);
		}
		return result;
	}

	
	public boolean putCacheData(Map<String, ?> cacheMap) {
		boolean result = false;
		Jedis jedis = null;

		byte[][] total = new byte[cacheMap.size() * 2][];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		int i = 0;
		for (String eachKey : cacheMap.keySet()) {
			Object cache = cacheMap.get(eachKey);
			try {
				new ObjectOutputStream(bos).writeObject(cache);
				byte[] t = bos.toByteArray();
				bos.flush();
				bos.reset();
				total[i++] = eachKey.getBytes();
				total[i++] = t;
			} catch (IOException e) {
				log_info.error("putCacheData", e);
			}
		}
		try {
			jedis = getJedis();
			String resp = jedis.mset(total);
			if ("OK".equals(resp)) {
				result = true;
			}
		} catch (Exception e) {
			log_info.error("putCacheData,jedis_info", e);
		} finally {
			returnJedis(jedis);
		}
		return result;
	}

	
	public void putRWCacheDataInStringMode(Map<String, ?> cacheMap) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			for (String eachKey : cacheMap.keySet()) {
				Object cache = cacheMap.get(eachKey);
				Field[] fields = cache.getClass().getDeclaredFields();
				Map<String, String> hash = new HashMap<String, String>();

				for (Field eachField : fields) {
					String strValue;
					try {
						strValue = cache .getClass() .getMethod( "get" + Utils.FirstUpperCase(eachField .getName()), null) .invoke(cache, null).toString();
						hash.put(eachField.getName(), strValue);
					} catch (Exception e) {
						log_info.error("putRWCacheDataInStringMode", e);
					}
				}
				jedis.hmset(eachKey, hash);
				jedis.sadd("HashCacheKeysSet", eachKey);
			}
		} catch (Exception e) {
			log_info.error(
					"putRWCacheDataInStringMode", e);
		} finally {
			returnJedis(jedis);
		}
	}

	
	public void putRWCacheData(Map<String, ?> cacheMap) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			for (String eachKey : cacheMap.keySet()) {
				Object cache = cacheMap.get(eachKey);
				Field[] fields = cache.getClass().getDeclaredFields();
				Map<byte[], byte[]> hash = new HashMap<byte[], byte[]>();

				for (Field eachField : fields) {
					Object objValue;
					byte[] value;
					try {
						objValue = cache.getClass().getMethod( "get" + Utils.FirstUpperCase(eachField .getName()), null) .invoke(cache, null);
						value = Utils.toByteArray(objValue);
						hash.put(eachField.getName().getBytes(), value);
					} catch (Exception e) {
						log_info.error("putRWCacheData", e);
					}
				}
				jedis.hmset(eachKey.getBytes(), hash);
				jedis.sadd("HashCacheKeysSet", eachKey);
			}
		} catch (Exception e) {
			log_info.error("putRWCacheData", e);
		} finally {
			returnJedis(jedis);
		}
	}

	
	public <T> List<T> getRWCacheInStringMode(String keyPattern, Class<T> clazz) {
		List<T> result = new ArrayList<T>();
		Jedis jedis = null;
		try {
			jedis = getJedis();
			ScanParams sp = new ScanParams();
			sp.match(keyPattern);
			sp.count(COUNT);
			ScanResult<String> rs = jedis.sscan("HashCacheKeysSet", "0", sp);
			List<String> keySet = rs.getResult();

			Pipeline pipeline = jedis.pipelined();

			for (String each : keySet) {
				pipeline.hgetAll(each);
			}
			List<Object> tmpList = pipeline.syncAndReturnAll();
			for (Object each : tmpList) {
				@SuppressWarnings("unchecked")
				Map<String, String> tmp = (Map<String, String>) each;
				T o = clazz.newInstance();
				for (String field : tmp.keySet()) {
					String str = tmp.get(field);
					Method m = clazz.getMethod(
							"set" + Utils.FirstUpperCase(new String(field)),
							String.class);
					m.invoke(o, str);
				}
				result.add(o);
			}
		} catch (Exception e) {
			log_info.error("getRWCacheInStringMode,key=[" + keyPattern
					+ "]", e);
		} finally {
			returnJedis(jedis);
		}
		return result;
	}

	
	public <T> List<T> getRWCacheInStringModeByKey(String keyPattern,
			Class<T> clazz) {
		List<T> result = new ArrayList<T>();
		Jedis jedis = null;
		try {
			jedis = getJedis();
			Set<String> keySet = jedis.keys(keyPattern);

			Pipeline pipeline = jedis.pipelined();

			for (String each : keySet) {
				pipeline.hgetAll(each);
			}
			List<Object> tmpList = pipeline.syncAndReturnAll();
			for (Object each : tmpList) {
				@SuppressWarnings("unchecked")
				Map<String, String> tmp = (Map<String, String>) each;
				T o = clazz.newInstance();
				for (String field : tmp.keySet()) {
					String str = tmp.get(field);
					Method m = clazz.getMethod(
							"set" + Utils.FirstUpperCase(new String(field)),
							String.class);
					m.invoke(o, str);
				}
				result.add(o);
			}
		} catch (Exception e) {
			log_info.error("getRWCacheInStringModeByKey,key=[" + keyPattern
					+ "]", e);
		} finally {
			returnJedis(jedis);
		}
		return result;
	}

	
	public <T> List<T> getRWCache(String keyPattern, Class<T> clazz) {
		List<T> result = new ArrayList<T>();
		Jedis jedis = null;
		try {
			jedis = getJedis();

			ScanParams sp = new ScanParams();
			sp.match(keyPattern);
			sp.count(COUNT);
			ScanResult<String> rs = jedis.sscan("HashCacheKeysSet", "0", sp);
			List<String> keySet = rs.getResult();

			Pipeline pipeline = jedis.pipelined();

			for (String each : keySet) {
				pipeline.hgetAll(each.getBytes());
			}
			List<Object> tmpList = pipeline.syncAndReturnAll();
			for (Object each : tmpList) {
				@SuppressWarnings("unchecked")
				Map<byte[], byte[]> tmp = (Map<byte[], byte[]>) each;
				T o = clazz.newInstance();
				Method[] ms = clazz.getMethods();
				for (byte[] field : tmp.keySet()) {

					Object obj = Utils.toObject(tmp.get(field));
					for (Method m : ms) {
						if (m.getName()
								.equals("set"
										+ Utils.FirstUpperCase(new String(field)))) {
							m.invoke(o, obj);
						}
					}
				}
				result.add(o);
			}
		} catch (Exception e) {
			log_info.error(
					"getRWCache,key=[" + keyPattern + "]", e);
		} finally {
			returnJedis(jedis);
		}
		return result;
	}

	
	public <T> List<T> getRWCacheByKey(String keyPattern, Class<T> clazz) {
		List<T> result = new ArrayList<T>();
		Jedis jedis = null;
		try {
			jedis = getJedis();
			Set<String> keySet = jedis.keys(keyPattern);
			Pipeline pipeline = jedis.pipelined();

			for (String each : keySet) {
				pipeline.hgetAll(each.getBytes());
			}
			List<Object> tmpList = pipeline.syncAndReturnAll();
			for (Object each : tmpList) {
				@SuppressWarnings("unchecked")
				Map<byte[], byte[]> tmp = (Map<byte[], byte[]>) each;
				T o = clazz.newInstance();
				Method[] ms = clazz.getMethods();
				for (byte[] field : tmp.keySet()) {

					Object obj = Utils.toObject(tmp.get(field));
					for (Method m : ms) {
						if (m.getName()
								.equals("set"
										+ Utils.FirstUpperCase(new String(field)))) {
							m.invoke(o, obj);
						}
					}
				}
				result.add(o);
			}
		} catch (Exception e) {
			log_info.error("getRWCacheByKey,key=[" + keyPattern
					+ "]", e);
		} finally {
			returnJedis(jedis);
		}
		return result;
	}

	
	public void updateCachesValue(String[] changeFields,
			Map<String, ?> currentMap) {
		Jedis jedis = null;
		if (changeFields != null && currentMap != null) {
			try {
				jedis = getJedis();
				Pipeline pipeline = jedis.pipelined();

				for (String key : currentMap.keySet()) {
					Object obj = currentMap.get(key);
					for (String field : changeFields) {
						Object changedValue = obj.getClass().getMethod("get" + Utils.FirstUpperCase(field),null).invoke(obj, null);
						pipeline.hset(key.getBytes(), field.getBytes(),Utils.toByteArray(changedValue));
					}
				}
				pipeline.sync();
			} catch (Exception e) {
				log_info.error("updateCachesValue", e);
			} finally {
				returnJedis(jedis);
			}
		}
	}

	
	public void updateCachesValueInStringMode(String[] changeFields,
			Map<String, ?> currentMap) {
		Jedis jedis = null;
		if (changeFields != null && currentMap != null) {
			try {
				jedis = getJedis();
				Pipeline pipeline = jedis.pipelined();
				for (String key : currentMap.keySet()) {
					Object obj = currentMap.get(key);
					for (String field : changeFields) {
						Object changedValue = obj.getClass() .getMethod("get" + Utils.FirstUpperCase(field), null).invoke(obj, null);
						pipeline.hset(key, field, changedValue.toString());
					}
				}
				pipeline.sync();
			} catch (Exception e) {
				log_info.error(
						"updateCachesValueInStringMode", e);
			} finally {
				returnJedis(jedis);
			}
		}

	}

	
	public void removeCacheKey(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.srem("HashCacheKeysSet", key);
			jedis.del(key);
		} catch (Exception e) {
			log_info.error(
					"removeCacheKey,key=[" + key + "]", e);
		} finally {
			returnJedis(jedis);
		}
	}

	
	public <T> boolean addStringValue(String prefix, Map<String, T> keyValues) {
		boolean result = false;
		if (keyValues != null && keyValues.size() > 0) {
			Jedis jedis = null;
			try {
				jedis = getJedis();
				List<byte[]> tmpList = new ArrayList<byte[]>();
				for (String each : keyValues.keySet()) {
					T t = keyValues.get(each);
					tmpList.add((prefix + each).getBytes());
					tmpList.add(Utils.toByteArray(t));
				}
				byte[][] bytes = tmpList.toArray(new byte[tmpList.size()][]);
				String response = jedis.mset(bytes);
				if ("OK".equals(response)) {
					result = true;
				}
			} catch (Exception e) {
				log_info.error("addStringValue,key=[" + prefix
						+ "]", e);
			} finally {
				returnJedis(jedis);
			}
		}
		return result;
	}

	
	public List<Object> getStringTypeValues(List<String> keys) {
		List<Object> result = new ArrayList<Object>();
		Jedis jedis = null;
		if (keys != null && keys.size() > 0) {
			try {
				byte[][] byteKeys = new byte[keys.size()][];
				jedis = getJedis();
				for (int i = 0; i < keys.size(); i++) {
					String key = keys.get(i);
					byteKeys[i] = key.getBytes();
				}

				List<byte[]> byteValues = jedis.mget(byteKeys);
				for (byte[] eachValue : byteValues) {
					Object obj = Utils.toObject(eachValue);
					result.add(obj);
				}
			} catch (Exception e) {
				log_info.error("getStringTypeValues,key=[" + keys
						+ "]", e);
			} finally {
				returnJedis(jedis);
			}
		}
		return result;
	}
}
