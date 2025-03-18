package com.example.tileshop.service;

import java.util.concurrent.TimeUnit;

public interface EmailRateLimiterService {

    boolean setMailLimit(String email, long timeout, TimeUnit unit);

    boolean isMailLimited(String email);
}
