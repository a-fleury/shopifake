package shopifake.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient userServiceWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:4001") // URL du UserService
                .build();
    }

    @Bean
    public WebClient productServiceWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:4002") // URL du ProductService
                .build();
    }
}
