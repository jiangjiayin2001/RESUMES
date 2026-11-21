package com.resumestudio.resume;

import com.resumestudio.auth.AppUserPrincipal;
import com.resumestudio.common.ApiPaths;
import com.resumestudio.resume.dto.ResumePayload;
import com.resumestudio.resume.dto.ResumeResponse;
import com.resumestudio.resume.service.ResumePayloadValidator;
import com.resumestudio.resume.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.RESUMES_BASE)
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final ResumePayloadValidator payloadValidator;

    @GetMapping(ApiPaths.CURRENT_RESUME)
    public ResponseEntity<ResumeResponse> current(@AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(resumeService.getCurrent(principal.getUserId()));
    }

    @PutMapping(ApiPaths.CURRENT_RESUME)
    public ResponseEntity<ResumeResponse> save(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @Valid @RequestBody ResumePayload payload
    ) {
        payloadValidator.validate(payload);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(resumeService.saveCurrent(principal.getUserId(), payload));
    }
}



