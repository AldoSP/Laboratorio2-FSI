package com.udea.labdrools.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

    private static final KieServices KIE_SERVICES = KieServices.Factory.get();
    private static final String AIRLINE_RULES_DRL = "rules/airline_rules.drl";

    @Bean
    public KieContainer kieContainer() {
        KieFileSystem kieFileSystem = KIE_SERVICES.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource(AIRLINE_RULES_DRL));

        KieBuilder kieBuilder = KIE_SERVICES.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new IllegalStateException(kieBuilder.getResults().getMessages(Message.Level.ERROR).toString());
        }

        KieModule kieModule = kieBuilder.getKieModule();
        return KIE_SERVICES.newKieContainer(kieModule.getReleaseId());
    }
}
