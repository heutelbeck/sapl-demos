package org.demo.tenants;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
public class TenantAwareUserDetails extends User {

	@Getter
	String tenantId;

	public TenantAwareUserDetails(String tenantId, String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.tenantId = tenantId;
	}

	public TenantAwareUserDetails(String tenantId, String username, String password, boolean enabled,
			boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.tenantId = tenantId;
	}

}
