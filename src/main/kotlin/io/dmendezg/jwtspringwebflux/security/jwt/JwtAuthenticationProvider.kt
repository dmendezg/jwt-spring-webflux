package io.dmendezg.jwtspringwebflux.security.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono

class JwtAuthenticationProvider(private val key: String?) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        return try {
            val token = authentication!!.principal as String
            val claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token)
            Mono.just(JWTAuthenticationToken(claims))
        } catch (e: SignatureException) {
            Mono.empty()
        }
    }
}