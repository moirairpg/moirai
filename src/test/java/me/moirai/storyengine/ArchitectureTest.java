package me.moirai.storyengine;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameEndingWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "me.moirai.storyengine", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    @ArchTest
    static final ArchRule theDomainDoesNotDependOnSpring =
            noClasses().that().resideInAPackage("..core.domain..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework..");

    @ArchTest
    static final ArchRule theDomainDoesNotDependOnOutboundPorts =
            noClasses().that().resideInAPackage("..core.domain..")
                    .should().dependOnClassesThat().resideInAPackage("..core.port.outbound..");

    @ArchTest
    static final ArchRule theDomainDoesNotDependOnInboundPorts =
            noClasses().that().resideInAPackage("..core.domain..")
                    .should().dependOnClassesThat().resideInAPackage("..core.port.inbound..");

    @ArchTest
    static final ArchRule theDomainDoesNotDependOnInfrastructure =
            noClasses().that().resideInAPackage("..core.domain..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule queryHandlersDoNotDependOnTheDomain =
            noClasses().that().resideInAPackage("..core.application.query..")
                    .should().dependOnClassesThat().resideInAPackage("..core.domain..");

    @ArchTest
    static final ArchRule queryHandlersDoNotUseRepositories =
            noClasses().that().resideInAPackage("..core.application.query..")
                    .should().dependOnClassesThat(
                            resideInAPackage("..core.port.outbound..")
                                    .and(simpleNameEndingWith("Repository")));

    @ArchTest
    static final ArchRule commandHandlersDoNotUseReaders =
            noClasses().that().resideInAPackage("..core.application.command..")
                    .should().dependOnClassesThat(
                            resideInAPackage("..core.port.outbound..")
                                    .and(simpleNameEndingWith("Reader")));

    @ArchTest
    static final ArchRule theCoreDoesNotDependOnSpringMessaging =
            noClasses().that().resideInAPackage("..core..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework.messaging..");

    @ArchTest
    static final ArchRule inboundAdaptersDoNotDependOnTheDomain =
            noClasses().that().resideInAPackage("..infrastructure.inbound..")
                    .should().dependOnClassesThat().resideInAPackage("..core.domain..");
}
