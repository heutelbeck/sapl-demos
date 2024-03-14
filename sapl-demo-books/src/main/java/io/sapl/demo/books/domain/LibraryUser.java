package io.sapl.demo.books.domain;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LibraryUser extends org.springframework.security.core.userdetails.User implements UserDetails {

    private static final long serialVersionUID = -7244331453519181420L;

    @Getter
    private int           department;
    @Getter
    private List<Integer> dataScope = List.of();

    public LibraryUser(String username, int department, List<Integer> dataScope, String password) {
        super(username, password, true, true, true, true, List.of());
        this.department = department;
        this.dataScope  = dataScope;
    }

}
