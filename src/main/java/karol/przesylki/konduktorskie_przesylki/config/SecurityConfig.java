package karol.przesylki.konduktorskie_przesylki.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import karol.przesylki.konduktorskie_przesylki.UserDetails.ConductorDetailsServiceImpl;
import karol.przesylki.konduktorskie_przesylki.repository.ConductorRepository;
import karol.przesylki.konduktorskie_przesylki.tables.Conductor;
import karol.przesylki.konduktorskie_przesylki.tables.ERole;
import karol.przesylki.konduktorskie_przesylki.tables.Role;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private ConductorRepository conductorRepo;

    @Bean
    public UserDetailsService userDetailsService() {
        return new ConductorDetailsServiceImpl();
    }
     
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
     
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
         
        return authProvider;
    }

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		Conductor admin = new Conductor();
		admin.setConductorIndeficator("admin");
		admin.setPassword(passwordEncoder().encode("admin#01"));
		admin.setName("admin");

		Role role = new Role();
		role.setName(ERole.ROLE_ADMIN);
			
		Set<Role> roles =new HashSet();
		roles.add(role);

		admin.setRole(roles);
		admin.setId(1);

		conductorRepo.save(admin);
	}   
	
	@Override
	    protected void configure(HttpSecurity http) throws Exception { 
		  http
		  	.authenticationProvider(authenticationProvider())
	        .cors().and()
	        .csrf().disable()
	        .authorizeRequests()
	        //.antMatchers("/company/**").hasRole("COMPANY")
	        //.antMatchers("/traincar/view/detail/**").hasRole("RAILWAY")
	        .antMatchers("/admin/**").hasRole("ADMIN")
	        .antMatchers("**").permitAll()
	        .and()
	        .formLogin();
	    }
	  
	  /**
	   * Allows access to static resources, bypassing Spring security.
	   */
	  @Override
	  public void configure(WebSecurity web) throws Exception {
	      web.ignoring().antMatchers(
	              // Vaadin Flow static resources // (1)
	              "/VAADIN/**",

	              // the standard favicon URI
	              "/favicon.ico",

	              // the robots exclusion standard
	              "/robots.txt",

	              // web application manifest // (2)
	              "/manifest.webmanifest",
	              "/sw.js",
	              "/offline-page.html",

	              // (development mode) static resources // (3)
	              "/frontend/**",

	              // (development mode) webjars // (3)
	              "/webjars/**",

	              // (production mode) static resources // (4)
	              "/frontend-es5/**", "/frontend-es6/**");
	  }
}
