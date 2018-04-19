package com.kaos.dbFactory;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.kaos.dbUtils.DBConnection;
import com.kaos.dbUtils.DataSourceConnection;
import com.kaos.dbUtils.Datasource;
import com.kaos.dbUtils.JDBCConnection;
import com.kaos.dbUtils.Jdbc;

class FactoryManager {
	private static final HashMap<String, ConnectionFactory> manager=new HashMap<String, ConnectionFactory>();
	private static ConnectionFactory lookup(String dbName, String dbType){
		if(!manager.containsKey(dbName)){
			manager.put(dbName, new ConnectionFactory(dbName, dbType));
		}
		return manager.get(dbName);
	}
	protected static Jdbc getJDBCInstance(String IP, String USERNAME, String PASSWORD, String URL, String JDBC_CLASS){
		String key=ConnectionManager.KEY_SEPARATOR+IP+ConnectionManager.KEY_SEPARATOR+USERNAME+ConnectionManager.KEY_SEPARATOR+PASSWORD+ConnectionManager.KEY_SEPARATOR+URL+ConnectionManager.KEY_SEPARATOR+JDBC_CLASS+ConnectionManager.KEY_SEPARATOR;
		return (Jdbc)lookup(key, "JDBC").getInstance(IP, USERNAME, PASSWORD, URL, JDBC_CLASS);
	}
	protected static Datasource getDatasourceInstance(String SERVER_IP, String JNDI_NAME, String CONTEXT_FACTORY){
		String key=ConnectionManager.KEY_SEPARATOR+SERVER_IP+ConnectionManager.KEY_SEPARATOR+JNDI_NAME+ConnectionManager.KEY_SEPARATOR+CONTEXT_FACTORY+ConnectionManager.KEY_SEPARATOR;
		return (Datasource)lookup(key, "DataSource").getInstance(SERVER_IP, JNDI_NAME, CONTEXT_FACTORY);
	}
	private static String formDbName(DBConnection connection){
		String key="";
		if(connection instanceof DataSourceConnection){
			key=ConnectionManager.KEY_SEPARATOR+((DataSourceConnection) connection).SERVER_IP+ConnectionManager.KEY_SEPARATOR+((DataSourceConnection) connection).JNDI_NAME+ConnectionManager.KEY_SEPARATOR+((DataSourceConnection) connection).CONTEXT_FACTORY+ConnectionManager.KEY_SEPARATOR;
		}
		else if(connection instanceof JDBCConnection){
			key=ConnectionManager.KEY_SEPARATOR+((JDBCConnection) connection).IP+ConnectionManager.KEY_SEPARATOR+((JDBCConnection) connection).USERNAME+ConnectionManager.KEY_SEPARATOR+((JDBCConnection) connection).PASSWORD+ConnectionManager.KEY_SEPARATOR+((JDBCConnection) connection).URL+ConnectionManager.KEY_SEPARATOR+((JDBCConnection) connection).JDBC_CLASS+ConnectionManager.KEY_SEPARATOR;
		}
		else{
			throw new RuntimeException("Invalid DBConnection object");
		}
		return key;
	}
	private static String formDbType(DBConnection connection){
		String dbType="";
		if(connection instanceof DataSourceConnection){
			dbType="DataSource";
		}
		else if(connection instanceof JDBCConnection){
			dbType="JDBC";
		}
		else{
			throw new RuntimeException("Invalid DBConnection object");
		}
		return dbType;
	}
	protected static long getSleepTimer(DBConnection connection) {
		return lookup(formDbName(connection), formDbType(connection)).getConnectionManager().SLEEP_TIMER;
	}
	protected static void setSleepTimer(DBConnection connection, long sleepTimer) {
		lookup(formDbName(connection), formDbType(connection)).getConnectionManager().SLEEP_TIMER=sleepTimer;
	}
	protected static int getMaxPoolSize(DBConnection connection) {
		return lookup(formDbName(connection), formDbType(connection)).getConnectionManager().MAX_POOL_SIZE;
	}
	protected static void setMaxPoolSize(DBConnection connection, int maxPoolSize) {
		lookup(formDbName(connection), formDbType(connection)).getConnectionManager().MAX_POOL_SIZE=maxPoolSize;
	}
	protected static boolean isEnforceMaxPool(DBConnection connection) {
		return lookup(formDbName(connection), formDbType(connection)).getConnectionManager().ENFORCE_MAX_POOL;
	}
	protected static void setEnforceMaxPool(DBConnection connection, boolean enforceMaxPool) {
		lookup(formDbName(connection), formDbType(connection)).getConnectionManager().ENFORCE_MAX_POOL=enforceMaxPool;
	}
	protected static void setFactoryLogger(DBConnection connection, Logger log) {
		lookup(formDbName(connection), formDbType(connection)).getConnectionManager().log= log;
	}
}
