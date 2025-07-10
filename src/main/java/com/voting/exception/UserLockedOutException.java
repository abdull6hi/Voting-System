package com.voting.exception;

import java.time.LocalDateTime;

public class UserLockedOutException extends AuthException {
    private final LocalDateTime unlockTime;

    public UserLockedOutException(LocalDateTime unlockTime) {
        super("Account locked until " + unlockTime);
        this.unlockTime = unlockTime;
    }

    public LocalDateTime getUnlockTime() {
        return unlockTime;
    }
}