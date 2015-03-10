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

import org.osgi.service.log.LogService;

public class Logger {

	public static LogService logService;

	public Logger() {
	}

	public static void log(int level, String message) {
		if (logService != null) {
			logService.log(level, message);
		}
	}

	public static void log(int level, String message, Throwable t) {
		if (logService != null) {
			logService.log(level, message, t);
		}
	}

	protected void bindLogService(LogService logService) {
		Logger.logService = logService;
	}

	protected void unbindLogService(LogService logService) {
		Logger.logService = null;
	}

}
