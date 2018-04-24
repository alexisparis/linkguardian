package org.blackdog.linkguardian.config;

import org.blackdog.linkguardian.security.*;
import org.blackdog.linkguardian.security.jwt.*;

import org.blackdog.linkguardian.web.filter.CsrfCookieGeneratorFilter;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import javax.annotation.PostConstruct;

@Configuration
@Import(SecurityProblemSupport.class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserDetailsService userDetailsService;

    private final TokenProvider tokenProvider;

    private final CorsFilter corsFilter;

    private final SecurityProblemSupport problemSupport;

    public SecurityConfiguration(AuthenticationManagerBuilder authenticationManagerBuilder, UserDetailsService userDetailsService,TokenProvider tokenProvider,CorsFilter corsFilter, SecurityProblemSupport problemSupport) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDetailsService = userDetailsService;
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.problemSupport = problemSupport;
    }

    @PostConstruct
    public void init() {
        try {
            authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/app/**/*.{js,html}")
            .antMatchers("/bower_components/**")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/swagger-ui/index.html")
            .antMatchers("/test/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
            .disable()
//        .and()
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
//            .addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint(problemSupport)
            .accessDeniedHandler(problemSupport)
        .and()
            .headers()
            .defaultsDisabled()
            .cacheControl()
        .and()
            .frameOptions()
            .deny()
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
            .antMatchers("/api/register").permitAll()
            .antMatchers("/api/activate").permitAll()
            .antMatchers("/api/authenticate").permitAll()
            .antMatchers("/api/account").permitAll()
            .antMatchers("/api/account/reset-password/init").permitAll()
            .antMatchers("/api/account/reset-password/finish").permitAll()
            .antMatchers("/api/profile-info").permitAll()
//            .antMatchers("/api/**").authenticated()

            // special links that should be allowed to every one connected
            .antMatchers("/api/account/change-password").hasAuthority(AuthoritiesConstants.USER)

            .antMatchers(HttpMethod.GET, "/api/my_links").hasAuthority(AuthoritiesConstants.USER)
            .antMatchers(HttpMethod.POST, "/api/my_links").hasAuthority(AuthoritiesConstants.USER)
            .antMatchers(HttpMethod.POST, "/api/my_links/manual").hasAuthority(AuthoritiesConstants.USER)

            .antMatchers(HttpMethod.DELETE, "/api/my_links").hasAuthority(AuthoritiesConstants.USER)

            .antMatchers(HttpMethod.PUT, "/api/my_links/tag").hasAuthority(AuthoritiesConstants.USER)
            .antMatchers(HttpMethod.DELETE, "/api/my_links/tag").hasAuthority(AuthoritiesConstants.USER)

            .antMatchers(HttpMethod.PUT, "/api/my_links/note").hasAuthority(AuthoritiesConstants.USER)

            .antMatchers(HttpMethod.PUT, "/api/my_links/read").hasAuthority(AuthoritiesConstants.USER)
            .antMatchers(HttpMethod.PUT, "/api/my_links/unread").hasAuthority(AuthoritiesConstants.USER)

            .antMatchers(HttpMethod.GET, "/api/my_links/count_per_tags").hasAuthority(AuthoritiesConstants.USER)

            // else... only admin
            .antMatchers("/api/**").hasAuthority(AuthoritiesConstants.ADMIN)

            .antMatchers("/websocket/tracker").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/websocket/**").permitAll()
            .antMatchers("/management/health").permitAll()
            .antMatchers("/management/mail").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/v2/api-docs/**").permitAll()
            .antMatchers("/swagger-resources/configuration/ui").permitAll()
            .antMatchers("/swagger-ui/index.html").hasAuthority(AuthoritiesConstants.ADMIN)
        .and()
            .apply(securityConfigurerAdapter());

    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }

}
