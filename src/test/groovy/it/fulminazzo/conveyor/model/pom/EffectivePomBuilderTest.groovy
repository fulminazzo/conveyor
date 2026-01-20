package it.fulminazzo.conveyor.model.pom

import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.dependency.Dependency
import it.fulminazzo.conveyor.model.dependency.RawDependency
import it.fulminazzo.conveyor.model.dependency.Scope
import it.fulminazzo.conveyor.model.pom.resolver.RepositoryPomResolver
import it.fulminazzo.conveyor.model.profile.Profile
import it.fulminazzo.conveyor.model.profile.activation.Activation
import it.fulminazzo.conveyor.model.properties.MavenProjectProperties
import it.fulminazzo.conveyor.model.repository.RawRepository
import it.fulminazzo.conveyor.model.repository.Repository
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

class EffectivePomBuilderTest extends Specification {

    def 'test that populateDependencies adds parent and active profiles dependencies'() {
        given:
        def parent = newArtifact('parent')

        and:
        def parentPom = newPomWithDependencies(
                parent,
                [
                        RawDependency.builder()
                                .groupId('${groupId}')
                                .artifactId('${artifactId}')
                                .version('1.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-dependency2')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-dependency3')
                                .version('1.0')
                                .build()
                ],
                [
                        RawDependency.builder()
                                .groupId('${groupId}')
                                .artifactId('${profile-artifactId}')
                                .version('1.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-profile-dependency2')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-profile-dependency3')
                                .version('1.0')
                                .build()
                ]
        )

        and:
        def artifact = newArtifact('artifact')

        and:
        def pom = newPomWithDependencies(
                artifact,
                [
                        RawDependency.builder()
                                .groupId('${groupId}')
                                .artifactId('${artifactId}')
                                .version('2.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('dependency2')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('dependency3')
                                .version('2.0')
                                .build()
                ],
                [
                        RawDependency.builder()
                                .groupId('${groupId}')
                                .artifactId('${profile-artifactId}')
                                .version('2.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('profile-dependency2')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('profile-dependency3')
                                .version('2.0')
                                .build()
                ]
        )

        and:
        def parentBuilder = new EffectivePomBuilder(parentPom, Mock(RepositoryPomResolver), TestUtils.BASE_DIR)
        getProperties(parentBuilder).addAll([
                'groupId'           : 'it.fulminazzo',
                'artifactId'        : 'parent-dependency1',
                'profile-artifactId': 'parent-profile-dependency1'
        ])
        parentBuilder.activeProfiles.add(parentPom.profiles[0])
        parentBuilder.dependencyManagement.putAll([
                'it.fulminazzo:parent-dependency2:jar:'        : '1.0',
                'it.fulminazzo:parent-profile-dependency2:jar:': '1.0'
        ])

        when:
        parentBuilder.populateDependencies()

        then:
        parentBuilder.dependencies.sort() == [
                'it.fulminazzo:parent-dependency1:jar:'        : Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('parent-dependency1')
                        .version('1.0')
                        .build(),
                'it.fulminazzo:parent-dependency2:jar:'        : Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('parent-dependency2')
                        .version('1.0')
                        .build(),
                'it.fulminazzo:parent-dependency3:jar:'        : Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('parent-dependency3')
                        .version('1.0')
                        .build(),
                'it.fulminazzo:parent-profile-dependency1:jar:': Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('parent-profile-dependency1')
                        .version('1.0')
                        .build(),
                'it.fulminazzo:parent-profile-dependency2:jar:': Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('parent-profile-dependency2')
                        .version('1.0')
                        .build(),
                'it.fulminazzo:parent-profile-dependency3:jar:': Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('parent-profile-dependency3')
                        .version('1.0')
                        .build()
        ].sort()

        when:
        def builder = new EffectivePomBuilder(pom, Mock(RepositoryPomResolver), TestUtils.BASE_DIR)
        getProperties(builder).addAll([
                'groupId'           : 'it.fulminazzo',
                'artifactId'        : 'dependency1',
                'profile-artifactId': 'profile-dependency1'
        ])
        builder.activeProfiles.add(pom.profiles[0])
        builder.parentEffectivePomBuilder = parentBuilder
        builder.dependencyManagement.putAll([
                'it.fulminazzo:parent-dependency2:jar:'        : '2.0',
                'it.fulminazzo:parent-profile-dependency2:jar:': '2.0',
                'it.fulminazzo:dependency2:jar:'               : '2.0',
                'it.fulminazzo:profile-dependency2:jar:'       : '2.0'
        ])

        and:
        builder.populateDependencies()

        then:
        builder.dependencies.sort() == [
                'it.fulminazzo:parent-dependency1:jar:'        : Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('parent-dependency1')
                        .version('1.0')
                        .build(),
                'it.fulminazzo:parent-dependency2:jar:'        : Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('parent-dependency2')
                        .version('1.0')
                        .build(),
                'it.fulminazzo:parent-dependency3:jar:'        : Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('parent-dependency3')
                        .version('1.0')
                        .build(),
                'it.fulminazzo:dependency1:jar:'               : Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('dependency1')
                        .version('2.0')
                        .build(),
                'it.fulminazzo:dependency2:jar:'               : Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('dependency2')
                        .version('2.0')
                        .build(),
                'it.fulminazzo:dependency3:jar:'               : Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('dependency3')
                        .version('2.0')
                        .build(),
                'it.fulminazzo:parent-profile-dependency1:jar:': Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('parent-profile-dependency1')
                        .version('1.0')
                        .build(),
                'it.fulminazzo:parent-profile-dependency2:jar:': Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('parent-profile-dependency2')
                        .version('1.0')
                        .build(),
                'it.fulminazzo:parent-profile-dependency3:jar:': Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('parent-profile-dependency3')
                        .version('1.0')
                        .build(),
                'it.fulminazzo:profile-dependency1:jar:'       : Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('profile-dependency1')
                        .version('2.0')
                        .build(),
                'it.fulminazzo:profile-dependency2:jar:'       : Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('profile-dependency2')
                        .version('2.0')
                        .build(),
                'it.fulminazzo:profile-dependency3:jar:'       : Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('profile-dependency3')
                        .version('2.0')
                        .build()
        ].sort()
    }

    def 'test that populateDependencyManagement adds parent, active profiles and imported dependencies dependency management'() {
        given:
        def parentDependency = newArtifact('parent-dependency2', '1.0')
        def parentDependencyPom = newPom(parentDependency,
                [
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-dependency1')
                                .version('5.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-dependency2')
                                .version('5.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-dependency3')
                                .version('5.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-dependency4')
                                .version('5.0')
                                .build()
                ],
                [
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-profile-dependency1')
                                .version('5.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-profile-dependency2')
                                .version('5.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-profile-dependency3')
                                .version('5.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-profile-dependency4')
                                .version('5.0')
                                .build()
                ]
        )

        and:
        def parentProfileDependency = newArtifact('parent-profile-dependency2', '1.0')
        def parentProfileDependencyPom = newPom(parentProfileDependency,
                [
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-dependency1')
                                .version('6.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-dependency4')
                                .version('6.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-dependency5')
                                .version('6.0')
                                .build()
                ],
                [
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-profile-dependency1')
                                .version('6.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-profile-dependency1')
                                .version('6.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-profile-dependency4')
                                .version('6.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('parent-profile-dependency5')
                                .version('6.0')
                                .build()
                ]
        )

        and:
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
                                .scope(Scope.IMPORT.value())
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
                                .scope(Scope.IMPORT.value())
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
                                .artifactId('parent-dependency1')
                                .version('3.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('dependency1')
                                .version('3.0')
                                .build(),
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
                                .artifactId('parent-profile-dependency1')
                                .version('3.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('profile-dependency1')
                                .version('3.0')
                                .build(),
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
                                .artifactId('parent-dependency1')
                                .version('4.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('dependency1')
                                .version('4.0')
                                .build(),
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
                                .artifactId('parent-profile-dependency1')
                                .version('4.0')
                                .build(),
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('profile-dependency1')
                                .version('4.0')
                                .build(),
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
        def resolver = Mock(RepositoryPomResolver)
        resolver.resolve(_) >> (a) -> {
            def arg = a[0]
            def art = Artifact.builder()
                    .groupId(arg.groupId)
                    .artifactId(arg.artifactId)
                    .classifier(arg.classifier)
                    .version(arg.version)
                    .build()
            if (art == artifact) return pom
            else if (art == parentDependency) return parentDependencyPom
            else if (art == parentProfileDependency) return parentProfileDependencyPom
            else if (art == parent) return parentPom
            else if (art == dependency) return dependencyPom
            else if (art == profileDependency) return profileDependencyPom
            else throw new IllegalArgumentException("Could not get pom of artifact: $arg")
        }

        and:
        def parentBuilder = new EffectivePomBuilder(parentPom, resolver, TestUtils.BASE_DIR)
        parentBuilder.activeProfiles.addAll(parentPom.profiles)

        when:
        parentBuilder.populateDependencyManagement()

        then:
        parentBuilder.dependencyManagement.sort() == [
                'it.fulminazzo:parent-dependency1:jar:'        : '6.0',
                'it.fulminazzo:parent-dependency2:jar:'        : '1.0',
                'it.fulminazzo:parent-dependency3:jar:'        : '5.0',
                'it.fulminazzo:parent-dependency4:jar:'        : '6.0',
                'it.fulminazzo:parent-dependency5:jar:'        : '6.0',
                'it.fulminazzo:parent-profile-dependency1:jar:': '1.0',
                'it.fulminazzo:parent-profile-dependency2:jar:': '1.0',
                'it.fulminazzo:parent-profile-dependency3:jar:': '5.0',
                'it.fulminazzo:parent-profile-dependency4:jar:': '6.0',
                'it.fulminazzo:parent-profile-dependency5:jar:': '6.0',
                'it.fulminazzo:dependency1:jar:'               : '1.0',
                'it.fulminazzo:dependency2:jar:'               : '1.0',
                'it.fulminazzo:profile-dependency1:jar:'       : '1.0',
                'it.fulminazzo:profile-dependency2:jar:'       : '1.0'
        ].sort()

        when:
        def builder = new EffectivePomBuilder(pom, resolver, TestUtils.BASE_DIR)
        builder.activeProfiles.addAll(pom.profiles)
        builder.parentEffectivePomBuilder = parentBuilder

        and:
        builder.populateDependencyManagement()

        then:
        builder.dependencyManagement.sort() == [
                'it.fulminazzo:parent-dependency1:jar:'        : '4.0',
                'it.fulminazzo:parent-dependency2:jar:'        : '1.0',
                'it.fulminazzo:parent-dependency3:jar:'        : '5.0',
                'it.fulminazzo:parent-dependency4:jar:'        : '6.0',
                'it.fulminazzo:parent-dependency5:jar:'        : '6.0',
                'it.fulminazzo:parent-profile-dependency1:jar:': '4.0',
                'it.fulminazzo:parent-profile-dependency2:jar:': '1.0',
                'it.fulminazzo:parent-profile-dependency3:jar:': '5.0',
                'it.fulminazzo:parent-profile-dependency4:jar:': '6.0',
                'it.fulminazzo:parent-profile-dependency5:jar:': '6.0',
                'it.fulminazzo:dependency1:jar:'               : '4.0',
                'it.fulminazzo:dependency2:jar:'               : '2.0',
                'it.fulminazzo:dependency3:jar:'               : '3.0',
                'it.fulminazzo:dependency4:jar:'               : '4.0',
                'it.fulminazzo:dependency5:jar:'               : '4.0',
                'it.fulminazzo:profile-dependency1:jar:'       : '2.0',
                'it.fulminazzo:profile-dependency2:jar:'       : '2.0',
                'it.fulminazzo:profile-dependency3:jar:'       : '3.0',
                'it.fulminazzo:profile-dependency4:jar:'       : '4.0',
                'it.fulminazzo:profile-dependency5:jar:'       : '4.0'
        ].sort()
    }

    def 'test that populateRepositories adds parent and active profiles repositories'() {
        given:
        def repositories = []

        and:
        def pomResolver = Mock(RepositoryPomResolver)
        pomResolver.addRepositories(_) >> { a ->
            repositories.addAll(a[0])
            return pomResolver
        }

        and:
        def parent = newArtifact('parent')
        def parentPom = newPomWithRepositories(
                parent,
                [
                        RawRepository.builder().id('parent-repository1').url('https://url1.com').build(),
                        RawRepository.builder().id('${parent.repository.id}').url('${parent.repository.url}').build()
                ],
                [
                        RawRepository.builder().id('parent-profile-repository1').url('https://url3.com').build(),
                        RawRepository.builder().id('${parent.profile.repository.id}').url('${parent.profile.repository.url}').build()
                ]
        )

        and:
        def artifact = newArtifact('artifact')
        def pom = newPomWithRepositories(
                artifact,
                [
                        RawRepository.builder().id('repository1').url('https://url5.com').build(),
                        RawRepository.builder().id('${repository.id}').url('${repository.url}').build()
                ],
                [
                        RawRepository.builder().id('profile-repository1').url('https://url7.com').build(),
                        RawRepository.builder().id('${profile.repository.id}').url('${profile.repository.url}').build()
                ]
        )

        and:
        def parentBuilder = new EffectivePomBuilder(parentPom, pomResolver, TestUtils.BASE_DIR)
        getProperties(parentBuilder).addAll([
                'parent.repository.id'         : 'parent-repository2',
                'parent.repository.url'        : 'https://url2.com',
                'parent.profile.repository.id' : 'parent-profile-repository2',
                'parent.profile.repository.url': 'https://url4.com'
        ])
        parentBuilder.activeProfiles.add(parentPom.profiles[0])

        when:
        parentBuilder.populateRepositories()

        then:
        repositories.sort() == [
                Repository.builder().id('parent-repository1').url('https://url1.com').build(),
                Repository.builder().id('parent-repository2').url('https://url2.com').build(),
                Repository.builder().id('parent-profile-repository1').url('https://url3.com').build(),
                Repository.builder().id('parent-profile-repository2').url('https://url4.com').build()
        ].sort()

        when:
        def builder = new EffectivePomBuilder(pom, pomResolver, TestUtils.BASE_DIR)
        getProperties(builder).addAll([
                'repository.id'         : 'repository2',
                'repository.url'        : 'https://url6.com',
                'profile.repository.id' : 'profile-repository2',
                'profile.repository.url': 'https://url8.com'
        ])
        builder.parentEffectivePomBuilder = parentBuilder
        builder.activeProfiles.add(pom.profiles[0])

        and:
        builder.populateRepositories()

        then:
        repositories.sort() == [
                Repository.builder().id('parent-repository1').url('https://url1.com').build(),
                Repository.builder().id('parent-repository2').url('https://url2.com').build(),
                Repository.builder().id('parent-profile-repository1').url('https://url3.com').build(),
                Repository.builder().id('parent-profile-repository2').url('https://url4.com').build(),
                Repository.builder().id('parent-repository1').url('https://url1.com').build(),
                Repository.builder().id('parent-repository2').url('https://url2.com').build(),
                Repository.builder().id('parent-profile-repository1').url('https://url3.com').build(),
                Repository.builder().id('parent-profile-repository2').url('https://url4.com').build(),
                Repository.builder().id('repository1').url('https://url5.com').build(),
                Repository.builder().id('repository2').url('https://url6.com').build(),
                Repository.builder().id('profile-repository1').url('https://url7.com').build(),
                Repository.builder().id('profile-repository2').url('https://url8.com').build()
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
        def parentBuilder = new EffectivePomBuilder(parentPom, Mock(RepositoryPomResolver), TestUtils.BASE_DIR)
        parentBuilder.activeProfiles.addAll(parentPom.profiles)

        when:
        parentBuilder.populateProperties()

        then:
        getProperties(parentBuilder).toMap() == ['parent': '1', 'parent-profile': '2', 'pom': '2', 'profile': '2']

        when:
        def artifact = newArtifact('conveyor')
        def pom = newPom(
                artifact,
                ['pom': '3', 'profile': '3'],
                ['profile': '4']
        )
        pom.parent >> parent

        and:
        def builder = new EffectivePomBuilder(pom, Mock(RepositoryPomResolver), TestUtils.BASE_DIR)
        builder.activeProfiles.addAll(pom.profiles)
        builder.parentEffectivePomBuilder = parentBuilder

        and:
        builder.populateProperties()

        then:
        getProperties(builder).toMap() == ['parent': '1', 'parent-profile': '2', 'pom': '3', 'profile': '4']
    }

    def 'test that populateProperties allows resolving of recursive OS Maven Plugin properties'() {
        given:
        def artifact = newArtifact('conveyor')
        def pom = newPom(artifact, [
                'tcnative.classifier': '${os.detected.classifier}'
        ], [:])

        and:
        def builder = new EffectivePomBuilder(pom, Mock(RepositoryPomResolver), TestUtils.BASE_DIR)

        when:
        builder.populateProperties()

        and:
        def properties = getProperties(builder)

        and:
        def property = properties.apply('${tcnative.classifier}')

        then:
        property != null

        and:
        property != '${os.detected.classifier}'
    }

    def 'test that resolveParentEffectivePom stores correct parent builder'() {
        given:
        def parent = newArtifact('parent')

        and:
        def parentPom = Mock(Pom)
        parentPom.properties >> [:]
        parentPom.repositories >> []
        parentPom.dependencyManagement >> []
        parentPom.dependencies >> []
        parentPom.profiles >> []
        parentPom.project >> new Artifact('it.fulminazzo', 'parent', '1.0')

        and:
        def resolver = Mock(RepositoryPomResolver)
        resolver.resolve(_) >> parentPom

        and:
        def pom = Mock(Pom)
        pom.parent >> parent
        pom.profiles >> []
        pom.dependencyManagement >> []
        pom.dependencies >> []
        pom.project >> new Artifact('it.fulminazzo', 'conveyor', '1.0')

        and:
        def builder = new EffectivePomBuilder(pom, resolver, TestUtils.BASE_DIR)

        when:
        builder.resolveParentEffectivePom()

        then:
        builder.parentEffectivePomBuilder != null

        and:
        builder.parentEffectivePomBuilder.startingPom == parentPom
    }

    def 'test that resolveParentEffectivePom does not throw on missing parent'() {
        given:
        def pom = newPom([], [])

        and:
        def builder = new EffectivePomBuilder(pom, Mock(RepositoryPomResolver), TestUtils.BASE_DIR)

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
        def builder = new EffectivePomBuilder(pom, Mock(RepositoryPomResolver), TestUtils.BASE_DIR)

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
        def builder = new EffectivePomBuilder(pom, Mock(RepositoryPomResolver), TestUtils.BASE_DIR)

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

    def 'test that getDependency of #rawDependency returns #expected'() {
        given:
        def builder = new EffectivePomBuilder(newPom([], []), Mock(RepositoryPomResolver), TestUtils.BASE_DIR)
        getProperties(builder).addAll([
                'groupId'           : 'it.fulminazzo',
                'artifactId'        : 'dependency',
                'dependency.version': '0.0.1',
                'version'           : '1.0'
        ])
        builder.dependencyManagement.put('it.fulminazzo:dependency2:jar:', '0.0.1')
        builder.dependencyManagement.put('it.fulminazzo:dependency:jar:', '0.0.1')

        when:
        def actual = builder.getDependency(rawDependency)

        then:
        actual == expected

        where:
        rawDependency                             || expected
        RawDependency.builder()
                .groupId('${groupId}')
                .artifactId('${artifactId}')
                .version('${version}')
                .build()                          || Dependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('dependency')
                .version('1.0')
                .build()
        RawDependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('dependency2')
                .version('${dependency.version}')
                .build()                          || Dependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('dependency2')
                .version('0.0.1')
                .build()
        RawDependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('dependency')
                .build()                          || Dependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('dependency')
                .version('0.0.1')
                .build()
        RawDependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('dependency')
                .version('${version}')
                .build()                          || Dependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('dependency')
                .version('1.0')
                .build()
        RawDependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('${artifactId}')
                .version('1.0')
                .build()                          || Dependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('dependency')
                .version('1.0')
                .build()
        RawDependency.builder()
                .groupId('${groupId}')
                .artifactId('dependency')
                .version('1.0')
                .build()                          || Dependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('dependency')
                .version('1.0')
                .build()
        RawDependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('dependency')
                .version('1.0')
                .build()                          || Dependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('dependency')
                .version('1.0')
                .build()
    }

    private Pom newPom(final Artifact artifact,
                       final Collection<RawDependency> dependencyManagement,
                       final Collection<RawDependency> profileDependencyManagement) {
        def profile = Mock(Profile)
        profile.id >> "$artifact.artifactId-profile"
        profile.properties >> [:]
        profile.repositories >> []
        profile.dependencyManagement >> profileDependencyManagement
        profile.activation >> {
            def activation = Mock(Activation)
            activation.isEnabled(_) >> true
            return activation
        }

        def pom = Mock(Pom)
        pom.project >> artifact
        pom.properties >> [:]
        pom.repositories >> []
        pom.dependencyManagement >> dependencyManagement
        pom.profiles >> [profile]

        return pom
    }

    private Pom newPomWithDependencies(final Artifact artifact,
                                       final Collection<RawDependency> dependencies,
                                       final Collection<RawDependency> profileDependencies) {
        def profile = Mock(Profile)
        profile.id >> "$artifact.artifactId-profile"
        profile.dependencies >> profileDependencies

        def pom = Mock(Pom)
        pom.project >> artifact
        pom.dependencies >> dependencies
        pom.profiles >> [profile]

        return pom
    }

    private Pom newPomWithRepositories(final Artifact artifact,
                                       final Collection<RawRepository> repositories,
                                       final Collection<RawRepository> profileRepositories) {
        def profile = Mock(Profile)
        profile.id >> "$artifact.artifactId-profile"
        profile.repositories >> profileRepositories

        def pom = Mock(Pom)
        pom.project >> artifact
        pom.repositories >> repositories
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
        pom.project >> new Artifact('it.fulminazzo', 'conveyor', '1.0')
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
        return new Artifact("it.fulminazzo", id, version)
    }

    private static MavenProjectProperties getProperties(final Object object) {
        def field = object.class.getDeclaredField('properties')
        field.accessible = true
        return field.get(object)
    }

}
