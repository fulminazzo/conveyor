package it.fulminazzo.conveyor.model.pom

import it.fulminazzo.conveyor.model.Properties
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.dependency.RawDependency
import it.fulminazzo.conveyor.model.dependency.Scope
import it.fulminazzo.conveyor.model.profile.Profile
import it.fulminazzo.conveyor.model.profile.activation.Activation
import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext
import spock.lang.Specification

class EffectivePomBuilderTest extends Specification {

    def 'test that populateDependencyManagement adds parent, active profiles and imported dependencies dependency management'() {
        given:
        def parent = newArtifact('parent')
        def parentPom = newPom(parent,
                [
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-dependency1')
                                .version('1.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-dependency2')
                                .version('1.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('dependency1')
                                .version('1.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('dependency2')
                                .version('1.0')
                                .build()
                ],
                [
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-profile-dependency1')
                                .version('1.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-profile-dependency2')
                                .version('1.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('profile-dependency1')
                                .version('1.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('profile-dependency2')
                                .version('1.0')
                                .build()
                ]
        )

        and:
        def dependency = newArtifact('dependency2', '2.0')
        def dependencyPom = newPom(dependency,
                [
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('dependency2')
                                .version('3.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('dependency3')
                                .version('3.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('dependency4')
                                .version('3.0')
                                .build()
                ],
                [
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('profile-dependency2')
                                .version('3.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('profile-dependency3')
                                .version('3.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('profile-dependency4')
                                .version('3.0')
                                .build()
                ]
        )

        and:
        def profileDependency = newArtifact('profile-dependency2', '2.0')
        def profileDependencyPom = newPom(profileDependency,
                [
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('dependency4')
                                .version('4.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('dependency5')
                                .version('4.0')
                                .build()
                ],
                [
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('profile-dependency4')
                                .version('4.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('profile-dependency5')
                                .version('4.0')
                                .build()
                ]
        )

        and:
        def artifact = newArtifact('conveyor')
        def pom = newPom(artifact,
                [
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('dependency1')
                                .version('2.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('dependency2')
                                .version('2.0')
                                .scope(Scope.IMPORT.value())
                                .build()
                ],
                [
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('profile-dependency1')
                                .version('2.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('profile-dependency2')
                                .version('2.0')
                                .scope(Scope.IMPORT.value())
                                .build()
                ]
        )

        and:
        PomResolver resolver = (a) -> {
            def art = Artifact.builder()
                    .groupId(a.groupId)
                    .artifactId(a.artifactId)
                    .classifier(a.classifier)
                    .version(a.version)
                    .build()
            if (art == artifact) return pom
            else if (art == parent) return parentPom
            else if (art == dependency) return dependencyPom
            else if (art == profileDependency) return profileDependencyPom
            else throw new IllegalArgumentException("Could not get pom of artifact: $a")
        }

        and:
        def parentBuilder = new EffectivePomBuilder(parentPom, resolver, Mock(ActivationContext))
        parentBuilder.activeProfiles.addAll(parentPom.profiles)

        when:
        parentBuilder.populateDependencyManagement()

        then:
        parentBuilder.dependencyManagement == [
                'it.fulminazzo:parent-dependency1:jar:'        : '1.0',
                'it.fulminazzo:parent-dependency2:jar:'        : '1.0',
                'it.fulminazzo:parent-profile-dependency1:jar:': '1.0',
                'it.fulminazzo:parent-profile-dependency2:jar:': '1.0',
                'it.fulminazzo:dependency1:jar:'               : '1.0',
                'it.fulminazzo:dependency2:jar:'               : '1.0',
                'it.fulminazzo:profile-dependency1:jar:'       : '1.0',
                'it.fulminazzo:profile-dependency2:jar:'       : '1.0'
        ]

        when:
        def builder = new EffectivePomBuilder(pom, resolver, Mock(ActivationContext))
        builder.activeProfiles.addAll(pom.profiles)
        builder.parentEffectivePomBuilder = parentBuilder

        and:
        builder.populateDependencyManagement()

        then:
        builder.dependencyManagement.sort() == [
                'it.fulminazzo:parent-dependency1:jar:'        : '1.0',
                'it.fulminazzo:parent-dependency2:jar:'        : '1.0',
                'it.fulminazzo:parent-profile-dependency1:jar:': '1.0',
                'it.fulminazzo:parent-profile-dependency2:jar:': '1.0',
                'it.fulminazzo:dependency1:jar:'               : '2.0',
                'it.fulminazzo:dependency2:jar:'               : '2.0',
                'it.fulminazzo:dependency3:jar:'               : '3.0',
                'it.fulminazzo:dependency4:jar:'               : '3.0',
                'it.fulminazzo:dependency5:jar:'               : '4.0',
                'it.fulminazzo:profile-dependency1:jar:'       : '2.0',
                'it.fulminazzo:profile-dependency2:jar:'       : '2.0',
                'it.fulminazzo:profile-dependency3:jar:'       : '3.0',
                'it.fulminazzo:profile-dependency4:jar:'       : '3.0',
                'it.fulminazzo:profile-dependency5:jar:'       : '4.0'
        ].sort()
    }

    def 'test that populateProperties adds parent and active profiles properties'() {
        given:
        def parent = newArtifact('parent')
        def parentPom = newPom(
                parent,
                ['parent': '1', 'parent-profile': '1', 'pom': '1', 'profile': '1'],
                ['parent-profile': '2', 'pom': '2', 'profile': '2']
        )

        and:
        def parentBuilder = new EffectivePomBuilder(parentPom, (p) -> { }, Mock(ActivationContext))
        parentBuilder.activeProfiles.addAll(parentPom.profiles)

        when:
        parentBuilder.populateProperties()

        then:
        getProperties(parentBuilder) == ['parent': '1', 'parent-profile': '2', 'pom': '2', 'profile': '2']

        when:
        def artifact = newArtifact('conveyor')
        def pom = newPom(
                artifact,
                ['pom': '3', 'profile': '3'],
                ['profile': '4']
        )
        pom.parent >> parent

        and:
        def builder = new EffectivePomBuilder(pom, (p) -> { }, Mock(ActivationContext))
        builder.activeProfiles.addAll(pom.profiles)
        builder.parentEffectivePomBuilder = parentBuilder

        and:
        builder.populateProperties()

        then:
        getProperties(builder) == ['parent': '1', 'parent-profile': '2', 'pom': '3', 'profile': '4']
    }

    def 'test that resolveParentEffectivePom stores correct parent builder'() {
        given:
        def parent = newArtifact('parent')

        and:
        def parentPom = Mock(Pom)
        parentPom.profiles >> []
        parentPom.dependencyManagement >> []
        parentPom.properties >> [:]

        and:
        PomResolver resolver = p -> parentPom

        and:
        def pom = Mock(Pom)
        pom.parent >> parent
        pom.profiles >> []
        pom.dependencyManagement >> []

        and:
        def builder = new EffectivePomBuilder(pom, resolver, Mock(ActivationContext))

        when:
        builder.resolveParentEffectivePom()

        then:
        builder.parentEffectivePomBuilder != null

        and:
        builder.parentEffectivePomBuilder.startingPom == parentPom
    }

    def 'test that resolveParentEffectivePom does not throw on missing parent'() {
        given:
        def pom = Mock(Pom)
        pom.profiles >> []

        and:
        def builder = new EffectivePomBuilder(pom, (p) -> null, Mock(ActivationContext))

        when:
        builder.resolveParentEffectivePom()

        then:
        builder.parentEffectivePomBuilder == null
    }

    def 'test that populateActiveProfiles adds only active profiles'() {
        given:
        def activeProfiles = createMockProfilesList(0..9, 'active', true)
        def passiveProfiles = createMockProfilesList(0..9, 'inactive', false)

        and:
        def pom = newPom(activeProfiles, passiveProfiles)

        and:
        def builder = new EffectivePomBuilder(pom, Mock(PomResolver), Mock(ActivationContext))

        when:
        builder.populateActiveProfiles()

        then:
        builder.activeProfiles.sort() == activeProfiles
    }

    def 'test that populateActiveProfiles overrides active profiles'() {
        given:
        def pom = newPom(
                createMockProfilesList(0..9, 'active', true),
                createMockProfilesList(0..9, 'inactive', false)
        )

        and:
        def activeProfiles = createMockProfilesList(10..19, 'active', true)
        def passiveProfiles = createMockProfilesList(10..19, 'inactive', false)

        and:
        def builder = new EffectivePomBuilder(pom, Mock(PomResolver), Mock(ActivationContext))

        when:
        builder.populateActiveProfiles()

        and:
        def newPom = newPom(activeProfiles, passiveProfiles)
        def field = EffectivePomBuilder.getDeclaredField('startingPom')
        field.setAccessible(true)
        field.set(builder, newPom)

        and:
        builder.populateActiveProfiles()

        then:
        builder.activeProfiles.sort() == activeProfiles
    }

    private Pom newPom(final Artifact artifact,
                       final Collection<RawDependency> dependencyManagement,
                       final Collection<RawDependency> profileDependencyManagement) {
        def profile = Mock(Profile)
        profile.id >> "$artifact.artifactId-profile"
        profile.properties >> [:]
        profile.dependencyManagement >> profileDependencyManagement
        profile.activation >> {
            def activation = Mock(Activation)
            activation.isEnabled(_) >> true
            return activation
        }

        def pom = Mock(Pom)
        pom.project >> artifact
        pom.properties >> [:]
        pom.dependencyManagement >> dependencyManagement
        pom.profiles >> [profile]

        return pom
    }

    private Pom newPom(final Artifact artifact,
                       final Map<String, String> properties,
                       final Map<String, String> profileProperties) {
        def profile = Mock(Profile)
        profile.id >> "$artifact.artifactId-profile"
        profile.properties >> profileProperties
        profile.dependencyManagement >> []

        def pom = Mock(Pom)
        pom.project >> artifact
        pom.properties >> properties
        pom.dependencyManagement >> []
        pom.profiles >> [profile]

        return pom
    }

    private Pom newPom(final List<Profile> activeProfiles, final List<Profile> inactiveProfiles) {
        def pom = Mock(Pom)
        pom.profiles >> [*activeProfiles, *inactiveProfiles]
        return pom
    }

    private List<Profile> createMockProfilesList(final IntRange range, final String name, final boolean enabled) {
        return range.collect {
            def profile = Mock(Profile)
            profile.id >> "$name-$it"
            profile.activation >> {
                def activation = Mock(Activation)
                activation.isEnabled(_) >> enabled
                return activation
            }
            return profile
        }.sort()
    }

    private static Artifact newArtifact(final String id) {
        return newArtifact(id, '1.0')
    }

    private static Artifact newArtifact(final String id, final String version) {
        return Artifact.builder()
                .groupId('it.fulminazzo')
                .artifactId(id)
                .version(version)
                .build()
    }

    private static Properties getProperties(final Object object) {
        def field = object.class.getDeclaredField('properties')
        field.accessible = true
        return field.get(object)
    }

}
