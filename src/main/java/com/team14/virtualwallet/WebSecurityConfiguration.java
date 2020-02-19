package com.team14.virtualwallet;

import com.team14.virtualwallet.services.contracts.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UsersService usersService;

    private PasswordEncoder passwordEncoder;

    public WebSecurityConfiguration(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .httpBasic()
                .and()

                    .authorizeRequests()
                    .antMatchers("/", "/index", "/login", "/register", "/verification", "/email-confirm",
                        "/verified", "/api/v1/public/**").permitAll()
                .antMatchers("/home/**", "/user/**", "/newtransaction/**", "/api/v1/private/**", "/swagger-resources/**",
                        "/swagger-ui.html", "/swagger.json").hasAuthority("USER")
                    .antMatchers("/admin/**").hasAuthority("ADMIN")
                .and()
                    .formLogin()
                    .loginPage("/login")
                       .usernameParameter("username")
                       .passwordParameter("password")
                    .defaultSuccessUrl("/home")
                .and()
                    .logout()
                    .logoutSuccessUrl("/")
                .and()
                    .csrf()
                    .disable();

    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository =
                new HttpSessionCsrfTokenRepository();
        repository.setSessionAttributeName("_csrf");
        return repository;
    }

    @Override
    public UserDetailsService userDetailsService() {
        return usersService;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(usersService)
                .passwordEncoder(passwordEncoder);
    }
}
