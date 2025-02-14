package org.terrakube.registry.configuration.authentication.dex;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

@Builder
@Getter
@Setter
public class RegistryAuthenticationManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {

    private static final String jwtPat ="Terrakube";
    private static final String jwtInternal ="TerrakubeInternal";
    private String internalSecret;
    private String issuerUri;
    private String patSecret;

    @Override
    public AuthenticationManager resolve(HttpServletRequest request) {
        ProviderManager providerManager = null;
        String tokenIssuer = getJwtIssuer(request);
        switch (tokenIssuer) {
            case jwtInternal:
                providerManager = new ProviderManager(new JwtAuthenticationProvider(getJwtEncoder(jwtInternal)));
                break;
            case jwtPat:
                providerManager = new ProviderManager(new JwtAuthenticationProvider(getJwtEncoder(jwtPat)));
                break;
            default:
                providerManager = new ProviderManager(new JwtAuthenticationProvider(JwtDecoders.fromIssuerLocation(this.issuerUri)));
                break;
        }
        return providerManager;
    }

    private String getJwtIssuer(HttpServletRequest request) {
        String token = request.getHeader("authorization").replace("Bearer ", "");
        String jwtToken = token.substring(0, token.lastIndexOf('.') + 1);
        return Jwts.parserBuilder().build().parseClaimsJwt(jwtToken).getBody().getIssuer();
    }

    private JwtDecoder getJwtEncoder(String issuerType) {
        String tokenSecret = (issuerType.equals(jwtPat) ? patSecret : internalSecret);
        SecretKey jwtTokenKey = new SecretKeySpec(Decoders.BASE64URL.decode(tokenSecret), "HMACSHA256");
        return NimbusJwtDecoder.withSecretKey(jwtTokenKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

}
