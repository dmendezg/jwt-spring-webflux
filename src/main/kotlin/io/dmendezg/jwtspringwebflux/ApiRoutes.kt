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
import java.util.*

@Component
class ApiRoutes {

    @Value("\${key}")
    private val key: String? = null

    @Bean
    fun routes(): RouterFunction<ServerResponse> {
        return router {
            POST("/oauth/token", this@ApiRoutes::token)
            GET("/secure", this@ApiRoutes::secure)
        }
    }

    fun token(serverRequest: ServerRequest): Mono<ServerResponse> {
        return serverRequest.bodyToMono(Login::class.java).flatMap { login ->
            val now = System.currentTimeMillis()
            val exp = now + 7200000
            val compactJws = Jwts.builder()
                .setIssuedAt(Date(now))
                .setExpiration(Date(exp))
                .setSubject(login.username)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact()
            ServerResponse.ok().body(fromObject(Token(compactJws, 7200)))
        }
    }

    fun secure(serverRequest: ServerRequest): Mono<ServerResponse> {
        return serverRequest.principal().flatMap {
            ServerResponse.ok().body(fromObject("Hello ${it.name}!"))
        }
    }

}

data class Login(val username: String)
data class Token(val token: String, val expiresIn: Int)