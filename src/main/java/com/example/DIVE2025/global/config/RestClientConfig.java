package com.example.DIVE2025.global.config;


import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        // 커넥션 풀
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);            // 전체 최대 커넥션
        cm.setDefaultMaxPerRoute(20);   // 라우트별 최대 커넥션

        // ✅ HttpClient 5는 Timeout 객체 필요
        RequestConfig reqConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(5))        // 연결 타임아웃
                .setConnectionRequestTimeout(Timeout.ofSeconds(2)) // 풀에서 가져오기 타임아웃
                .setResponseTimeout(Timeout.ofSeconds(10))      // 응답 대기 타임아웃
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(reqConfig)
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofSeconds(30)) // 유휴 정리
                .build();

        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate rest = new RestTemplate(factory);
        rest.setErrorHandler(new DefaultResponseErrorHandler());
        return rest;
    }
}
