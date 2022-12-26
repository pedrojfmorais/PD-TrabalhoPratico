package pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.LoginStatus;
import pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.Application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        LoginStatus loginStatus;

        try {
            loginStatus = Application.connDB.verifyLogin(username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (loginStatus == LoginStatus.SUCCESSFUL_ADMIN_USER || loginStatus == LoginStatus.SUCCESSFUL_NORMAL_USER) {
            List<GrantedAuthority> authorities = new ArrayList<>();

            if (loginStatus == LoginStatus.SUCCESSFUL_ADMIN_USER)
                authorities.add(new SimpleGrantedAuthority("ADMIN"));
            else
                authorities.add(new SimpleGrantedAuthority("USER"));

            return new UsernamePasswordAuthenticationToken(username, password, authorities);
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
