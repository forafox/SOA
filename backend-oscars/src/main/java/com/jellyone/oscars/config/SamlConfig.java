package com.jellyone.oscars.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;

import java.util.List;

@Configuration
public class SamlConfig {

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
        RelyingPartyRegistration registration = RelyingPartyRegistration
                .withRegistrationId("backend-oscars-saml")
                .entityId("backend-oscars-saml")
                .assertionConsumerServiceLocation("http://localhost:8080/saml/sso")
                .assertionConsumerServiceBinding(Saml2MessageBinding.POST)
                .singleLogoutServiceLocation("http://localhost:8080/saml/logout")
                .singleLogoutServiceBinding(Saml2MessageBinding.POST)
                .assertingPartyDetails(party -> party
                        .entityId("http://localhost:8082/realms/soa-realm")
                        .singleSignOnServiceLocation("http://localhost:8082/realms/soa-realm/protocol/saml")
                        .singleSignOnServiceBinding(Saml2MessageBinding.POST)
                        .wantAuthnRequestsSigned(false)
                )
                .build();

        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }
}
