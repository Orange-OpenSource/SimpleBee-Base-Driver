/*
 * Software Name : m2m.simplebee.application
 *
 * Module name: m2m.simplebee.application
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
package com.orange.simplebee.simple.application;

import com.orange.simplebee.SimpleBeeDevice;
import com.orange.simplebee.SimpleBeeListener;

public class SimpleBeeSandTimer implements SimpleBeeListener {

	public SimpleBeeSandTimer() {
	}

	public void updateData(SimpleBeeDevice simpleBeeDevice, String oldData,
			String newData) {
		System.out.println("new notif : newData=" + newData + ", oldData="
				+ oldData);
	}

	public void updateBatteryLevel(SimpleBeeDevice simpleBeeDevice,
			float oldBatteryLevel, float newBatteryLevel) {
	}

	public void bindSandTimer(SimpleBeeDevice sandTimer) {
		System.out.println("bind SandTimer");

		System.out.println("sandtimer getData() =" + sandTimer.getData());

		System.out.println("setData :");
		sandTimer.setData("6");
	}

	public void unbindSandTimer(SimpleBeeDevice sandTimer) {

	}

}
