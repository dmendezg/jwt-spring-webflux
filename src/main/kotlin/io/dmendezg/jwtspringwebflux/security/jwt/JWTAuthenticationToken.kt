package io.dmendezg.jwtspringwebflux.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class JWTAuthenticationToken(private val jws: Jws<Claims>) : Authentication {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        TODO("not implemented")
    }

    override fun getName(): String {
        return this.jws.body.subject
    }

    override fun getCredentials(): Any {
        return this.jws.body.getValue("scopes")
    }

    override fun getPrincipal(): Any {
        return this.jws.body.subject
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun getDetails(): Any {
        TODO("not implemented")
    }

}