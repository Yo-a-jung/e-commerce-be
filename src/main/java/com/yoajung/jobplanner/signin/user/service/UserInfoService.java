package com.yoajung.jobplanner.signin.user.service;

import com.yoajung.jobplanner.signin.user.domain.UserInfoEntity;
import com.yoajung.jobplanner.signin.user.domain.enums.LoginSource;
import com.yoajung.jobplanner.signin.user.dto.UserInfoModifyDTO;
import com.yoajung.jobplanner.signin.user.dto.UserInfoSignUpDTO;
import com.yoajung.jobplanner.signin.user.exception.UserAlreadyExistException;
import com.yoajung.jobplanner.signin.user.exception.UserNotFoundException;
import com.yoajung.jobplanner.signin.user.mapper.UserInfoEntityMapper;
import com.yoajung.jobplanner.signin.user.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = false)
    public UserInfoEntity modifyUserInfo(UserInfoModifyDTO userInfoModifyDTO, Long id) {
        UserInfoEntity userInfoById = this.findUserInfoById(id);
        UserInfoEntity userInfo = UserInfoEntityMapper.toUserInfoEntity(userInfoById, userInfoModifyDTO);
        return userInfoRepository.save(userInfo);
    }

    @Transactional(readOnly = false)
    public UserInfoEntity signUp(UserInfoSignUpDTO userInfoSignUpRequestDTO) {
        UserInfoEntity userInfoEntity = UserInfoEntityMapper.toUserInfo(userInfoSignUpRequestDTO);
        Boolean doesUserExist = userInfoRepository.existsByEmailAndLoginSource(userInfoEntity.getEmail(),
                userInfoEntity.getLoginSource());
        if (doesUserExist) {
            throw new UserAlreadyExistException(userInfoEntity.getEmail());
        } else {
            userInfoEntity.setPassword(passwordEncoder.encode(userInfoEntity.getPassword()));
            userInfoRepository.save(userInfoEntity);
            return userInfoEntity;
        }
    }

    public UserInfoEntity findUserInfoById(Long id) {
        return userInfoRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found With Given ID"));
    }

    public UserInfoEntity findUserInfoByEmailAndLoginSource(String email, LoginSource source)
            throws UserNotFoundException {
        Optional<UserInfoEntity> userInfoEntityOptional = userInfoRepository.findByEmailAndLoginSource(email, source);
        return userInfoEntityOptional.orElseThrow(
                () -> new UserNotFoundException("User Not Found With Given Email and Login source"));
    }

    @Transactional(readOnly = false)
    public UserInfoEntity saveUserInfo(UserInfoEntity userInfoEntity) {
        return userInfoRepository.save(userInfoEntity);
    }

    @Transactional(readOnly = false)
    public void deleteUserInfo(UserInfoEntity userInfoEntity) {
        userInfoRepository.delete(userInfoEntity);
    }
}
