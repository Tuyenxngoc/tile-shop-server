package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.RoleConstant;
import com.example.tileshop.constant.SuccessMessage;
import com.example.tileshop.dto.auth.*;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.common.DataMailDTO;
import com.example.tileshop.entity.Cart;
import com.example.tileshop.entity.Customer;
import com.example.tileshop.entity.Role;
import com.example.tileshop.entity.User;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.ConflictException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.exception.UnauthorizedException;
import com.example.tileshop.repository.CartRepository;
import com.example.tileshop.repository.CustomerRepository;
import com.example.tileshop.repository.RoleRepository;
import com.example.tileshop.repository.UserRepository;
import com.example.tileshop.security.CustomUserDetails;
import com.example.tileshop.security.jwt.JwtTokenProvider;
import com.example.tileshop.service.AuthService;
import com.example.tileshop.service.CustomUserDetailsService;
import com.example.tileshop.service.EmailRateLimiterService;
import com.example.tileshop.service.JwtBlacklistService;
import com.example.tileshop.util.JwtUtil;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.RandomPasswordUtil;
import com.example.tileshop.util.SendMailUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    AuthenticationManager authenticationManager;

    JwtTokenProvider jwtTokenProvider;

    JwtBlacklistService jwtBlacklistService;

    EmailRateLimiterService emailRateLimiterService;

    MessageUtil messageUtil;

    UserRepository userRepository;

    CustomerRepository customerRepository;

    CartRepository cartRepository;

    PasswordEncoder passwordEncoder;

    RoleRepository roleRepository;

    SendMailUtil sendMailUtil;

    CustomUserDetailsService customUserDetailsService;

    private void sendEmail(String to, String subject, Map<String, Object> properties, String templateName) {
        DataMailDTO dataMailDTO = new DataMailDTO();
        dataMailDTO.setTo(to);
        dataMailDTO.setSubject(subject);
        dataMailDTO.setProperties(properties);

        CompletableFuture.runAsync(() -> {
            try {
                sendMailUtil.sendEmailWithHTML(dataMailDTO, templateName);
            } catch (MessagingException e) {
                log.error("Failed to send email to [{}] with subject [{}]. Error: {}", to, subject, e.getMessage(), e);
            } catch (Exception e) {
                log.error("Unexpected error while sending email to [{}]. Error: {}", to, e.getMessage(), e);
            }
        });
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            String accessToken = jwtTokenProvider.generateToken(customUserDetails, false);
            String refreshToken = jwtTokenProvider.generateToken(customUserDetails, true);

            return new LoginResponseDTO(accessToken, refreshToken);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException(ErrorMessage.Auth.ERR_INCORRECT_USERNAME_PASSWORD);
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(ErrorMessage.ERR_EXCEPTION_GENERAL);
        }
    }

    @Override
    public CommonResponseDTO logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String accessToken = JwtUtil.extractTokenFromRequest(request);
        String refreshToken = JwtUtil.extractRefreshTokenFromRequest(request);

        if (accessToken != null) {
            // Lưu accessToken vào blacklist
            jwtBlacklistService.blacklistAccessToken(accessToken);
        }

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken) && jwtTokenProvider.isRefreshToken(refreshToken)) {
            // Lưu refreshToken vào blacklist
            jwtBlacklistService.blacklistRefreshToken(refreshToken);
        }

        SecurityContextLogoutHandler logout = new SecurityContextLogoutHandler();
        logout.logout(request, response, authentication);

        String message = messageUtil.getMessage(SuccessMessage.Auth.LOGOUT);
        return new CommonResponseDTO(message);
    }

    @Override
    public TokenRefreshResponseDTO refresh(TokenRefreshRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken) ||
                !jwtTokenProvider.isRefreshToken(refreshToken) ||
                jwtBlacklistService.isTokenBlocked(refreshToken)) {
            throw new BadRequestException(ErrorMessage.Auth.ERR_INVALID_REFRESH_TOKEN);
        }

        String userId = jwtTokenProvider.extractSubjectFromJwt(refreshToken);
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUserId(userId);

        String newAccessToken = jwtTokenProvider.generateToken(userDetails, false);
        String newRefreshToken = jwtTokenProvider.generateToken(userDetails, true);

        jwtBlacklistService.blacklistRefreshToken(refreshToken);

        return new TokenRefreshResponseDTO(newAccessToken, newRefreshToken);
    }

    @Override
    public CommonResponseDTO forgotPassword(ForgotPasswordRequestDTO requestDTO) {
        User user = userRepository.findByUsernameAndEmail(requestDTO.getUsername(), requestDTO.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ACCOUNT));

        // Kiểm tra giới hạn thời gian gửi email
        if (emailRateLimiterService.isMailLimited(requestDTO.getEmail())) {
            throw new BadRequestException(ErrorMessage.User.RATE_LIMIT);
        }

        String newPassword = RandomPasswordUtil.random(6);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", requestDTO.getUsername());
        properties.put("newPassword", newPassword);
        sendEmail(user.getEmail(), "Lấy lại mật khẩu", properties, "forgotPassword.html");

        emailRateLimiterService.setMailLimit(requestDTO.getEmail(), 1, TimeUnit.MINUTES);

        String message = messageUtil.getMessage(SuccessMessage.User.FORGOT_PASSWORD);
        return new CommonResponseDTO(message);
    }

    @Override
    public CommonResponseDTO changePassword(ChangePasswordRequestDTO requestDTO, String userId) {
        if (!requestDTO.getPassword().equals(requestDTO.getRepeatPassword())) {
            throw new BadRequestException(ErrorMessage.INVALID_REPEAT_PASSWORD);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_USERNAME, userId));

        boolean isCorrectPassword = passwordEncoder.matches(requestDTO.getOldPassword(), user.getPassword());
        if (!isCorrectPassword) {
            throw new BadRequestException(ErrorMessage.Auth.ERR_INCORRECT_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        userRepository.save(user);

        Map<String, Object> properties = new HashMap<>();
        properties.put("currentTime", new Date());
        sendEmail(user.getEmail(), "Đổi mật khẩu thành công", properties, "changePassword.html");

        String message = messageUtil.getMessage(SuccessMessage.User.CHANGE_PASSWORD);
        return new CommonResponseDTO(message);
    }

    @Override
    public CurrentUserLoginResponseDTO getCurrentUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, userId));

        return CurrentUserLoginResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .roleNames(Set.of(user.getRole().getCode()))
                .build();
    }

    @Override
    public CommonResponseDTO register(RegisterRequestDTO requestDTO) {
        if (!requestDTO.getPassword().equals(requestDTO.getRepeatPassword())) {
            throw new BadRequestException(ErrorMessage.INVALID_REPEAT_PASSWORD);
        }
        boolean isUsernameExists = userRepository.existsByUsername(requestDTO.getUsername());
        if (isUsernameExists) {
            throw new ConflictException(ErrorMessage.Auth.ERR_DUPLICATE_USERNAME);
        }
        boolean isEmailExists = userRepository.existsByEmail(requestDTO.getEmail());
        if (isEmailExists) {
            throw new ConflictException(ErrorMessage.Auth.ERR_DUPLICATE_EMAIL);
        }

        //Lấy ra quyền người dùng
        Role role = roleRepository.findByCode(RoleConstant.ROLE_USER).orElseThrow(() -> new RuntimeException("Role ROLE_USER not found"));

        //Tạo mới user
        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setEmail(requestDTO.getEmail());
        user.setRole(role);
        userRepository.save(user);

        //Tạo mới customer
        Customer customer = new Customer();
        customer.setFullName(requestDTO.getFullName());
        customer.setPhoneNumber(requestDTO.getPhoneNumber());
        customer.setUser(user);
        customerRepository.save(customer);

        //Tạo mới giỏ hàng
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cartRepository.save(cart);

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", requestDTO.getUsername());
        properties.put("password", requestDTO.getPassword());

        sendEmail(requestDTO.getEmail(), "Đăng ký thành công", properties, "registerSuccess.html");

        String message = messageUtil.getMessage(SuccessMessage.User.REGISTER);
        return new CommonResponseDTO(message, user);
    }

}
