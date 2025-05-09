package karol.przesylki.konduktorskie_przesylki.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import karol.przesylki.konduktorskie_przesylki.tables.Conductor;
import karol.przesylki.konduktorskie_przesylki.tables.Role;

public class ConductorDetails implements UserDetails {

    private Conductor conductor; 

    @Transactional
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return conductor.getRole().stream()
	        		 .map(Role::getStringName)
	                 .map(SimpleGrantedAuthority::new)
	                 .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return conductor.getPassword();
    }

    @Override
    public String getUsername() {
        return conductor.getConductor_indeficator();        
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
       return true;  
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;    
    }

    @Override
    public boolean isEnabled() {
        return true;        
    }

}
