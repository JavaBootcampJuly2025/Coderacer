

package com.coderacer.scheduler;

import com.coderacer.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateAdminAccountTask {

    private final AccountService accountService;

    @Value("${superuser.username}")
    private String superUsername;

    @Value("${superuser.email}")
    private String superEmail;

    @Value("${superuser.password}")
    private String superPassword;

    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartup() {
        accountService.createSuperUser(superUsername, superEmail, superPassword);
    }
}

