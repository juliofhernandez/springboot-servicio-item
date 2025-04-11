package com.microservices.item;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Value("${config.baseurl.endpoint.products}")
    private String baseurl;

    @Bean
    @LoadBalanced
    public RestTemplate registerRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder, ReactorLoadBalancerExchangeFilterFunction loadbalancerExchangeFilterFunction) {
        return builder
                .filter(loadbalancerExchangeFilterFunction)
                .baseUrl(baseurl)
                .build();
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> customizer() {
        return factory -> factory.configureDefault(idCircuitBreaker -> new Resilience4JConfigBuilder(idCircuitBreaker)
                .circuitBreakerConfig(
                        CircuitBreakerConfig.custom()
                                .slidingWindowSize(6)
                                .failureRateThreshold(50)
                                .waitDurationInOpenState(Duration.ofSeconds(10L))
                                .permittedNumberOfCallsInHalfOpenState(3)
                                .slowCallRateThreshold(50)
                                .slowCallDurationThreshold(Duration.ofSeconds(1L))
                                .build())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(3L)).build())
                .build());
    }
}
