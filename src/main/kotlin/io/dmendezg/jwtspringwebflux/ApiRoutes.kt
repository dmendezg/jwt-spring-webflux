package io.dmendezg.jwtspringwebflux

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Component
class ApiRoutes {

    @Value("\${key}")
    private val key: String? = null

    @Bean
    fun routes(): RouterFunction<ServerResponse> {
        return router {
            GET("/oauth/token", this@ApiRoutes::token)
            GET("/secure", this@ApiRoutes::secure)
        }
    }

    fun token(serverRequest: ServerRequest): Mono<ServerResponse> {
        val compactJws = Jwts.builder()
            .setSubject("Joe")
            .signWith(SignatureAlgorithm.HS512, key)
            .compact()
        return ServerResponse.ok().body(fromObject(compactJws))
    }

    fun secure(serverRequest: ServerRequest): Mono<ServerResponse> {
        return serverRequest.principal().flatMap {
            ServerResponse.ok().body(fromObject("Hello $it!"))
        }
    }

}