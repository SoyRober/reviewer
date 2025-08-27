package com.client.profiler;

import com.client.profiler.model.ActionType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/api")
public interface ProfilerHttpClient {

    @PostExchange("/client/{address}/add-experience/{actionType}")
    void updateExperience(@PathVariable String address, @PathVariable ActionType actionType);
}
