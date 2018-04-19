package com.kaos.dbFactory;

import com.kaos.dbUtils.DBConnection;

class ConnectionFactory {
	private ConnectionManager manager;
	private static final String FACTORY_NAME="com.kaos.dbFactory";
	@SuppressWarnings("deprecation")
	protected ConnectionFactory(String dbName, String dbType){
		
		for(Thread t : Thread.getAllStackTraces().keySet()){
			if(t.getName().equals(FACTORY_NAME+dbName)){
				/*if(dbType.equals("JDBC")){
					manager=(JdbcManager)t;
				}
				else if(dbType.equals("DataSource")){
					manager=(DatasourceManager)t;
				}
				else{
					throw new RuntimeException("Invalid dbType");
				}*/
				t.stop();
			}
		}
		if(manager==null || !manager.isAlive()){
			if(dbType.equals("JDBC")){
				manager=new JdbcManager();
			}
			else if(dbType.equals("DataSource")){
				manager=new DatasourceManager();
			}
			else{
				throw new RuntimeException("Invalid dbType");
			}
			manager.setName(FACTORY_NAME+dbName);
			manager.setDaemon(true);
			manager.start();
		}
	}
	protected DBConnection getInstance(String... strings){
		return manager.getConnection(strings);
	}
	protected void shutdownPool(){
		manager.shutdownPool();
	}
	@Override
	protected void finalize() throws Throwable {
		shutdownPool();
		super.finalize();
	}
	protected ConnectionManager getConnectionManager(){
		return manager;
	}
}
