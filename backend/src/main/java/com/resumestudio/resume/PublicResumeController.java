package com.resumestudio.resume;

import com.resumestudio.resume.dto.ResumeResponse;
import com.resumestudio.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicResumeController {

    private final ResumeService resumeService;

    @GetMapping("/resume")
    public ResponseEntity<ResumeResponse> publishedResume() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(resumeService.getPublished());
    }
}
