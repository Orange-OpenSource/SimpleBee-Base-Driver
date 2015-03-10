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

/**
 * <p>
 * A SimpleBeeListener is notified about the following SimpleBee events :
 * <ul>
 * <li>a simplebee device business data update (see
 * {@link #updateData(SimpleBeeDevice, String, String)})</li>
 * <li>a simplebee device battery level update (see
 * {@link #updateBatteryLevel(SimpleBeeDevice, float, float)}</li>
 * </ul>
 * </p>
 * To receive these events, a SimpleBeeListener must be registered as an OSGi
 * service. It is possible to receive notification about a specific SimpleBee device
 * by setting the DEVICE_SERIAL service property.
 */
public interface SimpleBeeListener {

	/**
	 * This method is used to notify about a business data update.
	 * 
	 * @param simpleBeeDevice
	 *            simpleBee device
	 * @param oldData
	 *            previous business data value (null if no previous)
	 * @param newData
	 *            new business data value
	 */
	public void updateData(SimpleBeeDevice simpleBeeDevice, String oldData,
			String newData);

	/**
	 * This method is used to notify about a battery level update.
	 * 
	 * @param simpleBeeDevice
	 *            related simpleBee device
	 * @param oldBatteryLevel
	 *            old battery level
	 * @param newBatteryLevel
	 *            new battery level
	 */
	public void updateBatteryLevel(SimpleBeeDevice simpleBeeDevice,
			float oldBatteryLevel, float newBatteryLevel);
}
