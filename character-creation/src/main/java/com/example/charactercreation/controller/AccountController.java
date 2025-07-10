package com.example.charactercreation.controller;

import com.example.charactercreation.dto.AccountRequest;
import com.example.charactercreation.dto.JwtResponse;
import com.example.charactercreation.model.Account;
import com.example.charactercreation.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public JwtResponse createAccount(@RequestBody AccountRequest accountRequest) {
        return accountService.createAccount(accountRequest.getUsername(), accountRequest.getPassword());
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody AccountRequest accountRequest) {
        return accountService.login(accountRequest.getUsername(), accountRequest.getPassword());
    }
}
