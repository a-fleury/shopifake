package shopifake.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class GatewayController {

    private final WebClient userServiceWebClient;
    private final WebClient productServiceWebClient;

    @GetMapping("/users")
    public Mono<String> getUsers() {
        return userServiceWebClient.get()
                .uri("/users")
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("/products")
    public Mono<String> getProducts() {
        return productServiceWebClient.get()
                .uri("/products")
                .retrieve()
                .bodyToMono(String.class);
    }
}
