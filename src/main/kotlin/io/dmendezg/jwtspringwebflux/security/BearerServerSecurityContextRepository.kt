package io.dmendezg.jwtspringwebflux.security

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class BearerServerSecurityContextRepository : ServerSecurityContextRepository {

    override fun save(exchange: ServerWebExchange, context: SecurityContext?): Mono<Void> {
        return Mono.empty()
    }

    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        val context = SecurityContextHolder.createEmptyContext()
        val token = tokenFromRequest(exchange.request)
        token ?: return Mono.empty()
        val authentication = PreAuthenticatedAuthenticationToken(token, token)
        context.authentication = authentication
        return Mono.just(context)
    }

    private fun tokenFromRequest(request: ServerHttpRequest): String? {
        if (request.headers["Authorization"] == null) return null
        val value = (request.headers["Authorization"] as List<String>)[0]
        if (!value.toLowerCase().startsWith("bearer")) {
            return null
        }
        val parts = value
            .split(" ".toRegex())
            .dropLastWhile({ it.isEmpty() })
            .toTypedArray()
        return if (parts.size < 2) {
            null
        } else parts[1].trim({ it <= ' ' })
    }

}