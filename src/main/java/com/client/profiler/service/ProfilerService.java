package com.client.profiler.service;

import com.client.profiler.ProfilerHttpClient;
import com.client.profiler.model.ActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfilerService {
    private final ProfilerHttpClient profilerHttpClient;

    public void updateExperience(String address, ActionType actionType) {
        profilerHttpClient.updateExperience(address, actionType);
    }
}
