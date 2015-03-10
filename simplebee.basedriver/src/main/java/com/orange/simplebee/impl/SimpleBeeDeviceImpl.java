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
package com.orange.simplebee.impl;

import com.orange.simplebee.SimpleBeeDevice;

import fr.liglab.adele.icasa.device.simplebee.driver.DeviceInfo;
import fr.liglab.adele.icasa.device.simplebee.driver.TypeCode;
import fr.liglab.adele.icasa.device.simplebee.driver.SimpleBeeDriver;

/**
 * 
 */
public class SimpleBeeDeviceImpl implements SimpleBeeDevice {

	private final DeviceInfo deviceInfo;
	private final SimpleBeeDriver simpleBeeDriver;
	private String serial;

	/**
	 * 
	 */
	public SimpleBeeDeviceImpl(DeviceInfo pDeviceInfo, SimpleBeeDriver pSimpleBeeDriver) {
		deviceInfo = pDeviceInfo;
		simpleBeeDriver = pSimpleBeeDriver;

		// serial = <TYPE_CODE_FRIENDLY_NAME>@<MODULE_ADDRESS>
		serial = deviceInfo.getTypeCode().getFriendlyName() + "@"
				+ deviceInfo.getModuleAddress();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.otb.simplebee.basedriver.SimpleBeeDevice#getModuleAddress()
	 */
	public String getModuleAddress() {
		return deviceInfo.getModuleAddress();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.otb.simplebee.basedriver.SimpleBeeDevice#getTypeCode()
	 */
	public TypeCode getTypeCode() {
		return deviceInfo.getTypeCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.otb.simplebee.basedriver.SimpleBeeDevice#getSerial()
	 */
	public String getSerial() {
		return serial;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.otb.simplebee.basedriver.SimpleBeeDevice#setData(java.lang.String)
	 */
	public void setData(String data) {
		simpleBeeDriver.setData(getModuleAddress(), data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.otb.simplebee.basedriver.SimpleBeeDevice#getData()
	 */
	public String getData() {
		return simpleBeeDriver.getData(getModuleAddress()).getData();
	}

}
