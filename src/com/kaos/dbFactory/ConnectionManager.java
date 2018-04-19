package com.kaos.dbFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import com.kaos.dbUtils.DBConnection;

abstract class ConnectionManager extends Thread{
	protected long SLEEP_TIMER = 60000;
	protected List<DBConnection> livePool;
	protected List<DBConnection> deadPool;
	protected static final String KEY_SEPARATOR = ":";
	protected int MAX_POOL_SIZE=20;
	protected static int INIT_POOL_SIZE=5;
	protected Logger log=getConsoleLogger(ConnectionManager.class);
	protected boolean ENFORCE_MAX_POOL=false;
	protected Recycler recycler;
	protected String dbType;
	protected ConnectionManager(String dbType){
		this.dbType=dbType;
	}
	private static Logger getConsoleLogger(@SuppressWarnings("rawtypes") Class clazz) {
		Logger log=Logger.getLogger(clazz);
		ConsoleAppender appender=new ConsoleAppender(new PatternLayout("%-5p [%-15.15t] %d{ISO8601} : %C{1}.%M :%L: %m%n"));
		appender.activateOptions();
		log.addAppender(appender);
		return log;
	}
	public void run() {
		log.info("Starting FactoryManager thread");
		while(true){
			try {
				recycler=new Recycler(deadPool, livePool, log, MAX_POOL_SIZE);
				recycler.clear();
				log.debug("Sleeping FactoryManager for : "+SLEEP_TIMER/1000+" secs");
				sleep(SLEEP_TIMER);
			} catch (InterruptedException e) {
				log.error("Error when managing recycler threads",e);
			}
		}
	}
	protected void shutdownPool(){
		log.info("Shutting down pool");
		freeList(livePool);
		freeList(deadPool);
	}
	private void freeList(List<DBConnection> list){
		for(DBConnection dbc : list){
			try {
				dbc.freeResources();
			} catch (Exception e) {
				log.error("Error when freeing resources", e);
			}
			list.remove(dbc);
		}
		list.clear();
	}
	protected synchronized DBConnection getConnection(String... s){
		log.info("JDBCConnection pool management");
		DBConnection freeObject=null;
		String key=KEY_SEPARATOR;
		for(String k : s){
			key=key+k+KEY_SEPARATOR;
		}
		if(livePool!=null){
			log.debug("Live connections pool size : "+livePool.size());
			for(int i=0; i<livePool.size(); i++){
				try{
					if((livePool.get(i)).isFree()){
						log.info("Found free connection in pool");
						if((livePool.get(i)).isLive()){
							log.info("Connection is live, allocating to current request");
							freeObject=livePool.get(i);
							freeObject.setFree(false);
							log.debug("Pooled object : "+freeObject);
							log.info("JDBCConnection pooling completed");
							return freeObject;
						}
						else{
							log.info("Connection is not live, moving to dead pool");
							deadPool.add(livePool.remove(i));
						}
					}
				}
				catch (Exception e) {
					log.error("Error when checking status of connection", e);
					log.info("Removing from live pool");
					livePool.remove(i);
				}
			}
		}
		else{
			log.debug("Creating initial pool : "+key);
			log.debug("Initial Pool size : "+INIT_POOL_SIZE);
			livePool=Collections.synchronizedList(new ArrayList<DBConnection>());
			deadPool=Collections.synchronizedList(new ArrayList<DBConnection>());
			for(int i=1;i<=INIT_POOL_SIZE;i++){
				freeObject=getNewObject(s);
				freeObject.setFree(true);
				livePool.add(freeObject);
			}
			try{
				freeObject.createConnection();
			}
			catch(Exception e){
				log.error("Error when creating connections for initial pool", e);
			}
			log.debug("Pooled object : "+freeObject);
			log.info("JDBCConnection pooling completed");
			return freeObject;
		}
		if(ENFORCE_MAX_POOL && livePool.size()>=MAX_POOL_SIZE){
			log.error("Pool has reached max capacity, could not allocate new resource since all are busy");
			log.error("Live pool size : "+livePool.size());
		}
		else{
			log.info("Creating fresh connection");
			freeObject=getNewObject(s);
			livePool.add(freeObject);
		}
		log.debug("Pooled object : "+freeObject);
		log.info("JDBCConnection pooling completed");
		return freeObject;
	}
	@Override
	protected void finalize() throws Throwable {
		shutdownPool();
		super.finalize();
	}
	
	protected abstract DBConnection getNewObject(String... strings);
}