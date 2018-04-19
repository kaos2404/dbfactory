package com.kaos.dbFactory;

import org.apache.log4j.Logger;
import com.kaos.dbUtils.DBConnection;
import com.kaos.dbUtils.Datasource;
import com.kaos.dbUtils.Jdbc;

/**
 * Class to access and manage the pooled connections
 * @author Kaos
 * @version v3.0
 * @since 14 April 2018
 */
public class DBFactory {
	/**
	 * This method is called internally in DBUtils to access the pool 
	 */
	public static Jdbc getJDBCInstance(String IP, String USERNAME, String PASSWORD, String URL, String JDBC_CLASS){
		return FactoryManager.getJDBCInstance(IP, USERNAME, PASSWORD, URL, JDBC_CLASS);
	}
	/**
	 * This method is called internally in DBUtils to access the pool 
	 */
	public static Datasource getDatasourceInstance(String SERVER_IP, String JNDI_NAME, String CONTEXT_FACTORY){
		return FactoryManager.getDatasourceInstance(SERVER_IP, JNDI_NAME, CONTEXT_FACTORY);
	}
	/**
	 * Get the sleep time for the factory associated with the DataSourceConnection or JDBCConnection object
	 * @param connection The DataSourceConnection or JDBCConnection object for which the Factory needs to be accessed
	 * @return The amount of time in millisecs for which the recycling is paused
	 */
	public static long getSleepTimer(DBConnection connection) {
		return FactoryManager.getSleepTimer(connection);
	}
	/**
	 * Set the sleep time for the factory associated with the DataSourceConnection or JDBCConnection object
	 * @param connection The DataSourceConnection or JDBCConnection object for which the Factory needs to be accessed
	 * @param sleepTimer The amount of time in millisecs for which the recycling is to be paused
	 */
	public static void setSleepTimer(DBConnection connection, long sleepTimer) {
		FactoryManager.setSleepTimer(connection, sleepTimer);
	}
	/**
	 * Get the maximum number of live connections held in the pool
	 * @param connection The DataSourceConnection or JDBCConnection object for which the Factory needs to be accessed
	 * @return The value in Integer of the maximum pooled connections are held
	 */
	public static int getMaxPoolSize(DBConnection connection) {
		return FactoryManager.getMaxPoolSize(connection);
	}
	/**
	 * Set the maximum number of live connections to hold in the pool
	 * @param connection The DataSourceConnection or JDBCConnection object for which the Factory needs to be accessed
	 * @param maxPoolSize The value in Integer of the maximum pooled connections to be held
	 */
	public static void setMaxPoolSize(DBConnection connection, int maxPoolSize) {
		FactoryManager.setMaxPoolSize(connection, maxPoolSize);
	}
	/**
	 * Set the initial pool size capacity for all new pools that are generated
	 * @param initPoolSize
	 */
	public static void setInitPoolSize(int initPoolSize) {
		ConnectionManager.INIT_POOL_SIZE=initPoolSize;
	}
	/**
	 * Get the maximum pool size enforcement status
	 * @param connection The DataSourceConnection or JDBCConnection object for which the Factory needs to be accessed
	 * @return true if maximum pool size is set on, false if not
	 */
	public static boolean isEnforceMaxPool(DBConnection connection) {
		return FactoryManager.isEnforceMaxPool(connection);
	}
	/**
	 * Sets the maximum pool size enforcement
	 * @param connection The DataSourceConnection or JDBCConnection object for which the Factory needs to be accessed
	 * @param enforceMaxPool If true, new connections will not be made if the pool size exceeds the maximum pool size that was set. If false, new connections will be made, however dead connections will not be reconncted if pool size exceeds the maximum pool size value.
	 */
	public static void setEnforceMaxPool(DBConnection connection, boolean enforceMaxPool) {
		FactoryManager.setEnforceMaxPool(connection, enforceMaxPool);
	}
	/**
	 * Set the log4j object to log Factory events
	 * @param connection The DataSourceConnection or JDBCConnection object for which the Factory needs to be accessed
	 * @param log The logging object to which events would be logged
	 */
	public static void setFactoryLogger(DBConnection connection, Logger log) {
		FactoryManager.setFactoryLogger(connection, log);
	}
}
