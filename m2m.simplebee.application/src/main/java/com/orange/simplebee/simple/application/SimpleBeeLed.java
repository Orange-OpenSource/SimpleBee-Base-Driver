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
 * Author(s): Gregory BONNARDEL (Alten Group)
 */
package com.orange.simplebee.simple.application;

import com.orange.simplebee.SimpleBeeDevice;

public class SimpleBeeLed {

	private SimpleBeeDevice simpleBeeLed;

	public SimpleBeeLed() {
		// TODO Auto-generated constructor stub
	}

	protected void bindSimpleBeeLed(SimpleBeeDevice led) {
		simpleBeeLed = led;

		System.out.println("bind led");
		String currentStatus = simpleBeeLed.getData();
		System.out.println("current led status :" + currentStatus);

		if (currentStatus.equals("0")) {
			simpleBeeLed.setData("1");
		} else {
			simpleBeeLed.setData("0");
		}
	}

	protected void unbindSimpleBeeLed(SimpleBeeDevice led) {

	}

}
