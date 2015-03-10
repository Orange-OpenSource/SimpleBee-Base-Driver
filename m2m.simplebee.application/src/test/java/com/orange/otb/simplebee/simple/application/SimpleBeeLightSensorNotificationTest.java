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

import com.orange.simplebee.simple.application.SimpleBeeLightSensorNotification;

public class SimpleBeeLightSensorNotificationTest extends TestCase {

	private SimpleBeeLightSensorNotification test;

	protected static void tearDownAfterClass() throws Exception {
	}

	protected void setUp() throws Exception {
		super.setUp();
		test = new SimpleBeeLightSensorNotification();
	}

	public void testLight() {
		test.updateData(null, null, ">9<:");
		test.updateData(null, null, "?1<9");
		test.updateData(null, null, ";193 ");
	}

}
