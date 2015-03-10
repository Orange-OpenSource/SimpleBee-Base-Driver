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
package fr.liglab.adele.icasa.device.simplebee.driver.serial.model;

/**
 * Enum class for zigbee devices categories.
 */
public class DeviceCategory {

	public static final DeviceCategory ACTUATOR = new DeviceCategory('A',
			"Actuator");
	public static final DeviceCategory SENSOR = new DeviceCategory('C',
			"Sensor");

	private final char value;
	private final String friendlyName;

	private DeviceCategory(char value, String pFriendlyName) {
		this.value = value;
		this.friendlyName = pFriendlyName;
	}

	public String toString() {
		return friendlyName + " : " + value;
	}

	public char getValue() {
		return this.value;
	}
}
