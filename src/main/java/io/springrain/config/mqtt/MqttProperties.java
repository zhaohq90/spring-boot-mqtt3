package io.springrain.config.mqtt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mqtt")
public class MqttProperties {

    private String username;
    private String password;
    private String[] urls;
    private String producerId;
	private String consumerId;
    private String defaultTopic;
    private String[] consumerTopics;

}
