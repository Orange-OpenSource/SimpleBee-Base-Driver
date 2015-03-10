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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.device.Constants;
import org.osgi.service.log.LogService;

import com.orange.simplebee.SimpleBeeDevice;
import com.orange.simplebee.SimpleBeeListener;

import fr.liglab.adele.icasa.device.simplebee.driver.Data;
import fr.liglab.adele.icasa.device.simplebee.driver.DeviceInfo;
import fr.liglab.adele.icasa.device.simplebee.driver.SimpleBeeDeviceTracker;
import fr.liglab.adele.icasa.device.simplebee.driver.SimpleBeeDriver;

public class Activator implements SimpleBeeDeviceTracker {

	private static final String DEVICE_CATEGORY = Constants.DEVICE_CATEGORY;
	private static final String DEVICE_SERIAL = Constants.DEVICE_SERIAL;
	private static final String DEVICE_DESCRIPTION = Constants.DEVICE_DESCRIPTION;
	private static final String DEVICE_FRIENDLY_NAME = "DEVICE_FRIENDLY_NAME";
	private static final String DEVICE_TYPE = "DEVICE_TYPE";
	private static final String SERVICE_PID = org.osgi.framework.Constants.SERVICE_PID;

	private final Map simpleBeeDevices = new HashMap();
	private final Map simpleBeeDeviceRegistrations = new HashMap();

	/**
	 * log service.
	 */
	private LogService logService;

	/**
	 * simplebee driver
	 */
	private SimpleBeeDriver simpleBeeDriver;

	/**
	 * Bundle context
	 */
	private BundleContext bundleContext;

	/**
	 * list of SimpleBeeListener ' service references.
	 */
	private List simpleBeeListenerServiceReferences;

	public Activator() {
		simpleBeeListenerServiceReferences = new ArrayList();
	}

	protected void activate(final ComponentContext cc) {
		bundleContext = cc.getBundleContext();
	}

	/**
	 * Bind SimpleBeeDriver
	 * 
	 * @param pSimpleBeeDriver
	 *            simpleBeeDriver
	 */
	protected void bindSimpleBeeDriver(final SimpleBeeDriver pSimpleBeeDriver) {
		simpleBeeDriver = pSimpleBeeDriver;
	}

	/**
	 * Unbind SimpleBeeDriver
	 * 
	 * @param pSimpleBeeDriver
	 *            simpleBeeDriver
	 */
	protected void unbindSimpleBeeDriver(final SimpleBeeDriver pSimpleBeeDriver) {
		simpleBeeDriver = null;
	}

	/**
	 * Bind log service
	 * 
	 * @param pLogService
	 *            log service
	 */
	protected void bindLogService(final LogService pLogService) {
		logService = pLogService;
	}

	/**
	 * Unbind log service
	 * 
	 * @param pLogService
	 */
	protected void unbindLogService(final LogService pLogService) {
		logService = null;
	}

	/**
	 * Bind SimpleBee listener
	 * 
	 * @param simpleBeeListenerServiceReference
	 *            service reference
	 */
	protected void bindSimpleBeeListener(
			final ServiceReference simpleBeeListenerServiceReference) {
		log(LogService.LOG_INFO, "new SimpleBeeListener");
		synchronized (simpleBeeListenerServiceReferences) {
			simpleBeeListenerServiceReferences.add(simpleBeeListenerServiceReference);
		}
	}

	/**
	 * Unbind SimpleBee listener
	 * 
	 * @param simpleBeeListenerServiceReference
	 *            service reference
	 */
	protected void unbindSimpleBeeListener(
			final ServiceReference simpleBeeListenerServiceReference) {
		log(LogService.LOG_INFO, "SimpleBeeListener removed");
		synchronized (simpleBeeListenerServiceReferences) {
			simpleBeeListenerServiceReferences.remove(simpleBeeListenerServiceReference);
		}
	}

	public void deviceAdded(final DeviceInfo deviceInfo) {
		log(LogService.LOG_INFO,
				"new SimpleBee device detected (@:" + deviceInfo.getModuleAddress()
						+ ")");

		SimpleBeeDevice simplebeeDevice = new SimpleBeeDeviceImpl(deviceInfo, simpleBeeDriver);

		Dictionary properties = new Hashtable();
		properties.put(DEVICE_CATEGORY, SimpleBeeDevice.DEVICE_CATEGORY);
		properties.put(DEVICE_SERIAL, simplebeeDevice.getSerial());
		properties.put(DEVICE_TYPE, simplebeeDevice.getTypeCode().getFriendlyName());
		properties.put(DEVICE_DESCRIPTION, simplebeeDevice.getTypeCode()
				.getDescription());
		properties.put(DEVICE_FRIENDLY_NAME, simplebeeDevice.getTypeCode()
				.getFriendlyName());
		properties.put(SERVICE_PID, SimpleBeeDevice.DEVICE_CATEGORY[0] + "_"
				+ simplebeeDevice.getSerial());

		ServiceRegistration sr = bundleContext.registerService(
				SimpleBeeDevice.class.getName(), simplebeeDevice, properties);

		synchronized (this) {
			simpleBeeDevices.put(simplebeeDevice.getModuleAddress(), simplebeeDevice);
			simpleBeeDeviceRegistrations.put(simplebeeDevice.getModuleAddress(), sr);
		}

	}

	public void deviceRemoved(final DeviceInfo deviceInfo) {
		log(LogService.LOG_INFO,
				"SimpleBee device removed (@:" + deviceInfo.getModuleAddress() + ")");

		// unregister the related SimpleBeeDevice
		ServiceRegistration sr = null;
		SimpleBeeDevice simpleBeeDevice = null;
		synchronized (this) {
			sr = (ServiceRegistration) simpleBeeDeviceRegistrations
					.remove(deviceInfo.getModuleAddress());
			simpleBeeDevice = (SimpleBeeDevice) simpleBeeDevices.remove(deviceInfo
					.getModuleAddress());
		}

		if (sr != null) {
			sr.unregister();
		}
		simpleBeeDevice = null;
	}

	public void deviceDataChanged(final String moduleAddress,
			final Data oldData, final Data newData) {
		log(LogService.LOG_DEBUG,
				"new notification received from " + moduleAddress + " : (old:"
						+ (oldData != null ? oldData.getData() : null)
						+ ", new:"
						+ (newData != null ? newData.getData() : null) + ")");
		SimpleBeeDevice simpleBeeDevice = null;
		synchronized (this) {
			simpleBeeDevice = (SimpleBeeDevice) simpleBeeDevices.get(moduleAddress);
		}

		if (simpleBeeDevice != null) {
			synchronized (simpleBeeListenerServiceReferences) {
				for (Iterator simpleBeeListenerSrsIt = simpleBeeListenerServiceReferences
						.iterator(); simpleBeeListenerSrsIt.hasNext();) {
					ServiceReference sr = (ServiceReference) simpleBeeListenerSrsIt
							.next();
					String deviceSerial = (String) sr
							.getProperty(DEVICE_SERIAL);
					if ((deviceSerial == null)
							|| (deviceSerial.endsWith("@" + moduleAddress))) {
						// the simpleBee listener must be notified
						SimpleBeeListener simpleBeeListener = (SimpleBeeListener) bundleContext
								.getService(sr);
						try {
							simpleBeeListener
									.updateData(
											simpleBeeDevice,
											(oldData != null ? oldData
													.getData() : null),
											(newData != null ? newData
													.getData() : null));
						} catch (Throwable t) {
							log(LogService.LOG_ERROR,
									"Exception thrown on SimpleBeeListener.updateData()",
									t);
							t.printStackTrace();
						}
						bundleContext.ungetService(sr);

					}

				}
			}
		} else {
			log(LogService.LOG_ERROR,
					"unable to get the SimpleBeeDevice related to @" + moduleAddress
							+ " - no notifcations sent");

		}

	}

	public void deviceBatteryLevelChanged(final String moduleAddress,
			final float oldBatteryLevel, final float newBatteryLevel) {
		log(LogService.LOG_DEBUG, "new battery notification received from "
				+ moduleAddress + " : (old:" + oldBatteryLevel + ", new:"
				+ newBatteryLevel + ")");
		SimpleBeeDevice simpleBeeDevice = null;
		synchronized (this) {
			simpleBeeDevice = (SimpleBeeDevice) simpleBeeDevices.get(moduleAddress);
		}

		if (simpleBeeDevice != null) {
			synchronized (simpleBeeListenerServiceReferences) {
				for (Iterator simpleBeeListenerSrsIt = simpleBeeListenerServiceReferences
						.iterator(); simpleBeeListenerSrsIt.hasNext();) {
					ServiceReference sr = (ServiceReference) simpleBeeListenerSrsIt
							.next();
					String deviceSerial = (String) sr
							.getProperty(DEVICE_SERIAL);
					if ((deviceSerial == null)
							|| (deviceSerial.endsWith("@" + moduleAddress))) {
						// the simpleBee listener must be notified
						SimpleBeeListener simpleBeeListener = (SimpleBeeListener) bundleContext
								.getService(sr);
						try {
							simpleBeeListener.updateBatteryLevel(simpleBeeDevice,
									oldBatteryLevel, newBatteryLevel);
						} catch (Throwable t) {
							log(LogService.LOG_ERROR,
									"Exception thrown on SimpleBeeListener.updateBatteryLevel()",
									t);
							t.printStackTrace();
						}
						bundleContext.ungetService(sr);

					}

				}
			}
		} else {
			log(LogService.LOG_ERROR,
					"unable to get the SimpleBeeDevice related to @" + moduleAddress
							+ " - no notifcations sent");

		}

	}

	private void log(final int level, final String message) {
		if (logService != null) {
			logService.log(level, message);
		}
	}

	private void log(final int level, final String message,
			final Throwable throwable) {
		if (logService != null) {
			logService.log(level, message, throwable);
		}
	}

}
