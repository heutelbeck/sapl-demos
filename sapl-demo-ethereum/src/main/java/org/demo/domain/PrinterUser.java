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
package org.demo.domain;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrinterUser extends User {

	private static final long serialVersionUID = -6541109260884369275L;

	private String ethereumAddress;

	private String transactionHash;

	public PrinterUser(String userName, String password, String ethereumAddress, String transactionHash,
			Collection<? extends GrantedAuthority> authorities) {
		super(userName, password, authorities);
		this.ethereumAddress = ethereumAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ethereumAddress == null) ? 0 : ethereumAddress.hashCode());
		result = prime * result + ((transactionHash == null) ? 0 : transactionHash.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrinterUser other = (PrinterUser) obj;
		if (ethereumAddress == null) {
			if (other.ethereumAddress != null)
				return false;
		}
		else if (!ethereumAddress.equals(other.ethereumAddress))
			return false;
		if (transactionHash == null) {
			return other.transactionHash == null;
		}
		else return transactionHash.equals(other.transactionHash);
	}

	public PrinterUser copy() {
		return new PrinterUser(this.getUsername(), this.getPassword(), this.getEthereumAddress(),
				this.getTransactionHash(), this.getAuthorities());
	}

}
