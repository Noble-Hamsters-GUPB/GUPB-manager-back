package com.gupb.manager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private DetailsService detailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(detailsService).passwordEncoder(PASSWORD_ENCODER);
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.httpBasic().and().authorizeRequests()
//                .antMatchers(HttpMethod.OPTIONS, "/**").hasRole("ADMIN")
//                .and()
//                .logout().permitAll()
//                .and()
//                .csrf().disable();
//        //http.headers().frameOptions().disable();
//    }
@Override
protected void configure(HttpSecurity http) throws Exception {
//    http
//            .csrf().disable()
//            .authorizeRequests()
//            .antMatchers("/api/v1/auth").permitAll()
//            .antMatchers("/**").hasAuthority("ADMIN")
//            .anyRequest().authenticated()
////            .logout().permitAll()
//            .and().cors().configurationSource(corsConfigurationSource());//and()
////            .formLogin();
////            .httpBasic();
//    }

    http.cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests().antMatchers("/api/v1/auth/**").permitAll()
            .antMatchers("/api/v1/test/**").permitAll()
            .anyRequest().authenticated();

    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
}

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
//        config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
//        config.setAllowCredentials(true);
//        config.setAllowedHeaders(Arrays.asList("Content-Type","Autorization"));
//
//        UrlBasedCorsConfigurationSource source= new UrlBasedCorsConfigurationSource();
//
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
//
//    @Bean
//    public FilterRegistrationBean<CorsFilter> corsFilter(){
//        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(corsConfigurationSource()));
//        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        return bean;
//    }
}
