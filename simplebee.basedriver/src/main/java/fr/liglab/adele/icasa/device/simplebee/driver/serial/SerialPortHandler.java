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
package fr.liglab.adele.icasa.device.simplebee.driver.serial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osgi.service.log.LogService;

import com.orange.simplebee.impl.Logger;

import fr.liglab.adele.icasa.device.simplebee.driver.Data;
import fr.liglab.adele.icasa.device.simplebee.driver.DeviceInfo;
import fr.liglab.adele.icasa.device.simplebee.driver.TypeCode;
import fr.liglab.adele.icasa.device.simplebee.driver.SimpleBeeDeviceTracker;
import fr.liglab.adele.icasa.device.simplebee.driver.impl.DataImpl;
import fr.liglab.adele.icasa.device.simplebee.driver.impl.DeviceInfoImpl;
import fr.liglab.adele.icasa.device.simplebee.driver.impl.SimpleBeeDriverImpl;
import fr.liglab.adele.icasa.device.simplebee.driver.serial.model.ResponseType;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

/**
 * Class managing the serial port data input/output.
 */
public class SerialPortHandler {

	private volatile boolean socketOpened = true;
	private DataInputStream ins = null;
	private DataOutputStream ous = null;
	private Object streamLock = new Object();
	// requests to sent to devices.
	private LinkedList/* <List<Byte>> */toWriteData = new LinkedList/* <List<Byte>> */();// handle
	// write
	// in
	// the
	// same
	// thread
	// as
	// read.
	// Map requestData <String/* module */, String /* Expected data */>
	private Map requestData = new Hashtable/* <String, String> */();

	// Map deviceDiscoveryList <String /* module address */, ScheduledFuture<?>>
	private Map deviceDiscoveryList = new HashMap/* <String, ScheduledFuture<?>> */();

	/* @GardedBy(deviceList) */
	// Map deviceList <String /* module address */, DeviceInfo>
	private Map deviceList = new HashMap/* <String, DeviceInfo> */();

	/* @GardedBy(deviceList) */
	private SimpleBeeDriverImpl trackerMgr;

	// private ScheduledExecutorService executor;

	public SerialPortHandler(SimpleBeeDriverImpl zigbeeDriverImpl) {
		this.trackerMgr = zigbeeDriverImpl;
		// executor = Executors.newSingleThreadScheduledExecutor();
	}

	public List/* <DeviceInfo> */getDeviceInfos() {
		synchronized (deviceList) {
			return new ArrayList/* <DeviceInfo> */(deviceList.values());
		}
	}

	/**
	 * Start listening on the given serial port.
	 * 
	 * @param port
	 * @throws IOException
	 */
	public void startListening(String port, int baud) throws IOException {

		CommPortIdentifier commPortIdentifer = null;
		try {
			commPortIdentifer = CommPortIdentifier.getPortIdentifier(port);
		} catch (NoSuchPortException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		CommPort commPort = null;
		try {
			commPort = commPortIdentifer.open(this.getClass().getName(), 10000);
		} catch (PortInUseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		SerialPort serialPort = (SerialPort) commPort;

		synchronized (streamLock) {
			this.socketOpened = true;

			try {
				serialPort.setSerialPortParams(baud, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			} catch (UnsupportedCommOperationException e) {
				e.printStackTrace();
			}

			try {
				ins = new DataInputStream(serialPort.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				ous = new DataOutputStream(serialPort.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		try {
			while (socketOpened) {
				List/* <Byte> */sb = read();
				if (sb.size() > 1) {
					parseData(sb);
					sb.clear();
				}
			}
		} finally {
			closeStreams();
			serialPort.close();
		}
	}

	public void stopListening() {
		this.socketOpened = false;
	}

	/**
	 * Parse data from the list of byte read.
	 * 
	 * @param sb
	 * @return
	 * @throws IOException
	 */
	public DeviceInfo parseData(List/* <Byte> */sb) throws IOException {

		DeviceInfo deviceInfos = null;
		DataImpl dataValue = null;
		String moduleAddress = null;

		// verify checksum, otherwise discard
		if (verifyChecksum(sb)) {
			// depending on frame we got, send a response
			char type = getTrameType(sb);
			switch (type) {
			case 'I':
				// identification frame not handled
				break;
			case 'W':
			case 'R':
			case 'D':
				moduleAddress = parseModuleAddress(sb);
				boolean existingDevice = deviceList.containsKey(moduleAddress);
				if (existingDevice) {
					deviceInfos = (DeviceInfo) deviceList.get(moduleAddress);
				} else {
					deviceInfos = new DeviceInfoImpl();
					((DeviceInfoImpl) deviceInfos)
							.setModuleAddress(moduleAddress);
					// ScheduledFuture/*<?>*/ scheduledDeviceTimeout = executor
					// .scheduleWithFixedDelay(
					// new DeviceDiscoveryTimeoutTask(
					// moduleAddress), 0, 120,
					// TimeUnit.SECONDS);
					// deviceDiscoveryList.put(moduleAddress,
					// scheduledDeviceTimeout);
					Runnable extendDeviceTimeoutTask = new ExtendDeviceTimeoutTask(
					/* scheduledDeviceTimeout, */moduleAddress);
					Thread t = new Thread(extendDeviceTimeoutTask);
					t.start();
					// executor.schedule(extendDeviceTimeoutTask, 120,
					// TimeUnit.SECONDS);
				}
				dataValue = new DataImpl();
				Data oldData = deviceInfos.getDeviceData();
				float oldBatteryLevel = deviceInfos.getBatteryLevel();
				// parse to get data
				dataValue.setTimeStamp(new Date());
				dataValue.setData(parseDataValue(sb));
				// parse to get battery level
				((DeviceInfoImpl) deviceInfos).setBatteryLevel(Float
						.parseFloat(parseBatteryLevel(sb)));
				((DeviceInfoImpl) deviceInfos).setDeviceData(dataValue);
				((DeviceInfoImpl) deviceInfos).setLastConnexionDate(new Date());
				((DeviceInfoImpl) deviceInfos).setTypeCode(TypeCode
						.valueOf(parseTypeCode(sb)));
				// notify battery level change and data change.
				// It will notify only when value has already changed.
				if (type == 'R') {
					// send data to set to device.
					handleWrite(moduleAddress);
				} else if (type == 'D') {
					Logger.log(LogService.LOG_DEBUG, "Sending ack of Data to "
							+ deviceInfos.getModuleAddress());
					write(buildResponse(ResponseType.DATA,
							deviceInfos.getModuleAddress()));
				} else if (type == 'W') {
					Logger.log(
							LogService.LOG_DEBUG,
							"Sending ack of Watchdog to "
									+ deviceInfos.getModuleAddress());
					write(buildResponse(ResponseType.WATCHDOG, moduleAddress));
				}
				if (!existingDevice) {
					Logger.log(LogService.LOG_DEBUG,
							"notifying tracker about new device.");
					// notify to trackers.
					notifyDeviceAdded(deviceInfos);
					notifyBatteryLevelChange(deviceInfos, oldBatteryLevel, true);
					notifyDataChange(deviceInfos, oldData, true);
				}
				notifyBatteryLevelChange(deviceInfos, oldBatteryLevel, false);
				notifyDataChange(deviceInfos, oldData, false);
				deviceList.put(moduleAddress, deviceInfos);
				break;
			default:
				Logger.log(LogService.LOG_DEBUG, "unknown frame received : "
						+ sb);
				break;
			}

		}
		return deviceInfos;
	}

	/**
	 * Parse the device typeCode in the given frame.
	 * 
	 * @param sb
	 * @return
	 */
	private String parseTypeCode(List/* <Byte> */sb) {
		int deviceCategoryPos = sb.lastIndexOf(new Byte((byte) 'B')) + 2;
		return ByteToChar(sb).substring(deviceCategoryPos,
				deviceCategoryPos + 4);
	}

	/**
	 * Build a response for the specified frame type.
	 * 
	 * @param responseType
	 * @param moduleAddress
	 * @return
	 */
	private List/* <Byte> */buildResponse(ResponseType responseType,
			String moduleAddress) {

		List/* <Byte> */response = new ArrayList/* <Byte> */();
		int csum = 0;

		response.add(new Byte((byte) responseType.getValue()));
		csum += responseType.getValue();
		byte[] moduleAddressAsBytes = moduleAddress.getBytes();
		for (int i = 0; i < moduleAddressAsBytes.length; i++) {
			Byte b = new Byte(moduleAddressAsBytes[i]);
			response.add(b);
			csum += b.intValue();
		}
		csum &= 0xff;
		response.add(new Byte((byte) ((csum >> 4) + 0x30)));
		response.add(new Byte((byte) ((csum & 0x0F) + 0x30)));
		response.add(new Byte((byte) '\r'));
		return response;
	}

	/**
	 * Build a response for the specified frame type and sets a new value for
	 * the device.
	 * 
	 * @param responseType
	 * @param moduleAddress
	 * @param newValue
	 * @return
	 */
	private List/* <Byte> */buildResponseWithNewValue(
			ResponseType responseType, String moduleAddress, String newValue) {
		List/* <Byte> */response = new ArrayList/* <Byte> */();
		int csum = 0;

		response.add(new Byte((byte) responseType.getValue()));
		csum += responseType.getValue();
		byte[] moduleAddressAsBytes = moduleAddress.getBytes();
		for (int i = 0; i < moduleAddressAsBytes.length; i++) {
			Byte b = new Byte(moduleAddressAsBytes[i]);
			response.add(b);
			csum += b.intValue();
		}
		response.add(new Byte(newValue.getBytes()[0]));
		csum += newValue.getBytes()[0];
		csum &= 0xff;
		response.add(new Byte((byte) ((csum >> 4) + 0x30)));
		response.add(new Byte((byte) ((csum & 0x0F) + 0x30)));
		response.add(new Byte((byte) '\r'));
		return response;
	}

	/**
	 * Parse the frame type from the frame list of bytes given in parameter.
	 * 
	 * @param sb
	 * @return
	 */
	private char getTrameType(List/* <Byte> */sb) {
		return (char) ((Byte) sb.get(0)).intValue();
	}

	/**
	 * Parse the module address in this frame.
	 * 
	 * @param sb
	 * @return
	 */
	private String parseModuleAddress(List/* <Byte> */sb) {
		return ByteToChar(sb).substring(1, 5);
	}

	/**
	 * Parse the battery level in this frame.
	 * 
	 * @param sb
	 * @return
	 */
	private String parseBatteryLevel(List/* <Byte> */sb) {
		return Character.toString((char) ((Byte) sb.get(sb
				.lastIndexOf(new Byte((byte) 'B')) + 1)).intValue());
	}

	/**
	 * Parse the data value in this frame.
	 * 
	 * @param sb
	 * @return
	 */
	private String parseDataValue(List/* <Byte> */sb) {
		return ByteToChar(sb)
				.substring(5, sb.lastIndexOf(new Byte((byte) 'B')));
	}

	/**
	 * Check if checksum is correct.
	 * 
	 * @param sb
	 * @return
	 */
	private boolean verifyChecksum(List/* <Byte> */sb) {

		int csum, val;

		csum = (((Byte) sb.get(sb.size() - 2)).byteValue() & 0x0F) << 4;
		val = (((Byte) sb.get(sb.size() - 1)).byteValue()) & 0x0F;
		csum += val;

		val = 0;
		for (int i = 0; i < sb.size() - 2; i++) {
			val += ((Byte) sb.get(i)).intValue();
		}
		val = val & 0xff;

		return (csum == val);
	}

	/**
	 * Convert a list of bytes into characters.
	 * 
	 * @param byteList
	 * @return
	 */
	private StringBuffer ByteToChar(List/* <Byte> */byteList) {

		StringBuffer charList = null;
		if (byteList != null && byteList.size() > 0) {
			charList = new StringBuffer();
			for (Iterator byteListIterator = byteList.iterator(); byteListIterator
					.hasNext();) {
				Byte b = (Byte) byteListIterator.next();
				charList.append(new Character((char) b.byteValue()));
			}
		}
		return charList;
	}

	/**
	 * Write a list of bytes into this outputStream.
	 * 
	 * @param data
	 * @throws IOException
	 */
	private void write(List/* <Byte> */data) throws IOException {
		synchronized (streamLock) {
			if (ous != null) {
				for (Iterator dataIt = data.iterator(); dataIt.hasNext();) {
					Byte b = (Byte) dataIt.next();
					ous.write(b.byteValue());
				}
			}
		}
	}

	/**
	 * Read from the serial port. It finish read when the 0x0d(EOS) byte is
	 * read.
	 * 
	 * @return the list of read bytes without the EOS(0x0d) byte.
	 * @throws IOException
	 */
	private List/* <Byte> */read() throws IOException {
		List/* <Byte> */sb = new ArrayList/* <Byte> */();
		byte readByte = 0x0d;
		do {
			synchronized (streamLock) {
				readByte = (byte) ins.read();
				if (readByte != -1 && readByte != 0x0d) { // no data && end of
															// stream.
					sb.add(new Byte(readByte));
				}
			}
		} while (readByte != 0x0d && socketOpened);
		return sb;
	}

	/**
	 * Write a response built from the given informations.
	 * 
	 * @param responseType
	 *            Type of frame to respond to.
	 * @param moduleAddress
	 *            address of module to send response to.
	 * @param newValue
	 *            the new value to send.
	 * @throws IOException
	 */
	public void write(ResponseType responseType, String moduleAddress,
			String newValue) {
		synchronized (streamLock) { // add to queue.
			requestData.put(moduleAddress, newValue);// set expected data
		}
	}

	/**
	 * Handles writing events.
	 */
	private void handleWrite(String module) {
		synchronized (streamLock) {
			List/* <Byte> */dataToSend = handleRequests(module);
			if (dataToSend != null) {
				try {
					write(dataToSend);
				} catch (IOException e) {
					Logger.log(LogService.LOG_ERROR,
							"Unable to write request. ", e);

				}
			}
		}
	}

	private List/* <Byte> */handleRequests(String moduleAddress) {
		String expected = (String) requestData.get(moduleAddress);
		DeviceInfo info = (DeviceInfo) deviceList.get(moduleAddress);
		if (info != null) {
			if (expected != null
					&& info.getDeviceData().getData().compareTo(expected) != 0) {
				Logger.log(LogService.LOG_DEBUG,
						"Resent request expected value");
				List/* <Byte> */response = buildResponseWithNewValue(
						ResponseType.REQUEST, moduleAddress, expected);
				Logger.log(LogService.LOG_DEBUG, "response sent to device "
						+ moduleAddress + " : " + response.toString());
				return (response);
			} else {
				// logger.debug("Sent request value");
				requestData.remove(moduleAddress);
				return (buildResponseWithNewValue(ResponseType.REQUEST,
						moduleAddress, info.getDeviceData().getData()));
			}
		}
		return null;
	}

	/**
	 * Close the serial port streams.
	 */
	private void closeStreams() {
		synchronized (streamLock) {
			if (ins != null) {
				try {
					ins.close();
				} catch (IOException e) {
					Logger.log(LogService.LOG_ERROR,
							"Exception while closing inputstream : ", e);
				}
			}
			if (ous != null) {
				try {
					ous.close();
				} catch (IOException e) {
					Logger.log(LogService.LOG_ERROR,
							"Exception while closing outputstream : ", e);
				}
			}
		}
	}

	/**
	 * Task to extend devices timeouts.
	 * 
	 * 
	 */
	private final class ExtendDeviceTimeoutTask implements Runnable {

		// private ScheduledFuture<?> fSchedFuture;
		private String moduleAddress;

		ExtendDeviceTimeoutTask(/* ScheduledFuture<?> aSchedFuture, */
		String moduleAddress) {
			// fSchedFuture = aSchedFuture;
			this.moduleAddress = moduleAddress;
		}

		public void run() {
			while (true) {
				// extend timeout
				try {
					Thread.sleep(120000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}

				DeviceInfo infos = (DeviceInfo) deviceList.get(moduleAddress);
				if (infos.getLastConnexionDate().getTime() < (new Date()
						.getTime() - 120000)) {
					// last connexion was before 2min, unregister device
					// fSchedFuture.cancel(true);
					deviceList.remove(moduleAddress);
					// notify to trackers.
					notifyDeviceRemoved(infos);
					return;
				} else {

					// Runnable extendDeviceTimeoutTask = new
					// ExtendDeviceTimeoutTask(
					// /*fSchedFuture,*/ moduleAddress);

					// executor.schedule(extendDeviceTimeoutTask, 120,
					// TimeUnit.SECONDS);
				}
			}
		}
	}

	public DeviceInfo getDeviceInfo(String moduleAddress) {
		synchronized (deviceList) {
			return (DeviceInfo) deviceList.get(moduleAddress);
		}
	}

	/**
	 * Notify trackers when there is a new ZigBee device.
	 * 
	 * @param deviceInfo
	 */
	private void notifyDeviceAdded(DeviceInfo deviceInfo) {
		Logger.log(LogService.LOG_INFO, "Device Added");
		logInfo(deviceInfo);
		List/* <SimpleBeeDeviceTracker> */listeners = trackerMgr.getTrackers();
		for (Iterator listenersIt = listeners.iterator(); listenersIt.hasNext();) {
			SimpleBeeDeviceTracker tracker = (SimpleBeeDeviceTracker) listenersIt.next();
			try {
				tracker.deviceAdded(deviceInfo);
			} catch (Exception e) {
				Logger.log(LogService.LOG_DEBUG,
						"Could not notify tracker about new device "
								+ deviceInfo.getModuleAddress(), e);
			}
		}
	}

	/**
	 * Notify trackers when a ZigBee device is no longer available..
	 * 
	 * @param deviceInfo
	 */
	private void notifyDeviceRemoved(DeviceInfo deviceInfo) {
		Logger.log(LogService.LOG_INFO, "Device Removed");
		logInfo(deviceInfo);
		List/* <SimpleBeeDeviceTracker> */listeners = trackerMgr.getTrackers();
		for (Iterator listenersIt = listeners.iterator(); listenersIt.hasNext();) {
			SimpleBeeDeviceTracker tracker = (SimpleBeeDeviceTracker) listenersIt.next();
			try {
				tracker.deviceRemoved(deviceInfo);
			} catch (Exception e) {
				Logger.log(LogService.LOG_ERROR,
						"could not notify tracker about device removal "
								+ deviceInfo.getModuleAddress(), e);
			}
		}
	}

	/**
	 * Notify the new battery level.
	 * 
	 * @param info
	 * @param oldLevel
	 */
	private void notifyBatteryLevelChange(DeviceInfo info, float oldLevel,
			boolean force) {
		if (!force && info.getBatteryLevel() != oldLevel) { // only notify when
															// data has changed.
			Logger.log(LogService.LOG_INFO, "Battery level changed");
			logInfo(info);
			List/* <SimpleBeeDeviceTracker> */listeners = trackerMgr.getTrackers();
			for (Iterator listenersIt = listeners.iterator(); listenersIt
					.hasNext();) {
				SimpleBeeDeviceTracker tracker = (SimpleBeeDeviceTracker) listenersIt
						.next();
				try {
					tracker.deviceBatteryLevelChanged(info.getModuleAddress(),
							oldLevel, info.getBatteryLevel());
				} catch (Exception e) {
					Logger.log(
							LogService.LOG_ERROR,
							"could not notify tracker about battery level change",
							e);
				}
			}
		}
	}

	/**
	 * Notify the data value.
	 * 
	 * @param info
	 * @param oldData
	 */
	private void notifyDataChange(DeviceInfo info, Data oldData, boolean force) {
		String oldDataValue = oldData != null ? oldData.getData() : "";

		if (!force && oldData == null || !force
				&& info.getDeviceData().getData().compareTo(oldDataValue) != 0) { // only
																					// notify
																					// when
																					// data
																					// has
																					// changed.
			Logger.log(LogService.LOG_INFO, "Data changed (Old value:"
					+ oldDataValue + ")");
			logInfo(info);
			List/* <SimpleBeeDeviceTracker> */listeners = trackerMgr.getTrackers();
			for (Iterator listenersIt = listeners.iterator(); listenersIt
					.hasNext();) {
				SimpleBeeDeviceTracker tracker = (SimpleBeeDeviceTracker) listenersIt
						.next();
				try {
					tracker.deviceDataChanged(info.getModuleAddress(), oldData,
							info.getDeviceData());
				} catch (Exception e) {
					Logger.log(LogService.LOG_ERROR,
							"could not notify tracker about data change for device "
									+ info.getModuleAddress(), e);
				}
			}
		}
	}

	/**
	 * log device info.
	 * 
	 * @param deviceInfo
	 */
	private void logInfo(DeviceInfo deviceInfo) {
		try {
			Logger.log(LogService.LOG_INFO,
					"battery : " + deviceInfo.getBatteryLevel());
			Logger.log(LogService.LOG_INFO,
					"ModuleAddress : " + deviceInfo.getModuleAddress());
			Logger.log(LogService.LOG_INFO, "data value : "
					+ deviceInfo.getDeviceData().getData());
			Logger.log(
					LogService.LOG_INFO,
					"type code : " + deviceInfo.getTypeCode() == null ? "unknown"
							: deviceInfo.getTypeCode().getFriendlyName());
		} catch (Exception ex) {
			Logger.log(LogService.LOG_ERROR, "Unable to log DeviceIngo"
					+ deviceInfo.getModuleAddress());
		}
	}
}
