/*
 * Software Name : simplebee.basedriver
 *
 * Module name: simplebee.basedriver
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
package com.orange.simplebee;

import fr.liglab.adele.icasa.device.simplebee.driver.TypeCode;

/**
 * <p>
 * A SimpleBeeDevice is used to interact with a physical XBEE device.
 * </p>
 * <p>
 * Once a SimpleBee device is detected on the network, an SimpleBeeDevice instance is
 * created and is registered into the OSGi registry. This instance is
 * unregistered as soon as the SimpleBee device leaves the network.
 * </p>
 * <p>
 * Business data are retrieved through a call to {@link #getData()}. For some
 * devices, it is possible to configure business data through a call to
 * {@link #setData(String)}.
 * </p>
 */
public interface SimpleBeeDevice {

	/**
	 * DEVICE_CATEGORY property
	 */
	public static final String[] DEVICE_CATEGORY = { "SimpleBee" };

	/**
	 * Get the module address.
	 * 
	 * @return module address.
	 */
	public String getModuleAddress();

	/**
	 * Get the type of device.
	 * 
	 * @return type of device
	 */
	public TypeCode getTypeCode();

	/**
	 * Get the device serial. The serial is composed of the typecode of the
	 * device following by "@" followed by the module address. Example :
	 * PUSH_BUTTON@1000
	 * 
	 * @return device serial
	 */
	public String getSerial();

	/**
	 * Set business data on the device. No checking about the provided data. No
	 * checking about the completion of the configuration.
	 * 
	 * @param data
	 *            data to set
	 */
	public void setData(String data);

	/**
	 * Get the business data from the device.
	 * 
	 * @return received business data or null if the data has not been received.
	 */
	public String getData();
}
