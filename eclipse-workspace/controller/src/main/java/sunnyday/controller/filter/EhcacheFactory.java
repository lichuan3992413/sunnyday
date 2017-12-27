package sunnyday.controller.filter;

import net.sf.ehcache.Cache;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class EhcacheFactory {
	private static int maxElementsInMemory = 100000; 
	private static int maxElementsOnDisk = 5000000;
	private static String diskStorePath = "";//need setter to inject
	private static boolean overflowToDisk = true;
	private static boolean eternal = false;
	private static MemoryStoreEvictionPolicy memoryStoreEvictionPolicy = MemoryStoreEvictionPolicy.LFU;
	private static int timeToLiveSeconds = 0;
	private static int timeToIdleSeconds = 0;
	private static boolean diskPersistent = true;
	private static RegisteredEventListeners registeredEventListeners = null;
	private static BootstrapCacheLoader bootstrapCacheLoader = null;
	private static int diskExpiryThreadIntervalSeconds = 300;
	private static int diskSpoolBufferSizeMB = 20;
	
	public static Cache newCache(int index){
		return newCache(String.valueOf(index));
	}
	
	public static Cache newCache(String cacheName){
		Cache cache = new Cache(cacheName, maxElementsInMemory, memoryStoreEvictionPolicy, overflowToDisk, diskStorePath, eternal, timeToLiveSeconds, timeToIdleSeconds, diskPersistent, diskExpiryThreadIntervalSeconds, registeredEventListeners, bootstrapCacheLoader, maxElementsOnDisk, diskSpoolBufferSizeMB);
		return cache;
	}
}
