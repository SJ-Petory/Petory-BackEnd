package com.sj.Petory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.Objects;

@Configuration
@EnableElasticsearchRepositories
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.data.elasticsearch.uris}")
    private String uris;

    @Value("${spring.data.elasticsearch.username:#{null}}")
    private String username;

    @Value("${spring.data.elasticsearch.password:#{null}}")
    private String password;

//    @Bean
//    public RestClient restClient() {
//
//        CredentialsProvider provider = new BasicCredentialsProvider();
//        provider.setCredentials(
//                new AuthScope(AuthScope.ANY),
//                new UsernamePasswordCredentials(username, password)
//        );
//
//        // RestClient를 만들고, 기본 인증을 설정
//        RestClientBuilder builder = RestClient.builder(new HttpHost(uris, 9200, "https"))
//                .setHttpClientConfigCallback(httpClientBuilder ->
//                        httpClientBuilder.setDefaultCredentialsProvider(provider));
//
//        return builder.build();
//    }
//
//    @Bean
//    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
//        // ElasticsearchClient를 생성하고 RestClient를 사용하도록 설정
//        RestClientTransport transport = new RestClientTransport(
//                restClient,
//                new JacksonJsonpMapper()
//        );
//        return new co.elastic.clients.elasticsearch.ElasticsearchClient(transport);
//    }


    @Override
    public ClientConfiguration clientConfiguration() {
        if (Objects.isNull(username) && Objects.isNull(password)) {
            return ClientConfiguration.builder()
                    .connectedTo(uris)
                    .build();
        }
        return ClientConfiguration.builder()
                .connectedTo(uris)
                .usingSsl()
                .withBasicAuth(username, password)
                .build();
    }
}
