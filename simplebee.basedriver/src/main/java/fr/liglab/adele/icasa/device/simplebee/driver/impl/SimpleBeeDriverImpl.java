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
package fr.liglab.adele.icasa.device.simplebee.driver.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import com.orange.simplebee.impl.Logger;

import fr.liglab.adele.icasa.device.simplebee.driver.Data;
import fr.liglab.adele.icasa.device.simplebee.driver.DeviceInfo;
import fr.liglab.adele.icasa.device.simplebee.driver.SimpleBeeDeviceTracker;
import fr.liglab.adele.icasa.device.simplebee.driver.SimpleBeeDriver;
import fr.liglab.adele.icasa.device.simplebee.driver.TypeCode;
import fr.liglab.adele.icasa.device.simplebee.driver.serial.SerialPortHandler;
import fr.liglab.adele.icasa.device.simplebee.driver.serial.model.ResponseType;

/**
 * Implementation class for the Zigbee Driver interface.
 */
public class SimpleBeeDriverImpl implements SimpleBeeDriver {

	private SerialPortHandler handler;

	private ComponentContext componentContext;

	private String port;

	private Integer baud;

	/* @GardedBy(trackers) */
	private List/* <SimpleBeeDeviceTracker> */trackers;

	public List/* <SimpleBeeDeviceTracker> */getTrackers() {
		synchronized (trackers) {
			return new ArrayList/* <SimpleBeeDeviceTracker> */(trackers);
		}
	}

	protected void bindSimpleBeeDeviceTracker(SimpleBeeDeviceTracker tracker) {
		synchronized (trackers) {
			List/* <DeviceInfo> */deviceInfos = handler.getDeviceInfos();
			trackers.add(tracker);
			for (Iterator deviceInfosIt = deviceInfos.iterator(); deviceInfosIt
					.hasNext();) {
				DeviceInfo deviceInfo = (DeviceInfo) deviceInfosIt.next();
				try {
					tracker.deviceAdded(deviceInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void unbindSimpleBeeDeviceTracker(SimpleBeeDeviceTracker tracker) {
		synchronized (trackers) {
			List/* <DeviceInfo> */deviceInfos = handler.getDeviceInfos();
			trackers.remove(tracker);
			for (Iterator deviceInfosIt = deviceInfos.iterator(); deviceInfosIt
					.hasNext();) {
				DeviceInfo deviceInfo = (DeviceInfo) deviceInfosIt.next();
				try {
					tracker.deviceRemoved(deviceInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public SimpleBeeDriverImpl() {
		// ? how to initialize the bundle context ?
		handler = new SerialPortHandler(this);
		trackers = new ArrayList/* <SimpleBeeDeviceTracker> */();
	}

	protected void activate(ComponentContext pComponentContext) {
		Logger.log(LogService.LOG_INFO, "activate SimpleBeeDriver");

		componentContext = pComponentContext;
		port = (String) componentContext.getProperties().get(
				SIMPLE_BEE_SERIAL_PORT_PROPERTY);
		baud = (Integer) componentContext.getProperties().get(
				SIMPLE_BEE_BAUD_RATE_PROPERTY);

		String lport = getCOMPort();
		Logger.log(LogService.LOG_INFO, "[SimpleBee] - (start) - lport:"
				+ lport);
		if (lport.compareTo("NONE") == 0) {
			Logger.log(LogService.LOG_WARNING,
					"Please set a port for ZigBee Driver");
			return;
		}
		Thread thread = new Thread(new Runnable() {

			public void run() {
				try {
					handler.startListening(getCOMPort(), baud.intValue());
				} catch (Exception e) {
					Logger.log(LogService.LOG_WARNING,
							"Unable to connect into port: " + getCOMPort());
				}
			};

		}, "zigbee driver listenning port thread");
		thread.start();
	}

	protected void deactivate(ComponentContext componentContext) {
		handler.stopListening();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.liglab.adele.habits.monitoring.zigbee.driver.ZigbeeDriver#getCOMPort()
	 */
	public String getCOMPort() {
		String givenPort = (String) componentContext.getProperties().get(
				SIMPLE_BEE_SERIAL_PORT_PROPERTY);
		if (givenPort != null && givenPort.length() > 0) {
			Logger.log(LogService.LOG_INFO, "Get port: " + givenPort
					+ " from property: " + SIMPLE_BEE_SERIAL_PORT_PROPERTY);
			return givenPort;
		}
		Logger.log(LogService.LOG_WARNING, "Get port: " + givenPort
				+ " from component: " + SIMPLE_BEE_SERIAL_PORT_PROPERTY);
		if (port == null) {
			return "NONE";
		}
		return port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.liglab.adele.habits.monitoring.zigbee.driver.ZigbeeDriver#getBaud()
	 */
	public int getBaud() {
		return baud.intValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.liglab.adele.habits.monitoring.zigbee.driver.ZigbeeDriver#getDeviceInfos
	 * ()
	 */
	public Set/* <DeviceInfo> */getDeviceInfos() {
		return new TreeSet/* <DeviceInfo> */(handler.getDeviceInfos());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.liglab.adele.habits.monitoring.zigbee.driver.ZigbeeDriver#getDeviceInfos
	 * (fr.liglab.adele.habits.monitoring.zigbee.driver.TypeCode)
	 */
	public Set/* <DeviceInfo> */getDeviceInfos(TypeCode typeCode) {
		Set/* <DeviceInfo> */typedDevices = new HashSet/* <DeviceInfo> */();
		for (Iterator deviceInfosIt = handler.getDeviceInfos().iterator(); deviceInfosIt
				.hasNext();) {
			DeviceInfo deviceInfo = (DeviceInfo) deviceInfosIt.next();
			if (deviceInfo.getTypeCode().equals(typeCode)) {
				typedDevices.add(deviceInfo);
			}
		}
		return typedDevices;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.liglab.adele.habits.monitoring.zigbee.driver.ZigbeeDriver#getData(
	 * java.lang.String)
	 */
	public Data getData(String moduleAddress) {
		DeviceInfo deviceInfo = handler.getDeviceInfo(moduleAddress);
		if (deviceInfo == null)
			return null;

		return deviceInfo.getDeviceData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.liglab.adele.habits.monitoring.zigbee.driver.ZigbeeDriver#setData(
	 * String moduleAddress, String dataToSet)
	 */
	public void setData(String moduleAddress, String dataToSet) {
		Logger.log(LogService.LOG_DEBUG,
				"sending request response to device : " + moduleAddress
						+ " with value : " + dataToSet);
		handler.write(ResponseType.REQUEST, moduleAddress, dataToSet);
	}

}
