package com.sj.Petory.config;

import ch.qos.logback.core.net.server.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.Objects;

@Configuration
@EnableElasticsearchRepositories
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.data.elasticsearch.host}")
    private String host;

    @Value("${spring.data.elasticsearch.username:#{null}}")
    private String username;

    @Value("${spring.data.elasticsearch.password:#{null}}")
    private String password;

    @Override
    public ClientConfiguration clientConfiguration() {
        if (Objects.isNull(username) && Objects.isNull(password)) {
            return ClientConfiguration.builder()
                    .connectedTo(host)
                    .build();
        }

        return ClientConfiguration.builder()
                .connectedTo(host)
                .withBasicAuth(username, password)
                .build();
    }
}