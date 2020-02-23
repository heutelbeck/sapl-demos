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
			if (other.transactionHash != null)
				return false;
		}
		else if (!transactionHash.equals(other.transactionHash))
			return false;
		return true;
	}

	public PrinterUser copy() {
		return new PrinterUser(this.getUsername(), this.getPassword(), this.getEthereumAddress(),
				this.getTransactionHash(), this.getAuthorities());
	}

}
