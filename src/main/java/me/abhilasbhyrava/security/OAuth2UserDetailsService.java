package me.abhilasbhyrava.security;

import lombok.RequiredArgsConstructor;
import me.abhilasbhyrava.model.Provider;
import me.abhilasbhyrava.model.Token;
import me.abhilasbhyrava.model.User;
import me.abhilasbhyrava.model.exception.BaseException;
import me.abhilasbhyrava.repository.TokenRepository;
import me.abhilasbhyrava.repository.UserRepository;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Optional;

//@Component
@RequiredArgsConstructor
public class OAuth2UserDetailsService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final OAuth2AuthorizedClientService clientService;
    private final Map<OAuth2AuthorizedClientId, OAuth2AuthorizedClient> authorizedClients;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException, BaseException {

        OAuth2User user = super.loadUser(userRequest);


        try{
            return checkUserPresent(userRequest, user);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        throw new BaseException(400, "Request invalid!");
    }

    private OAuth2User checkUserPresent(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

        if(!userRequest.getClientRegistration().getRegistrationId().equals("google"))
            throw new BaseException(400, "Invalid provider");

        SecurityOAuth2User securityOAuth2User = new SecurityOAuth2User(oAuth2User.getAttributes());

        Optional<User> user = userRepository.findByEmail(securityOAuth2User.getEmail());

        if(user.isEmpty()){
            return registerUser(securityOAuth2User, userRequest);
        }else{
            return new SecurityUser(user.get(), oAuth2User.getAttributes());
        }
    }

    private OAuth2User registerUser(SecurityOAuth2User securityOAuth2User,OAuth2UserRequest userRequest) {
        User user = new User();
        user.setName(securityOAuth2User.getName());
        user.setEmail(securityOAuth2User.getEmail());
        user.setProvider(Provider.valueOf(userRequest.getClientRegistration().getRegistrationId()));

        user = userRepository.save(user);

        return new SecurityUser(user, securityOAuth2User.getAttributes());
    }
}
