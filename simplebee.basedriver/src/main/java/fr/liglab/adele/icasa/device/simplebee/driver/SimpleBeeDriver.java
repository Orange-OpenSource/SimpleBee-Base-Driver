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

import java.util.Set;

/**
 * Provides this service to implement a Zigbee driver.
 */
public interface SimpleBeeDriver {

	public static final String SIMPLE_BEE_SERIAL_PORT_PROPERTY = "simplebee.driver.port";

	public static final String SIMPLE_BEE_BAUD_RATE_PROPERTY = "simplebee.driver.baud.rate";

	/**
	 * Returns COM port name used to plug the zigbee dongle.
	 * 
	 * @return COM port name used to plug the zigbee dongle.
	 */
	String getCOMPort();

	/**
	 * Returns baud used for usb communication channel.
	 * 
	 * @return baud used for usb communication channel.
	 */
	int getBaud();

	/**
	 * Returns information about all zigbee devices discovered by the dongle.
	 * 
	 * @return information about all zigbee devices discovered by the dongle.
	 */
	Set/* <DeviceInfo> */getDeviceInfos();

	/**
	 * Returns information about all zigbee devices discovered by the dongle
	 * with specified type. If there is no device, returns an empty set.
	 * 
	 * @param typeCode
	 *            type code representing a device type
	 * @return information about all zigbee devices discovered by the dongle
	 *         with specified type.
	 */
	Set/* <DeviceInfo> */getDeviceInfos(TypeCode typeCode);

	/**
	 * Returns the current data of the device with specified module address. The
	 * returned data could be cached data from last data sent from the device.
	 * 
	 * @param moduleAddress
	 *            a device module address
	 * @return the current data of the device with specified module address.
	 */
	Data getData(String moduleAddress);

	/**
	 * Sets specified value to the device with corresponding module address.
	 * 
	 * @param moduleAddress
	 *            a device module address
	 * @param dataToSet
	 *            value to set
	 */
	void setData(String moduleAddress, String dataToSet);
}
