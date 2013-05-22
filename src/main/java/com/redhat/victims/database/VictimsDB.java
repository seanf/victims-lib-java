package com.redhat.victims.database;

/*
 * #%L
 * This file is part of victims-lib.
 * %%
 * Copyright (C) 2013 The Victims Project
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.io.IOException;
import java.sql.SQLException;

import com.redhat.victims.VictimsConfig;
import com.redhat.victims.VictimsException;

/**
 * A class providing easy instantiation of DB implementation based on the
 * configured driver.
 * 
 * @author abn
 * 
 */
public class VictimsDB {

	/**
	 * The default driver class to use.
	 * 
	 * @return
	 */
	public static String defaultDriver() {
		return VictimsH2DB.driver();
	}

	/**
	 * The default url for the default driver.
	 * 
	 * @return
	 */
	public static String defaultURL() {
		return VictimsH2DB.defaultURL();
	}

	/**
	 * Fetches an instance implementing {@link VictimsDBInterface} using the
	 * configured driver.
	 * 
	 * @return A {@link VictimsDBInterface} implementation.
	 * @throws VictimsException
	 */
	public static VictimsDBInterface db() throws VictimsException {
		Throwable throwable = null;
		try {
			return (VictimsDBInterface) new VictimsSqlDB(
					VictimsConfig.dbDriver(), VictimsConfig.dbUrl(),
					VictimsConfig.dbCreate());
		} catch (SQLException e) {
			throwable = e;
		} catch (ClassNotFoundException e) {
			throwable = e;
		} catch (IOException e) {
			throwable = e;
		}
		throw new VictimsException(
				"Failed to get a Victims Database instance.", throwable);
	}

}
