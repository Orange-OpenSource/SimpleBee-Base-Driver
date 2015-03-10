/*
 * Software Name : m2m.simplebee.application
 *
 * Module name: m2m.simplebee.application
 * Version: 1.0.3-SNAPSHOT
 *
 * Copyright (C) 2014 - 2015 Orange
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author(s): Gregory BONNARDEL
 */
package com.orange.otb.simplebee.simple.application;

import junit.framework.TestCase;

import com.orange.simplebee.SimpleBeeDevice;
import com.orange.simplebee.simple.application.SimpleBeeTemperatureNotification;

import fr.liglab.adele.icasa.device.simplebee.driver.TypeCode;

public class SimpleBeeTemperatureNotificationTest extends TestCase {

	private SimpleBeeTemperatureNotification simpleBeeTemp;
	private SimpleBeeDevice simpleBeeDevice;

	protected static void tearDownAfterClass() throws Exception {
	}

	protected void setUp() throws Exception {
		super.setUp();
		simpleBeeTemp = new SimpleBeeTemperatureNotification();
		simpleBeeDevice = new SimpleBeeDevice() {

			public void setData(String data) {
				// TODO Auto-generated method stub

			}

			public TypeCode getTypeCode() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getSerial() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getModuleAddress() {
				return "1234";
			}

			public String getData() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	public void testTemp() {
		simpleBeeTemp.updateData(simpleBeeDevice, null, "1:>0");
	}

}
