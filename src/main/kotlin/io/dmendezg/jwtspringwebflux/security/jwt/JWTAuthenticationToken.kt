package io.dmendezg.jwtspringwebflux.security.jwt

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class JWTAuthenticationToken(private val token: String) : Authentication {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        TODO("not implemented")
    }

    override fun getName(): String {
        return this.token
    }

    override fun getCredentials(): Any {
        return this.token
    }

    override fun getPrincipal(): Any {
        return this.token
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun getDetails(): Any {
        TODO("not implemented")
    }

}