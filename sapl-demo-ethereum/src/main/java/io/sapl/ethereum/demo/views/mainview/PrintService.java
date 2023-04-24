/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.ethereum.demo.views.mainview;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import com.vaadin.flow.component.notification.Notification;

@Service
public class PrintService implements Serializable {

	private static final long serialVersionUID = -268367144866825840L;

	public void print(String template) {
		if (template == null || template.isEmpty()) {
			Notification.show("Please select a template");
		} else {
			try {
				Thread.sleep(3000L);
				Notification.show("Print job successful");
			} catch (InterruptedException e) {
				Notification.show("Error while printing");
				Thread.currentThread().interrupt();
			}
		}
	}

}
