package io.dmendezg.jwtspringwebflux.security

import io.dmendezg.jwtspringwebflux.security.jwt.JwtAuthenticationProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
class WebSecurity {

    @Value("\${key}")
    private val key: String? = null

    @Bean
    fun springWebFilterChain(
        http: ServerHttpSecurity, authenticationManager: ReactiveAuthenticationManager
    ): SecurityWebFilterChain {

        http.addFilterAt(CorsFilter(), SecurityWebFiltersOrder.FIRST)
        http.addFilterAt(JWTWebFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
        http.securityContextRepository(BearerServerSecurityContextRepository())

        http.authorizeExchange()
            .pathMatchers("/actuator/**").permitAll()
            .pathMatchers("/oauth/token").permitAll()
            .anyExchange().authenticated()

        http.httpBasic().disable()
            .formLogin().disable()
            .csrf().disable()
            .logout().disable()

        return http.build()
    }

    @Bean
    fun reactiveAuthenticationManager(): ReactiveAuthenticationManager {
        return JwtAuthenticationProvider(key)
    }


}

class JWTWebFilter(
    private val authenticationManager: ReactiveAuthenticationManager
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap { securityContext ->
                this.authenticationManager.authenticate(securityContext.authentication)
                    .map { securityContext.authentication = it }
                    .map { exchange }
            }
            .defaultIfEmpty(exchange)
            .flatMap { chain.filter(it) }
    }
}

class CorsFilter : WebFilter {
    override fun filter(ctx: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        ctx.response.headers.add("Access-Control-Allow-Origin", "*")
        ctx.response.headers.add("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS")
        ctx.response.headers.add("Access-Control-Allow-Headers", HEADERS)
        return if (ctx.request.method == HttpMethod.OPTIONS) {
            ctx.response.headers.add("Access-Control-Max-Age", "1728000")
            ctx.response.statusCode = HttpStatus.NO_CONTENT
            Mono.empty()
        } else {
            ctx.response.headers.add("Access-Control-Expose-Headers", HEADERS)
            chain.filter(ctx)
        }
    }

    companion object {
        const val HEADERS = "DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With," +
                "If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range,Authorization"
    }

}