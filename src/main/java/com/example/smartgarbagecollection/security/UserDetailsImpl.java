package com.example.smartgarbagecollection.security;

import com.example.smartgarbagecollection.enums.Role;
import com.example.smartgarbagecollection.model.AuthUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final UUID userId;
    private final UUID companyId;
    private final String username;
    private final String password;
    private final Role role;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(AuthUser authUser, UUID companyId) {
        this.userId = authUser.getUser().getId();
        this.companyId = companyId;
        this.username = authUser.getUsername();
        this.password = authUser.getPasswordHash();
        this.role = authUser.getUser().getRole();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
