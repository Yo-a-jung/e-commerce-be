package com.yoajung.jobplanner.security.thirdparty.unlink;

import com.yoajung.jobplanner.security.mapper.UserInfoMapper;
import com.yoajung.jobplanner.security.thirdparty.exception.OAuth2AuthenticationProcessingException;
import com.yoajung.jobplanner.security.thirdparty.user.OAuth2Provider;
import com.yoajung.jobplanner.security.thirdparty.user.OAuth2UserInfo;
import com.yoajung.jobplanner.signin.user.domain.UserInfoEntity;
import com.yoajung.jobplanner.signin.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuth2UserUnlinkManager {

    private final GoogleOAuth2UserUnlink googleOAuth2UserUnlink;
    private final KakaoOAuth2UserUnlink kakaoOAuth2UserUnlink;
    private final NaverOAuth2UserUnlink naverOAuth2UserUnlink;
    private final UserInfoService userInfoService;

    public void unlink(OAuth2Provider provider, String accessToken, OAuth2UserInfo oAuth2UserInfo) {
        deleteOAuth2User(oAuth2UserInfo);
        if (provider.equals(OAuth2Provider.GOOGLE)) {
            googleOAuth2UserUnlink.unlink(accessToken);
        } else if (provider.equals(OAuth2Provider.NAVER)) {
            naverOAuth2UserUnlink.unlink(accessToken);
        } else if (provider.equals(OAuth2Provider.KAKAO)) {
            kakaoOAuth2UserUnlink.unlink(accessToken);
        } else {
            throw new OAuth2AuthenticationProcessingException(
                    "Unlink with " + provider.getRegistrationId() + " is not supported");
        }
    }

    private void deleteOAuth2User(OAuth2UserInfo oAuth2UserInfo){
        UserInfoEntity userInfoByEmailAndLoginSource = userInfoService.findUserInfoByEmailAndLoginSource(oAuth2UserInfo.getEmail(), UserInfoMapper.getLoginSource(oAuth2UserInfo));
        userInfoService.deleteUserInfo(userInfoByEmailAndLoginSource);
    }
}
