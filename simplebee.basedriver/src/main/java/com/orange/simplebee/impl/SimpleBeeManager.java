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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.log.LogService;

import fr.liglab.adele.icasa.device.simplebee.driver.SimpleBeeDriver;

public class SimpleBeeManager implements ManagedService {

	/**
	 * default serial port
	 */
	private final String DEFAULT_SERIAL_PORT = "/dev/ttyUSB0";

	private String serialPort = null;
	private Integer baudRate = null;
	private List /* <String (port)> */usedPorts;
	private LogService logService;

	private ComponentFactory simpleBeeDriverFactory;

	public SimpleBeeManager() {
		log(LogService.LOG_INFO, "new SimpleBeeManager");
		usedPorts = new ArrayList();
	}

	protected void activate(ComponentContext componentContext) {
		log(LogService.LOG_INFO, "activate SimpleBeeManager");
	}

	protected void deactivate(ComponentContext componentContext) {
		log(LogService.LOG_INFO, "deactivate SimpleBeeManager");
	}

	protected void bindSimpleBeeDriverFactory(ComponentFactory componentFactory) {
		log(LogService.LOG_INFO, "bind factory");
		simpleBeeDriverFactory = componentFactory;
	}

	protected void unbindSimpleBeeDriverFactory(ComponentFactory componentFactory) {
		log(LogService.LOG_INFO, "unbind SimpleBeeDriverFactory");
		simpleBeeDriverFactory = null;
	}

	protected void bindSimpleBeeDriver(ServiceReference simpleBeeDriverServiceReference) {
		log(LogService.LOG_INFO, "bindSimpleBeeDriver....");
		String port = (String) simpleBeeDriverServiceReference
				.getProperty(SimpleBeeDriver.SIMPLE_BEE_SERIAL_PORT_PROPERTY);
		synchronized (usedPorts) {
			usedPorts.add(port);
		}
	}

	protected void unbindSimpleBeeDriver(ServiceReference simpleBeeDriverServiceReference) {
		log(LogService.LOG_INFO, "unbindSimpleBeeDriver....");
		String port = (String) simpleBeeDriverServiceReference
				.getProperty(SimpleBeeDriver.SIMPLE_BEE_SERIAL_PORT_PROPERTY);
		synchronized (usedPorts) {
			usedPorts.remove(port);
		}
	}

	protected void bindLogService(LogService logService) {
		this.logService = logService;
	}

	protected void unbindLogService(LogService logService) {
		this.logService = null;
	}

	public void updated(Dictionary properties) throws ConfigurationException {
		log(LogService.LOG_INFO, "update properties");
		if (properties != null) {
			log(LogService.LOG_DEBUG, "update");
			serialPort = (String) properties
					.get(SimpleBeeDriver.SIMPLE_BEE_SERIAL_PORT_PROPERTY);
			baudRate = (Integer) properties.get(SimpleBeeDriver.SIMPLE_BEE_BAUD_RATE_PROPERTY);

			log(LogService.LOG_DEBUG, "serialPort :" + serialPort);
			synchronized (usedPorts) {
				if (!usedPorts.contains(serialPort)) {
					log(LogService.LOG_DEBUG, "create new SimpleBeeDriver");
					properties.put("service.pid", "simplebee.driver_" + serialPort
							+ "_" + baudRate);

					simpleBeeDriverFactory.newInstance(properties);
				}
			}

		}

	}

	/**
	 * Log a message into the log service.
	 * 
	 * @param level
	 *            message level
	 * @param message
	 *            message
	 */
	private void log(int level, String message) {
		if (logService != null) {
			logService.log(level, message);
		}
	}

}
