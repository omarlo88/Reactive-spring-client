package lo.omar.webController;

import lo.omar.entities.Coffee;
import lo.omar.entities.CoffeeOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/CoffeeClient")
@Slf4j
public class CoffeeController {

    //private WebClient client = WebClient.create("localhost:8080/coffee");
    //private WebClient client = WebClient.create();
    private WebClient client;

    public CoffeeController(WebClient.Builder builder) {
        this.client = builder.baseUrl("http://localhost:8080/api/coffees")
                //.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                //.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    @GetMapping("/coffees")
    public Flux<Coffee> getCoffees(){
        return client.get()
                //.uri("/")
                //.accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Coffee.class)
                //.exchange()
                //.flatMapMany(clientResponse -> clientResponse.bodyToFlux(Coffee.class))
                //.filter(coffee -> coffee.getCoffeeName().equalsIgnoreCase("Jet Black Mongo"))
                .doOnNext(System.out::println)
                //.onErrorResume(e -> Mono.just(new Coffee()))
                ;
    }

    @GetMapping("/coffees/{id}")
    public Mono<Coffee> getCoffee(@PathVariable String id) {
        return client.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Coffee.class);
                //.exchange()
                //.flatMap(clientResponse -> clientResponse.bodyToMono(Coffee.class));
    }

    @GetMapping(value = "/coffee/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CoffeeOrder> getCoffeesStream(@PathVariable String id){
        // http://localhost:8080/api/coffees/coffee/8f2d4360-1ee4-44cf-a63c-1b3afe6c2e40
         return client.get()
                .uri("/coffee/{id}", id)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(CoffeeOrder.class)
                .doOnNext(System.out::println)
                 //.exchange()
                 //.flatMapMany(clientResponse -> clientResponse.bodyToFlux(CoffeeOrder.class))
                ;
    }

    @DeleteMapping("/coffees/{id}")
    public Mono<Void> deleteCoffe(@PathVariable String id) {
        return client.delete()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
                //.exchange()
                //.flatMap(clientResponse -> clientResponse.bodyToMono(Void.class));
    }

    @PostMapping("/coffees")
    public Mono<Coffee> addCoffee(@RequestBody Mono<Coffee> coffee) {
        return client.post()
                .uri("/")
                .contentType(MediaType.APPLICATION_JSON)
                //.bodyValue(coffeeMono) à partir de spring 5.2
                .body(coffee, Coffee.class)
                //.body(BodyInserters.fromPublisher(coffee, Coffee.class))
                .retrieve()
                .bodyToMono(Coffee.class);
                //.exchange()
                //.flatMap(clientResponse -> clientResponse.bodyToMono(Coffee.class));
    }

    @PutMapping("coffees/{id}")
    public Mono<Coffee> updateCoffee(@RequestBody Mono<Coffee> coffeeMono, @PathVariable String id){
        return client.put()
                .uri("/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                //.bodyValue(coffeeMono) à partir de spring 5.2
                .body(coffeeMono, Coffee.class)
                //.body(BodyInserters.fromPublisher(coffee, Coffee.class))
                .retrieve()
                .bodyToMono(Coffee.class);
    }

    private ExchangeFilterFunction logRequest(){
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse(){
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response status code {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}
