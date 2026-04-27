package com.bio.drqi.es.service;

import com.bio.drqi.es.properties.EsSyncProperties;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "bio.es", name = "enabled", havingValue = "true")
public class EsSyncClientConfig {

    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient(EsSyncProperties properties) {
        List<String> hosts = properties.getHosts();
        if (hosts == null || hosts.isEmpty()) {
            throw new IllegalStateException("sync.es.hosts 未配置，无法启用 ES 增量同步");
        }
        HttpHost[] httpHosts = hosts.stream().map(this::parseHost).toArray(HttpHost[]::new);

        String username = properties.getUsername();
        if (username == null || username.trim().isEmpty()) {
            return new RestHighLevelClient(RestClient.builder(httpHosts));
        }

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, properties.getPassword())
        );
        return new RestHighLevelClient(
                RestClient.builder(httpHosts).setHttpClientConfigCallback(
                        clientBuilder -> clientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                )
        );
    }

    private HttpHost parseHost(String raw) {
        URI uri = URI.create(raw);
        int port = uri.getPort() > 0 ? uri.getPort() : ("https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80);
        return new HttpHost(uri.getHost(), port, uri.getScheme());
    }
}
