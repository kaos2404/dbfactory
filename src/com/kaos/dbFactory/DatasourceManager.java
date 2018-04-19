package com.kaos.dbFactory;

import com.kaos.dbUtils.DBConnection;
import com.kaos.dbUtils.Datasource;

class DatasourceManager extends ConnectionManager{
	protected DatasourceManager(){
		super("DataSource");
	}
	protected DBConnection getNewObject(String... s) {
		return new Datasource(s[0], s[1], s[2]);
	}
}
