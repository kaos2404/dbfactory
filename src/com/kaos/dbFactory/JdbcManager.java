package com.kaos.dbFactory;

import com.kaos.dbUtils.DBConnection;
import com.kaos.dbUtils.Jdbc;

class JdbcManager extends ConnectionManager{
	protected  JdbcManager(){
		super("JDBC");
	}
	protected DBConnection getNewObject(String... s) {
		return new Jdbc(s[0], s[1], s[2], s[3], s[4]);
	}
}
