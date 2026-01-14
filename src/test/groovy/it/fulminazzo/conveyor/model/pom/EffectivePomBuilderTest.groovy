package it.fulminazzo.conveyor.model.pom

import it.fulminazzo.conveyor.model.profile.Profile
import it.fulminazzo.conveyor.model.profile.activation.Activation
import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext
import spock.lang.Specification

class EffectivePomBuilderTest extends Specification {

    def 'test that populateActiveProfiles adds only active profiles'() {
        given:
        def activeProfiles = createMockProfilesList(0..9, 'active', true)
        def passiveProfiles = createMockProfilesList(0..9, 'inactive', false)

        and:
        def pom = newPom(activeProfiles, passiveProfiles)

        and:
        def builder = new EffectivePomBuilder(pom, Mock(PomResolver))

        when:
        builder.populateActiveProfiles(Mock(ActivationContext))

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
        def builder = new EffectivePomBuilder(pom, Mock(PomResolver))

        when:
        builder.populateActiveProfiles(Mock(ActivationContext))

        and:
        def newPom = newPom(activeProfiles, passiveProfiles)
        def field = EffectivePomBuilder.getDeclaredField('startingPom')
        field.setAccessible(true)
        field.set(builder, newPom)

        and:
        builder.populateActiveProfiles(Mock(ActivationContext))

        then:
        builder.activeProfiles.sort() == activeProfiles
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

}
