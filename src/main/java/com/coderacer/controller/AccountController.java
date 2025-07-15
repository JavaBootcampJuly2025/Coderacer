package com.coderacer.controller;


import com.coderacer.dto.AccountCreateDTO;
import com.coderacer.dto.AccountDTO;
import com.coderacer.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.security.auth.login.AccountNotFoundException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor

public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable UUID id) throws AccountNotFoundException {
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    @GetMapping("/account-username/{username}")
    public ResponseEntity<AccountDTO> getAccountByUsername(@PathVariable String username){
        return ResponseEntity.ok(accountService.getAccountByUsername(username));
    }

    @GetMapping("/account-email/{email}")
    public ResponseEntity<AccountDTO> getAccountByEmail(@PathVariable String email){
        return ResponseEntity.ok(accountService.getAccountByEmail(email));
    }

    @GetMapping("/account-rank/{rank}")
    public ResponseEntity<AccountDTO> getAccountByRank(@PathVariable String rank){
        return ResponseEntity.ok(accountService.getAccountByRank(rank));
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts(){
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody @Valid AccountCreateDTO accountCreateDTO){
        AccountDTO created = accountService.createAccount(accountCreateDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<AccountDTO> updateAccount(
//            @PathVariable UUID id,
//            @RequestBody @Valid AccountUpdateDTO accountUpdateDTO){
//        return ResponseEntity.ok(accountService.updateAcount(id,accountUpdateDTO));
//    }
    /// NOT SURE IF WE NEED UPDATE

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable UUID id){
        accountService.deleteAccount(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> restoreAccount(@PathVariable UUID id){
        return ResponseEntity.ok(accountService.restoreAccount(id));
    }
}
