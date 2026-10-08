package com.serenitydojo.cashback_rewards.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@DisplayName("Hexagonal architecture")
class HexagonalArchitectureTest {

	private static final String BASE_PACKAGE = "com.serenitydojo.cashback_rewards";

	private static JavaClasses productionClasses;



	@BeforeAll
	static void importProductionClasses() {
		productionClasses = new ClassFileImporter()
				.withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
				.importPackages(BASE_PACKAGE);
	}

	@Nested
	@DisplayName("Domain isolation")
	class DomainIsolation {

		@Test
		@DisplayName("domain must not depend on Spring")
		void domainMustNotDependOnSpring() {
			noClasses().that().resideInAPackage("..domain..")
					.should().dependOnClassesThat().resideInAPackage("org.springframework..")
					.because("the domain should be independent of any spring framework dependencies")
					.allowEmptyShould(true)
					.check(productionClasses);
		}

		@Test
		@DisplayName("domain must not depend on Jakarta Persistence")
		void domainMustNotDependOnJakartaPersistence() {
			noClasses().that().resideInAPackage("..domain..")
					.should().dependOnClassesThat().resideInAPackage("jakarta.persistence..")
					.because("the domain should be independent of any persistence framework dependencies")
					.allowEmptyShould(true)
					.check(productionClasses);
		}
	}

	@Nested
	@DisplayName("Layer dependencies")
	class LayerDependencies {


		@Test
		@DisplayName("dependencies must flow inward: adapter -> application -> domain")
		void dependenciesMustFlowInward() {
			layeredArchitecture()
					.consideringOnlyDependenciesInLayers()
					.withOptionalLayers(true)
					.layer("Adapter").definedBy(BASE_PACKAGE + ".adapter..")
					.layer("Application").definedBy(BASE_PACKAGE + ".application..")
					.layer("Domain").definedBy(BASE_PACKAGE + ".domain..")
					.whereLayer("Adapter").mayNotBeAccessedByAnyLayer()
					.whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter")
					.whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapter")
					.check(productionClasses);
		}
	}
}
