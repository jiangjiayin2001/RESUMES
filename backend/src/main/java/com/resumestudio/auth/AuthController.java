package com.resumestudio.auth;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.resumestudio.common.ApiPaths;
import com.resumestudio.common.BadRequestException;
import com.resumestudio.config.properties.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping(ApiPaths.AUTH_BASE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final LoginAttemptStore loginAttemptStore;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of("headerName", token.getHeaderName(), "parameterName", token.getParameterName());
    }

    @PostMapping("/login")
    public AuthUser login(
            @Valid @RequestBody LoginRequest body,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        validateLoginRequest(body);
        String clientIp = clientIp(request);
        loginAttemptStore.assertAllowed(body.username(), clientIp);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(body.username().trim(), body.password())
            );
            if (request.getSession(false) != null) {
                request.changeSessionId();
            }
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
            loginAttemptStore.clear(body.username(), clientIp);
            return AuthUser.from((AppUserPrincipal) authentication.getPrincipal());
        } catch (AuthenticationException exception) {
            loginAttemptStore.recordFailure(body.username(), clientIp);
            throw new BadCredentialsException("账号或密码错误");
        }
    }

    @GetMapping("/me")
    public AuthUser me(@AuthenticationPrincipal AppUserPrincipal principal) {
        return AuthUser.from(principal);
    }

    @PostMapping("/password")
    @Transactional
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest body
    ) {
        validateChangePasswordRequest(body);
        AppUser user = appUserMapper.selectById(principal.getUserId());
        if (user == null) {
            throw new BadRequestException("当前账号不存在");
        }
        if (!passwordEncoder.matches(body.currentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("当前密码不正确");
        }

        appUserMapper.update(null, Wrappers.<AppUser>lambdaUpdate()
                .eq(AppUser::getId, principal.getUserId())
                .set(AppUser::getPasswordHash, passwordEncoder.encode(body.newPassword()))
                .set(AppUser::getUpdatedAt, LocalDateTime.now()));
        return ResponseEntity.noContent().build();
    }

    private void validateLoginRequest(LoginRequest body) {
        AppProperties.Security security = appProperties.getSecurity();
        if (body.username().length() > security.getUsernameMaxLength()) {
            throw new BadRequestException("账号长度超过配置上限");
        }
        if (body.password().length() > security.getPasswordMaxLength()) {
            throw new BadRequestException("密码长度超过配置上限");
        }
    }

    private void validateChangePasswordRequest(ChangePasswordRequest body) {
        AppProperties.Security security = appProperties.getSecurity();
        if (body.currentPassword().length() > security.getPasswordMaxLength()) {
            throw new BadRequestException("当前密码长度超过配置上限");
        }
        int newPasswordLength = body.newPassword().length();
        if (newPasswordLength < security.getNewPasswordMinLength()
                || newPasswordLength > security.getNewPasswordMaxLength()) {
            throw new BadRequestException("新密码长度不符合配置要求");
        }
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {
    }

    public record ChangePasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank String newPassword
    ) {
    }

    public record AuthUser(Long id, String username, String displayName) {
        public static AuthUser from(AppUserPrincipal principal) {
            return new AuthUser(principal.getUserId(), principal.getUsername(), principal.getDisplayName());
        }
    }
}






