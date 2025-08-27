package com.reviewer.client.profiler.config;

import com.reviewer.client.profiler.ProfilerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Configuration
public class ProfilerWebClientConfig {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    @Value("${jwt.token}")
    private String eternalToken;

    @Value("${url.profiler}")
    private String profilerUrl;

    @Bean
    public ProfilerHttpClient ProfilerHttpClient() {
        WebClient webClient = createWebClient();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder().exchangeAdapter(adapter).build();

        return factory.createClient(ProfilerHttpClient.class);
    }

    private WebClient createWebClient() {

        return WebClient
                .builder()
                .baseUrl(profilerUrl)
                .filter((request, next) -> {
                    ClientRequest filtered = ClientRequest.from(request)
                            .header(AUTHORIZATION_HEADER, BEARER + eternalToken)
                            .build();

                    return next.exchange(filtered);
                })
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

}
