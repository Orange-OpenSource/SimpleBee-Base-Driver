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
import com.orange.simplebee.SimpleBeeListener;

/**
 * This component receives data notification from the temperature device. It
 * shows how to decode the data/
 * 
 * @author Gregory BONNARDEL
 */
public class SimpleBeeTemperatureNotification implements SimpleBeeListener {

	public SimpleBeeTemperatureNotification() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method is called whenever the temperature change
	 */
	public void updateData(SimpleBeeDevice simpleBeeDevice, String oldData,
			String newData) {
		float temperature = decodeTemperature(newData);
		System.out.println("SimpleBee temperature device ("
				+ simpleBeeDevice.getModuleAddress() + ") reports "
				+ temperature + "�c");

	}

	public void updateBatteryLevel(SimpleBeeDevice simpleBeeDevice,
			float oldBatteryLevel, float newBatteryLevel) {
	}

	/**
	 * Decode the temperature from the provided string.
	 * 
	 * @param newData
	 *            encoded temperature
	 * @return temperature as float
	 */
	private float decodeTemperature(String newData) {
		// only the first three bytes are relevant
		// and for each of them, only the four least significant bit are
		// relevant
		// each byte is firstly converted as an hexadecimal string which is
		// converted into
		// integer.
		// then the first integer is multiplied by 256 (16^2)
		// the second is multiplied by 16 (16^1)

		int byte0 = (Integer.parseInt(
				Integer.toHexString((newData.charAt(0) & 0x0f)), 16) << 8);
		int byte1 = (Integer.parseInt(
				Integer.toHexString((newData.charAt(1) & 0x0f)), 16) << 4);
		int byte2 = (Integer.parseInt(
				Integer.toHexString((newData.charAt(2) & 0x0f)), 16));

		// finally add all integer and multiply by 0,0625
		return (float) ((byte0 + byte1 + byte2) * 0.0625);
	}

}
