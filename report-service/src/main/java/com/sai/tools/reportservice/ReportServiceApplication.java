package com.sai.tools.reportservice;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
public class ReportServiceApplication {

    @RefreshScope
    @RestController
    public class IndexResource {

        @Value("${greeting.message}")
        private String greetingMessage;

        @Autowired
        private DiscoveryClient discoveryClient;

        @RequestMapping(value = "/index", produces = MediaType.APPLICATION_JSON_VALUE)
        public Map<String, String> index() {
            return Maps.asMap(Sets.newHashSet("message"), (k) -> greetingMessage);
        }

        @RequestMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
        public List<String> services() {
            return discoveryClient.getServices();
        }

        @RequestMapping(value = "/instances/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
        public List<ServiceInstance> instances(@PathVariable("name") final String name) {
            return discoveryClient.getInstances(name);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ReportServiceApplication.class, args);
    }
}
