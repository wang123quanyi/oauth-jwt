package com.oauth.jwt.common.config;

import cn.hutool.core.util.StrUtil;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
public class ElasticsearchConfiguration {
    @Resource
    private Environment env;

    @Bean(destroyMethod = "close", name = "restClient")
    public RestHighLevelClient initRestClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost(getHost(), getPort()))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                        .setConnectTimeout(getConnTimeout())
                        .setSocketTimeout(getSocketTimeout())
                        .setConnectionRequestTimeout(getConnRequestTimeout()));
        return new RestHighLevelClient(builder);
    }

    public String getHost() {
        String property = "127.0.0.1";//env.getProperty("elasticsearch.host");
        return property;
    }

    public Integer getPort() {
        String port = "9200";//env.getProperty("elasticsearch.port");
        if (StrUtil.isNotBlank(port)) {
            return Integer.valueOf(port);
        }
        return null;
    }

    public Integer getConnTimeout() {
        String connTimeout = "3000";//env.getProperty("elasticsearch.connTimeout");
        if (StrUtil.isNotBlank(connTimeout)) {
            return Integer.valueOf(connTimeout);
        }
        return null;
    }

    public Integer getSocketTimeout() {
        String socketTimeout = "5000";  //env.getProperty("elasticsearch.socketTimeout");
        if (StrUtil.isNotBlank(socketTimeout)) {
            return Integer.valueOf(socketTimeout);
        }
        return null;
    }

    public Integer getConnRequestTimeout() {
        String connectionRequestTimeout = "500"; //env.getProperty("elasticsearch.connectionRequestTimeout");
        if (StrUtil.isNotBlank(connectionRequestTimeout)) {
            return Integer.valueOf(connectionRequestTimeout);
        }
        return null;
    }
}
