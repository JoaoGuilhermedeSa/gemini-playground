package com.example.charactercreation.service;

import com.example.charactercreation.dto.JwtResponse;
import com.example.charactercreation.model.Account;
import com.example.charactercreation.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccount_Success() {
        String username = "testuser";
        String password = "password123";
        String encodedPassword = "encodedPassword";
        String token = "test-token";

        when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(1L);
            return account;
        });
        when(jwtService.generateToken(username)).thenReturn(token);

        JwtResponse jwtResponse = accountService.createAccount(username, password);

        assertNotNull(jwtResponse);
        assertEquals(token, jwtResponse.getToken());
        verify(accountRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(password);
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(jwtService, times(1)).generateToken(username);
    }

    @Test
    void createAccount_UsernameAlreadyExists() {
        String username = "testuser";
        String password = "password123";

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(new Account()));

        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(username, password));
        verify(accountRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, never()).encode(anyString());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void login_Success() {
        String username = "testuser";
        String password = "password123";
        String encodedPassword = "encodedPassword";
        String token = "test-token";

        Account account = new Account();
        account.setUsername(username);
        account.setPasswordHash(encodedPassword);

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtService.generateToken(username)).thenReturn(token);

        JwtResponse jwtResponse = accountService.login(username, password);

        assertNotNull(jwtResponse);
        assertEquals(token, jwtResponse.getToken());
        verify(accountRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(jwtService, times(1)).generateToken(username);
    }

    @Test
    void login_InvalidCredentials_UserNotFound() {
        String username = "testuser";
        String password = "password123";

        when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> accountService.login(username, password));
        verify(accountRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_InvalidCredentials_WrongPassword() {
        String username = "testuser";
        String password = "password123";
        String wrongPassword = "wrongpassword";
        String encodedPassword = "encodedPassword";

        Account account = new Account();
        account.setUsername(username);
        account.setPasswordHash(encodedPassword);

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(wrongPassword, encodedPassword)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> accountService.login(username, wrongPassword));
        verify(accountRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(wrongPassword, encodedPassword);
    }
}