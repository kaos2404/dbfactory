package com.kaos.dbFactory;

import java.util.List;
import org.apache.log4j.Logger;
import com.kaos.dbUtils.DBConnection;

class Recycler{
	private List<DBConnection> deadList;
	private List<DBConnection> liveList;
	private Logger log;
	private int MAX_POOL_SIZE;
	protected Recycler(List<DBConnection> deadList, List<DBConnection> liveList, Logger log, int MAX_POOL_SIZE){
		this.deadList=deadList;
		this.liveList=liveList;
		this.log=log;
		this.MAX_POOL_SIZE=MAX_POOL_SIZE;
	}
	protected void clear(){
		int count=0;
		for(int i=0; i < liveList.size() && liveList!=null; i++){
			try{
				if(liveList.get(i).isFree() && !liveList.get(i).isLive()){
					count++;
					deadList.add(liveList.remove(i));
				}
			}
			catch(Exception e){
				log.error("Error when checking on closed connection", e);
				liveList.remove(i);
			}
		}
		log.debug("Flushed "+count+" dead connections from live map");
		count=0;
			for(int i=0; i < deadList.size() && liveList!=null; i++){
				try {
					if(liveList.size()<MAX_POOL_SIZE){
						deadList.get(i).createConnection();
						liveList.add(deadList.remove(i));
						count++;
					}
					else{
						deadList.get(i).freeResources();
						deadList.get(i).closeConnection();
						deadList.remove(i);
					}
				} catch (Exception e) {
					deadList.remove(i);
					log.error("Error when reconnecting to database ", e);
				}
			}
		log.debug("Reconnected "+count+" dead connections");
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
