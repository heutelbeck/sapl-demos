package io.sapl.demo.webflux.argumentmodification;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class StringController {

    private final StringService service;

    @GetMapping(value = "/string")
    public Mono<String> someString() {
        return service.lowercase(
                "IF ALL TEXT IS LOWERCASE THE SERVICE WAS CALLED. RIGHT TO THIS MESSAGE THERE IS A LOWERCASE 'HELLO MODIFICATION' THEN THE OBLIGATION SUCCESSFULLY MODIFIED THE METHOD ARGUMENTS->");
    }
}
