package com.reviewer.client.profiler;

import com.reviewer.client.profiler.model.ActionType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange("/api")
public interface ProfilerHttpClient {

    @PutExchange("/clients/{address}/add-experience/{actionType}")
    void updateExperience(@PathVariable String address, @PathVariable ActionType actionType);
}
