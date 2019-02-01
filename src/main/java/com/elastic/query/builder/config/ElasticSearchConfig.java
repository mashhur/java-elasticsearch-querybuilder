package com.elastic.query.builder.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ElasticSearchConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchConfig.class);

    @Autowired
    private Environment environment;

    @Bean(destroyMethod = "close", name = "highLevelRestClientBean")
    public RestHighLevelClient highLevelClient() {
        return new RestHighLevelClient(restClientBuilder());
    }

    @Bean(destroyMethod = "close")
    public RestClient restClient() {
        return restClientBuilder().build();
    }

    private RestClientBuilder restClientBuilder() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(
                        environment.getProperty("elasticsearch.username"),
                        environment.getProperty("elasticsearch.password")));

        List<HttpHost> hosts = new ArrayList<>();
        hosts.add(new HttpHost(
                environment.getProperty("elasticsearch.hosts.host1.name", String.class),
                environment.getProperty("elasticsearch.hosts.host1.port", Integer.class),
                "http"));

        LOGGER.info("Elasticsearch connection establishing..." + hosts.toArray().toString());

        return RestClient.builder(hosts.toArray(new HttpHost[hosts.size()]))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                        .setConnectTimeout(environment.getProperty("elasticsearch.connectTimeout", Integer.class))
                        .setSocketTimeout(environment.getProperty("elasticsearch.socketTimeout", Integer.class)))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
    }

}
