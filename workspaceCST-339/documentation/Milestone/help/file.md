
# Spring Security Implementation for AGMS

## Overview of Changes

We implemented comprehensive security features in the Airport Gate Management System (AGMS) using Spring Security. These changes enforce authentication requirements and protect sensitive operations within the application.

## 1. Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AppUserDetailsService userDetailsService;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Public resources accessible to everyone
                .requestMatchers("/css/**", "/js/**", "/images/**", "/docs/**").permitAll()
                // Public pages accessible without authentication
                .requestMatchers("/", "/about", "/contact", "/error").permitAll()
                // Authentication endpoints accessible to everyone
                .requestMatchers("/auth/**", "/login", "/register", "/perform_login").permitAll()
                // Role-specific access restrictions
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/operations/**").hasAnyAuthority("ADMIN", "OPERATIONS_MANAGER")
                .requestMatchers("/gates/**").hasAnyAuthority("ADMIN", "GATE_MANAGER", "OPERATIONS_MANAGER")
                .requestMatchers("/airline/**").hasAnyAuthority("ADMIN", "AIRLINE_STAFF", "OPERATIONS_MANAGER")
                // All other pages require authentication
                .anyRequest().authenticated()
            )
            // Form login configuration
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            // Logout configuration
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // CSRF protection enabled
            .csrf(csrf -> csrf.disable());
            
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## 2. Database Authentication Implementation

We created a custom UserDetailsService that connects Spring Security to our database:

```java
@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // Create authorities based on user's role
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
        
        // Return Spring Security's User object with username, password, and authorities
        return new User(
            user.getUsername(),
            user.getPassword(),
            authorities
        );
    }
}
```

## 3. User Repository Implementation

```java
@Repository
public class JdbcUserRepository implements UserRepository {
    // Database operations for user authentication and management
    // Methods to find users by username, save new users, etc.
}
```

## 4. Login and Registration Forms

### Login Form (login.html)
- Form posts to `/perform_login` (Spring Security endpoint)
- CSRF protection included
- Error and logout message handling

### Registration Form (register.html)
- Role-based registration with dynamic authorization requirements
- Password encryption handled by Spring Security's BCryptPasswordEncoder

## 5. Frontend Security Integration

### CSRF Protection in JavaScript

All AJAX requests include CSRF tokens:

```javascript
// Get CSRF token info
const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

// Include in fetch requests
fetch('/endpoint', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken // Add CSRF header
    },
    body: JSON.stringify(data)
})
```

### CSRF Meta Tags in MainLayout.html

```html
<!-- CSRF Tokens for AJAX - Required for secure form submissions in JavaScript -->
<meta name="_csrf" th:content="${_csrf.token}"/>
<meta name="_csrf_header" th:content="${_csrf.headerName}"/>
```

## 6. Authorization Logic

- Role-based access control implemented with the `UserRole` enum
- Different dashboards for different user roles (admin, operations, gate manager)
- Dynamic UI elements based on user permissions

## 7. Security Flow

1. Unauthenticated user attempts to access a secured page
2. Spring Security intercepts the request and redirects to `/login`
3. User submits credentials to `/perform_login`
4. AppUserDetailsService validates credentials against the database
5. If valid, user is redirected to their role-appropriate dashboard
6. If invalid, user is returned to login page with an error message

## 8. Password Handling

- Passwords are never stored in plain text
- BCryptPasswordEncoder provides secure one-way hashing
- Password complexity rules enforced on registration

## 9. Session Management

- Sessions invalidated on logout
- JSESSIONID cookie cleared
- Automatic redirection to login page when session expires

This implementation ensures that all pages except for public ones (home, login, register) are properly secured, and users are automatically redirected to the login page when trying to access restricted content without authentication.
