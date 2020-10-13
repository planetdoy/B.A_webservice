package com.breakaway.admin.BA_website.config.auth;

import com.breakaway.admin.BA_website.config.auth.dto.OAuthAttributes;
import com.breakaway.admin.BA_website.config.auth.dto.SessionUser;
import com.breakaway.admin.BA_website.domain.user.User;
import com.breakaway.admin.BA_website.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest,OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();    // (1)
        String userNameAttributeName = userRequest
                .getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();                          // (2)

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,oAuth2User.getAttributes()); //(3)

        User user = saveOrUpdate(attributes);

        httpSession.setAttribute("user",new SessionUser(user));                       // (4)

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes){
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
/*
    1. registrationId
        - 현재 로그인 진행 중인 서비스를 구분하는 코드입니다.
        - 지금은 구글만 사용하는 불필요한 값이지만, 이후 네이버 로그인 연동시에 네이버 로그인인지, 구글 로그인인지 구분하기 위해 사용합니다.
    2. userNameAttributeName
        - OAuth2 로그인 진랭 시 키가 되는 필드값을 이야기합니다. Primary Key 와 같은 의미입니다.
        - 구글의 경우 기본적으로 코드를 지원하지만 , 네이버 카카오 등은 기본 지원하지 않습니다. 구글의 기본 코드는 "sub"입니다.
        - 이후 네이버, 구글 로그인을 동시 지원할 때 사용됩니다.
    3. OAuthAttributes
        - OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스입니다.
        - 이후 네이버 등 다른 소셜 로그인도 이 클래스를 사용합니다.
    4. SessionUser
        - 세션에 사용자 정보를 저장하기 위한 Dto 클래스입니다.
        - 왜 User 클래스를 쓰지 않고 새로 만들어서 쓰는가??
            - User 클래스를 세션에 저장하려고 하니 User클래스에 직렬화를 구현하지 않았다는 의미의 에러가 발생합니다.
            그렇다고 해서 User 클래스에 직렬화를 코드를 넣게 되면 User클래스가 entity이기 때문에 만약 @OneToMany, @ManyToMany 등
            자식 엔티티를 갖고 있다면 직렬화 대상에 자식들 까지 포함이 되니 성능 이슈, 부수효과가 발생할 확률이 높아지기 때문에
            직렬화 기능을 가진 세션 Dto를 하나 추가로 만드는 것이 이후 운영 및 유지보수 때 많은 도움이 됩니다.

* */