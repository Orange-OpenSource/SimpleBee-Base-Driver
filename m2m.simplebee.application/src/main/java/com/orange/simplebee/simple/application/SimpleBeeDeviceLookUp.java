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

/**
 * This example shows how to lookup all SimpleBeeDevices detected on the
 * network.
 * 
 * @author Gregory BONNARDEL
 */
public class SimpleBeeDeviceLookUp {

	public SimpleBeeDeviceLookUp() {

	}

	/**
	 * This method is called as soon as a new SimpleBee device is detected on
	 * the network.
	 * 
	 * @param simpleBeeDevice
	 *            new simpleBee Device
	 */
	protected void bindSimpleBeeDevice(SimpleBeeDevice simpleBeeDevice) {
		System.out.println("bind a new SimpleBeeDevice");
		System.out.println("Module address: "
				+ simpleBeeDevice.getModuleAddress());
		System.out.println("Module serial: " + simpleBeeDevice.getSerial());
		System.out
				.println("Retrieve module data: " + simpleBeeDevice.getData());
	}

	/**
	 * This method is called when the SimpleBeeDevice leaves the network
	 * 
	 * @param simpleBeeDevice
	 *            simpleBee Device leaving the network
	 */
	protected void unbindSimpleBeeDevice(SimpleBeeDevice simpleBeeDevice) {
		System.out.println("SimpleBeeDevice "
				+ simpleBeeDevice.getModuleAddress() + " leaves the network");
	}

}
