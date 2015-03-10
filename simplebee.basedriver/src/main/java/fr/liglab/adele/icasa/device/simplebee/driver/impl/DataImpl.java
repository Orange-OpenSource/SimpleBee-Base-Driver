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

import java.util.Date;

import fr.liglab.adele.icasa.device.simplebee.driver.Data;

/**
 *
 */
public class DataImpl implements Data {

	private String data;
	private Date timeStamp;

	public void setData(String data) {
		this.data = data;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.habits.monitoring.zigbee.driver.Data#getTimeStamp()
	 */
	public Date getTimeStamp() {
		return this.timeStamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.habits.monitoring.zigbee.driver.Data#getData()
	 */
	public String getData() {
		return this.data;
	}

}
