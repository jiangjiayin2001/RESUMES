package com.resumestudio.resume;

import com.resumestudio.auth.AppUserPrincipal;
import com.resumestudio.resume.dto.ResumePayload;
import com.resumestudio.resume.dto.ResumeResponse;
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
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @GetMapping("/current")
    public ResponseEntity<ResumeResponse> current(@AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(resumeService.getCurrent(principal.getUserId()));
    }

    @PutMapping("/current")
    public ResponseEntity<ResumeResponse> save(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @Valid @RequestBody ResumePayload payload
    ) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(resumeService.saveCurrent(principal.getUserId(), payload));
    }
}
