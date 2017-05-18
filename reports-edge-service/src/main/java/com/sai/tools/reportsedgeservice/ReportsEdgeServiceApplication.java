package com.sai.tools.reportsedgeservice;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@EnableAutoConfiguration
@Configuration
@EnableHystrix
public class ReportsEdgeServiceApplication {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @RestController
    class ReportsController {

        @Autowired
        private MessageService messageService;

        @RequestMapping(value = "/greet", produces = MediaType.APPLICATION_JSON_VALUE)
        public Map<String, String> greet() {
            return messageService.message();
        }
    }


    @Component
    class MessageService {

        @Autowired
        private RestTemplate restTemplate;

        @HystrixCommand(fallbackMethod = "messageFallback")
        public Map<String, String> message() {
            System.out.println("Before -- ");
            Map<String, String> message = restTemplate.exchange("http://reports-service/index", HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, String>>() {
            }).getBody();
            return message;
        }

        public Map<String, String> messageFallback() {
            return Maps.asMap(Sets.newHashSet("message"), (k) -> "I have fallen back to this");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ReportsEdgeServiceApplication.class, args);
    }
}
