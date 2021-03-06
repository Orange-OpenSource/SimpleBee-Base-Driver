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

import org.osgi.service.log.LogService;

import com.orange.simplebee.impl.Logger;

/**
 * Task fired when a device discovery timer expires.
 */
public class DeviceDiscoveryTimeoutTask implements Runnable {

	private String deviceAddress;

	public DeviceDiscoveryTimeoutTask(String moduleAddress) {
		this.deviceAddress = moduleAddress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Logger.log(LogService.LOG_DEBUG,
				"Device discovery task started for device : " + deviceAddress);
	}
}
