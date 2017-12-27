package sunnyday.adapter.redis;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.Jedis;
import sunnyday.adapter.redis.exception.NoSuchQueueException;

public interface DCAdapter {
	
	public Jedis getJedis();
	
	public void returnJedis(Jedis jedis);
	
	/**
	 * 非阻塞的获取数据中心指定队列中单个元素
	 * @param String queueName （队列名 ）
	 * @return Object 如果队列空返回null
	 * throws NoSuchQueueExcepiton 如果指定队列不存在，抛出NoSuchQueueException
	 */
	public <T> T getQueueElement(String queueName) throws NoSuchQueueException;

	/**
	 * 非阻塞的获取数据中心指定队列中多个元素
	 * @param String queueName （队列名 ） int number（获取数量）  
	 * @return List<Object> 如果队列空返回空队列
	 * throws NoSuchQueueExcepiton 如果指定队列不存在，抛出NoSuchQueueException
	 */
	public List<Object> getQueueElements(String queueName, int number) throws NoSuchQueueException;
	
	/**
	 * 向HashQueue中添加元素，需要指明在hash表中的field名。
	 * @param queueName
	 * @param field
	 * @param obj
	 * return true 如果一切正常，异常情况下返回false
	 */
	public <T> boolean addHashQueueElements(String queueName, Map<String, T> hashValue);
	
	/**
	 * 删除hash表中的元素
	 * @param queueName
	 * @param field
	 * @return
	 */
	public boolean delHashQueueElements(String queueName, Set<String> fields);
	
	/**
	 * 获取hash表中对应键的值
	 * @param queueName
	 * @param fields
	 * @return
	 */
	public <T> List<T> getHashQueueValues(String queueName, Set<String> fields);
		
	/**
	 * 向数据中心添加string类型的缓存数据
	 * @param String prefix(前缀名), Map<String, T> keyValues (实际key-value) 
	 * @return boolean 添加成功返回true，失败返回false；
	 */
	public <T> boolean addStringValue(String prefix, Map<String, T> keyValues);
	
	/**
	 * 获取数据中心String类型的多个元素
	 * @param keys Nosql 数据库中key的集合 
	 * @return List<Object> 如果队列空返回null
	 */
	public List<Object> getStringTypeValues(List<String> keys);
	
	/**
	 * 向指定队列中添加单个元素
	 * @param queueName 目标队列名称
	 * @param element 要添加的元素
	 * @return true = 添加成功；false = 添加失败，可能是队列过大或数据中心暂挂导致
	 */
	public <T> boolean addQueueElement(String queueName, T element);

	/**
	 * 向指定队列中添加多个元素
	 * @param queueName 目标队列名称
	 * @param elements 要添加的集合，以list存放
	 * @return true = 添加成功；false = 添加失败，可能是队列过大或数据中心暂挂导致
	 */
	public <T> boolean addQueueElements(String queueName, List<T> elements);
	
	/**
	 * 向指定队列中添加多个元素
	 * @param queueName 目标队列名称
	 * @param element 要添加单个对象，以list存放
	 * @return true = 添加成功；false = 添加失败，可能是队列过大或数据中心暂挂导致
	 */
	public <T> boolean addQueueElements(String queueName, T element);
	
	/**
	 * 
	 * @param queueName
	 * @return 队列中的元素数
	 * @throws NoSuchQueueExcepiton
	 */
	public long getQueueSize(String queueName) throws NoSuchQueueException;
	
	/**
	 * 计费接口，对传入的list中的信息轮询扣费
	 * 
	 * @return 操作完成后的账户余额list
	 */
	public List<Object> chargingByBatch(Collection<?> chargeList);
	
	/**
	 * 充值接口，对传入的list中的账户轮询充值
	 * 
	 * @param rechargeList
	 * @return 充值完成后的账户当前余额
	 */
	public List<Object> rechargingByBatch(Collection<? extends Chargable> rechargeList);
	/**
	 * 到数据中心取数据库对应的缓存信息
	 * @param key
	 * @return 缓存对象，可能是map，list，set等
	 */
	public Object getCacheData(String key);
	
	/**
	 * 批量到数据中心取数据库对应的缓存信息
	 * @param keys
	 * @return 与keys排序相同的缓存对象
	 */
	public List<Object> getCacheData(List<String> keys);
	
	/**
	 * 将缓存数据放入数据中心
	 * @param cacheMap
	 * @return true:添加缓存成功，false:添加缓存失败
	 */
	public boolean putCacheData(Map<String, ?> cacheMap);
	
	/**
	 * 将可读写的缓存写入数据中心以byte数组的形式
	 * @param cacheMap
	 */
	public void putRWCacheData(Map<String, ?> cacheMap);
	
	/**
	 * 将可读写的缓存写入数据中心以String的形式
	 * @param cacheMap
	 */
	public void putRWCacheDataInStringMode(Map<String, ?> cacheMap);
	/**
	 * 从数据中获取多个可读写的缓存对象，以list形式返回
	 * @param <T>
	 * @param keyPattern 数据中心的key 的pattern
	 * @param clazz 该缓存对应对象的类
	 * @return 与keyPattern匹配的key对应的缓存组装成的list
	 */
	public <T> List<T> getRWCache(String keyPattern, Class<T> clazz);
	
	public <T> List<T> getRWCacheByKey(String keyPattern, Class<T> clazz);
	
	/**
	 * 从数据中获取多个可读写的缓存对象，以list形式返回
	 * @param <T>
	 * @param keyPattern 数据中心的key 的pattern
	 * @param clazz 该缓存对应对象的类
	 * @return 与keyPattern匹配的key对应的缓存组装成的list
	 */
	public <T> List<T> getRWCacheInStringMode(String keyPattern, Class<T> clazz);
	
	public <T> List<T> getRWCacheInStringModeByKey(String keyPattern, Class<T> clazz);
	/**
	 * 更新可写缓存的字段值
	 * @param changeFields 要更新的字段组
	 * @param currentMap 要更新的缓存的key和对应的对象
	 */
	public void updateCachesValue(String[] changeFields, Map<String, ?> currentMap);
	
	/**
	 * 更新可写缓存的字段值, 以str方式
	 *  * @param changeFields 要更新的字段组
	 * @param currentMap 要更新的缓存的key和对应的对象
	 */
	public void updateCachesValueInStringMode(String[] changeFields, Map<String, ?> currentMap);
	
	/**
	 * 移除数据中心中该key和值，以及所有对该key的引用
	 * @param key
	 */
	public void removeCacheKey(String key);
}
