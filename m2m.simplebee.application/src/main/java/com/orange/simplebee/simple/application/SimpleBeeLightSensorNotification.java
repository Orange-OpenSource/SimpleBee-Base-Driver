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
 * This component is notified by the light sensor device about illuminance.
 * 
 * @author Gregory BONNARDEL
 * 
 */
public class SimpleBeeLightSensorNotification implements SimpleBeeListener {

	public SimpleBeeLightSensorNotification() {
	}

	public void updateData(SimpleBeeDevice simpleBeeDevice, String oldData,
			String newData) {
		float lux = decodeLux(newData);
		System.out.println("receives lux :" + lux);

	}

	public void updateBatteryLevel(SimpleBeeDevice simpleBeeDevice,
			float oldBatteryLevel, float newBatteryLevel) {

	}

	private float decodeLux(String newData) {
		// first build value0 and value from the provided string

		// value0 is built with the first two characters
		int value0 = Integer.parseInt(
				Integer.toHexString(newData.charAt(0) & 0x0F), 16)
				* 16
				+ Integer.parseInt(
						Integer.toHexString(newData.charAt(1) & 0x0F), 16);

		// value1 is build with the last two characters
		int value1 = Integer.parseInt(
				Integer.toHexString(newData.charAt(2) & 0x0F), 16)
				* 16
				+ Integer.parseInt(
						Integer.toHexString(newData.charAt(3) & 0x0F), 16);

		// compute c0 et c1 with tri_val method
		double c0 = tri_val(value0);
		double c1 = tri_val(value1);

		return (float) (c0 * 0.46 * Math.pow(Math.E, (float) -3.13 * c1 / c0));
	}

	private double tri_val(int val) {
		double value, chord, step, step_number;
		if (val < 128) {
			value = -1;
		} else {
			int i = (val & 0x70) >> 4;
			switch (i) {
			case 0:
				chord = 0;
				step = 1;
				break;
			case 1:
				chord = 16;
				step = 2;
				break;
			case 2:
				chord = 49;
				step = 4;
				break;
			case 3:
				chord = 115;
				step = 8;
				break;
			case 4:
				chord = 247;
				step = 16;
				break;
			case 5:
				chord = 511;
				step = 32;
				break;
			case 6:
				chord = 1039;
				step = 64;
				break;
			case 7:
				chord = 2095;
				step = 128;
				break;
			default:
				chord = 0;
				step = 0;
				break;
			}

			step_number = (double) (val & 0x0f);
			value = (double) (chord + (step * step_number));

		}

		return value;
	}
}
