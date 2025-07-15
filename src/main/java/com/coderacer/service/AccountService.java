package com.coderacer.service;

import com.coderacer.dto.AccountCreateDTO;
import com.coderacer.dto.AccountDTO;
import com.coderacer.enums.AccountStatus;
import com.coderacer.enums.RankMedal;
import com.coderacer.exception.AccountNotFoundException;
import com.coderacer.model.Account;
import com.coderacer.repository.AccountRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional()
    public AccountDTO getAccount(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return AccountDTO.fromEntity(account);
    }

//    @Transactional()
//    public AccountDTO getAccountByUsername(String username) {
//        return accountRepository.findByUsername(username)
//                .map(AccountDTO::fromEntity);
//                .orElseThrow(() -> new AccountNotFoundException(username));
//    }
    /// THIS BROKEN IDK WHY


//    public AccountDTO getAccountByEmail(String email) {
//        return accountRepository.findByEmail(email)
//                .map(AccountDTO::fromEntity);
//                .orElseThrow(() -> new AccountNotFoundException(email));
//    }
    /// THIS BROKEN TOO

//    public AccountDTO getAccountByRank(String rank) {
//        return accountRepository.findByRank(rank).stream()
//                .map(AccountDTO::fromEntity)
//                .toList();
//    }
    /// I DONT KNOW WHAT IM DOING DO I

    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountDTO::fromEntity)
                .toList();
    }

//    public AccountDTO createAccount(@Valid AccountCreateDTO accountCreateDTO) {
//        Account account = new Account();
//        account.setId(UUID.randomUUID());
//        account.setAccountUsername(dto.accountUsername());
//        account.setType(dto.type());
//        account.setRank(RankMedal.JUNIOR);
//
//        Account saved = accountRepository.save(account);
//        return AccountDTO.fromEntity(saved);
//    }
    /// YEAH IM JUST CLUELESS XD

//    public AccountDTO updateAcount(UUID id, AccountUpdateDTO accountUpdateDTO) {
//    }
//
//    public void deleteAccount(UUID id) {
//        if (!accountRepository.existsById(id)) {
//            throw new AccountNotFoundException(id);
//        }
//
//        Account account = accountRepository.findById(id)
//                .orElseThrow(() -> new AccountNotFoundException(id));
//
//        account.setStatus(AccountStatus.CLOSED);
//        accountRepository.save(account);
//    }
    /// WHAT DO YOU MEAN CREATE METHOD IN ACCOUNT

//    public AccountDTO restoreAccount(UUID id) {
//        Account account = accountRepository.findById(id)
//                .orElseThrow(() -> new AccountNotFoundException(id));
//
//        account.setStatus(AccountStatus.ACTIVE);
//        Account saved = accountRepository.save(account);
//        return AccountDTO.fromEntity(saved);
//    }
    /// WHERE DO I EVEN GET THAT setStatus FROM?
}
