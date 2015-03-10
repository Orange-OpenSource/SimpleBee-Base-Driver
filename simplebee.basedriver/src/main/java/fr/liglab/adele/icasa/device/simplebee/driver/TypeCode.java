/*
 * Copyright (C) 2014 - 2015 Joseph Fourier University, LIG, ADELE Research Group
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
 * Author(s): German VEGA
 * 
 * Contributions:
 * - Orange: Dec. 2014 - Update code for CDC 1.1
 */
package fr.liglab.adele.icasa.device.simplebee.driver;

import org.osgi.service.log.LogService;

import com.orange.simplebee.impl.Logger;

/**
 * Represents a type of device.
 */
public class TypeCode {

	public static final TypeCode C001 = new TypeCode("C001", "PUSH_BUTTON",
			"A push button reports data (0 or 1) when the button is pushed");

	public static final TypeCode C002 = new TypeCode("C002", "POWER_SWITCH",
			"Power");

	public static final TypeCode C003 = new TypeCode("C003", "MOTION_SENSOR",
			"A motion sensor reports when motion is detected");

	public static final TypeCode C004 = new TypeCode("C004", "LIGHT_SENSOR",
			"Reports light");

	public static final TypeCode C005 = new TypeCode("C005",
			"TEMPERATURE_SENSOR",
			"A temperature sensor reports the temperature");

	public static final TypeCode A001 = new TypeCode("A001", "BINARY_LIGHT",
			"A binary light can be switched on or off");

	public static final TypeCode A002 = new TypeCode("A002", "SANDTIMER",
			"An electrical sandtimer");

	private final String value;

	private final String friendlyName;

	private final String description;

	private TypeCode(String value, String name, String description) {
		this.value = value;
		this.friendlyName = name;
		this.description = description;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public String getDescription() {
		return description;
	}

	public String getValue() {
		return value;
	}

	public static TypeCode getTypeCodeByFriendlyName(String friendlyName) {

		if (C001.friendlyName.equals(friendlyName)) {
			return TypeCode.C001;
		} else if (C002.friendlyName.equals(friendlyName)) {
			return TypeCode.C002;
		} else if (C003.friendlyName.equals(friendlyName)) {
			return TypeCode.C003;
		} else if (C004.friendlyName.equals(friendlyName)) {
			return TypeCode.C004;
		} else if (A001.friendlyName.equals(friendlyName)) {
			return TypeCode.A001;
		} else if (A002.friendlyName.equals(friendlyName)) {
			return TypeCode.A002;
		} else if (C005.friendlyName.equals(friendlyName)) {
			return TypeCode.C005;
		} else {
			Logger.log(LogService.LOG_ERROR,
					"unknown device type friendly name : " + friendlyName);
			return null;
		}
	}

	public String toString() {
		return friendlyName;
	}

	public static TypeCode valueOf(String typeCodeAsString) {
		if (C001.getValue().equals(typeCodeAsString)) {
			return TypeCode.C001;
		} else if (C002.getValue().equals(typeCodeAsString)) {
			return TypeCode.C002;
		} else if (C003.getValue().equals(typeCodeAsString)) {
			return TypeCode.C003;
		} else if (C004.getValue().equals(typeCodeAsString)) {
			return TypeCode.C004;
		} else if (A001.getValue().equals(typeCodeAsString)) {
			return TypeCode.A001;
		} else if (A002.getValue().equals(typeCodeAsString)) {
			return TypeCode.A002;
		} else if (C005.getValue().equals(typeCodeAsString)) {
			return TypeCode.C005;
		} else {
			Logger.log(LogService.LOG_ERROR, "unknown device type code : "
					+ typeCodeAsString);
			return null;
		}
	}
}
