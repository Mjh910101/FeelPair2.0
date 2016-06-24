package com.feelpair.xy.dao;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DaoConfig;

public class DBHandler {

	public static DbUtils getDbUtils(Context context) {
		DaoConfig config = new DaoConfig(context);
		config.setDbName("DB_FEEL_PAIR"); // db名
		config.setDbVersion(1); // db版本
		DbUtils db = DbUtils.create(config);
		return db;
	}
}
