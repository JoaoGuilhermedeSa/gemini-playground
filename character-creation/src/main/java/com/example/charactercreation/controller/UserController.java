package com.example.charactercreation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.charactercreation.dto.AccountRequest;
import com.example.charactercreation.dto.JwtResponse;
import com.example.charactercreation.service.AccountService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public JwtResponse login(@RequestBody AccountRequest accountRequest) {
        return accountService.login(accountRequest.getUsername(), accountRequest.getPassword());
    }
    
    @PostMapping("/create-account")
    public JwtResponse createAccount(@RequestBody AccountRequest accountRequest) {
        return accountService.createAccount(accountRequest.getUsername(), accountRequest.getPassword());
    }
}
