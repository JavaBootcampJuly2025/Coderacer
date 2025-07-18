package com.coderacer.scheduler;


import com.coderacer.service.EmailVerificationTokenService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailVerificationCleanupTask {
    private final EmailVerificationTokenService emailVerificationTokenService;

    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartup() {
        emailVerificationTokenService.cleanupExpiredTokens();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void runHourly() {
        emailVerificationTokenService.cleanupExpiredTokens();
    }
}