package me.abhilasbhyrava.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.abhilasbhyrava.config.FrontendRequestAttributes;
import me.abhilasbhyrava.model.Provider;
import me.abhilasbhyrava.model.Token;
import me.abhilasbhyrava.model.User;
import me.abhilasbhyrava.repository.TokenRepository;
import me.abhilasbhyrava.repository.UserRepository;
import me.abhilasbhyrava.security.SecurityOAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientRepository clientRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    private FrontendRequestAttributes requestAttributes;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = clientRepository
                .loadAuthorizedClient(authToken.getAuthorizedClientRegistrationId(),
                authToken,
                request);
        SecurityOAuth2User securityOAuth2User = new SecurityOAuth2User(authToken.getPrincipal().getAttributes());

        String frontendCheck = requestAttributes.getHeaderValue();

        this.setAlwaysUseDefaultTargetUrl(true);
        if(frontendCheck == null){
            this.setDefaultTargetUrl("http://localhost:5173");
        }else if(frontendCheck.equals("attendee")){
            this.setDefaultTargetUrl("http://localhost:4200");
            requestAttributes.setHeaderValue(null);
        }
//        Authentication auth = (Authentication) authentication.getPrincipal();
        super.onAuthenticationSuccess(request, response, authentication);

        Optional<User> prevUser = userRepository.findByEmail(securityOAuth2User.getEmail());

        User user = null;

        if(prevUser.isEmpty()) {

            user = new User();
            user.setName(securityOAuth2User.getName());
            user.setEmail(securityOAuth2User.getEmail());
            user.setProvider(Provider.valueOf(authToken.getAuthorizedClientRegistrationId()));

            Token token = new Token();
            token.setAccessToken(client.getAccessToken().getTokenValue());
            if (!ObjectUtils.isEmpty(client.getRefreshToken()))
                token.setRefreshToken(client.getRefreshToken().getTokenValue());
            token = tokenRepository.save(token);
            user.setToken(token);

        }else{

            user = prevUser.get();
            Token token = user.getToken();
            token.setAccessToken(client.getAccessToken().getTokenValue());
            if (!ObjectUtils.isEmpty(client.getRefreshToken()))
                token.setRefreshToken(client.getRefreshToken().getTokenValue());
            token = tokenRepository.save(token);
            user.setToken(token);

        }

        userRepository.save(user);

    }
}
