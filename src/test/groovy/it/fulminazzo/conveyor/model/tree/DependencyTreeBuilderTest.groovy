package it.fulminazzo.conveyor.model.tree

import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.dependency.Dependency
import it.fulminazzo.conveyor.model.dependency.RawDependency
import it.fulminazzo.conveyor.model.dependency.Scope
import it.fulminazzo.conveyor.model.pom.Pom
import it.fulminazzo.conveyor.model.pom.PomResolver
import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext
import spock.lang.Specification

class DependencyTreeBuilderTest extends Specification {
    private PomResolver resolver
    private DependencyTreeBuilder builder

    void setup() {
        this.resolver = Mock(PomResolver)

        this.builder = new DependencyTreeBuilder(Artifact.builder()
                .groupId('it.fulminazzo')
                .artifactId('main')
                .version('1.0')
                .build(), this.resolver, Mock(ActivationContext))
    }

    def 'test that populateTree correctly adds new dependencies'() {
        given:
        def pom = createFullDependenciesPom('it.fulminazzo', 'main')
        this.resolver.resolve(_) >> pom

        and:
        def dependencyNode = new DependencyNode(
                Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('main')
                        .version('1.0')
                        .build(),
                2
        )

        and:
        def dependencies = this.builder.dependenciesToCheck

        when:
        this.builder.populateTree(dependencyNode)

        then:
        dependencies.size() == pom.dependencies.size()

        and:
        for (Scope scope : Scope.values()) {
            def node = dependencies.find { it.dependency().scope == scope }
            println "Checking scope: $scope"
            assert node != null
            assert node.dependency().artifactId == "dependency-${scope.value()}"
            assert node.depth() == 3
        }

        and:
        this.builder.dependencyTree.get(dependencyNode.dependency().coordinates).depth() == 2
    }

    def 'test that populateTree does not add new dependencies if dependency already present in dependency tree'() {
        given:
        def pom = createFullDependenciesPom('it.fulminazzo', 'main')
        this.resolver.resolve(_) >> pom

        and:
        def dependencyNode = new DependencyNode(
                Dependency.builder()
                        .groupId('it.fulminazzo')
                        .artifactId('main')
                        .version('1.0')
                        .build(),
                2
        )

        and:
        this.builder.dependencyTree.put(
                dependencyNode.dependency().coordinates,
                new DependencyNode(dependencyNode.dependency(), 1)
        )

        and:
        def dependencies = this.builder.dependenciesToCheck

        when:
        this.builder.populateTree(dependencyNode)

        then:
        dependencies.size() == 0

        and:
        this.builder.dependencyTree.get(dependencyNode.dependency().coordinates).depth() == 1
    }

    def 'test that addPomDependenciesToCheckList adds all dependencies'() {
        given:
        def pom = createFullDependenciesPom('it.fulminazzo', 'main')

        and:
        def dependencies = this.builder.dependenciesToCheck

        when:
        this.builder.addPomDependenciesToCheckList(pom, 1)

        then:
        dependencies.size() == pom.dependencies.size()

        and:
        for (Scope scope : Scope.values()) {
            def node = dependencies.find { it.dependency().scope == scope }
            println "Checking scope: $scope"
            assert node != null
            assert node.dependency().artifactId == "dependency-${scope.value()}"
            assert node.depth() == 1
        }
    }

    def 'test that addPomDependenciesToCheckList adds all dependencies of scope #scope'() {
        given:
        def pom = createFullDependenciesPom('it.fulminazzo', 'main')

        and:
        def dependencies = this.builder.dependenciesToCheck

        when:
        this.builder.setRequiredScopes(scope).addPomDependenciesToCheckList(pom, 1)

        then:
        dependencies.size() == 1

        and:
        def node = dependencies.find { it.dependency().scope == scope }
        node != null
        node.dependency().artifactId == "dependency-${scope.value()}"
        node.depth() == 1

        where:
        scope << Scope.values()
    }

    private static Pom createFullDependenciesPom(final String groupId, final String artifactId) {
        return new Pom(Artifact.builder()
                .groupId(groupId)
                .artifactId(artifactId)
                .version('1.0')
                .build(), 'packaging', null, [], [:], [], [], Scope.values().collect {
            RawDependency.builder()
                    .groupId('it.fulminazzo')
                    .artifactId("dependency-${it.value()}")
                    .version('1.0')
                    .scope(it.value())
                    .build()
        })
    }

}
