package it.fulminazzo.conveyor.model.profile.activation

import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext
import spock.lang.Specification

class AndActivationTest extends Specification {

    def 'test that isEnabled returns #expected for activations #first, #second'() {
        given:
        def activation = new AndActivation()

        and:
        def firstActivation = Mock(Activation)
        firstActivation.isEnabled(_) >> first
        activation.addActivation('first', firstActivation)

        and:
        def secondActivation = Mock(Activation)
        secondActivation.isEnabled(_) >> second
        activation.addActivation('second', secondActivation)

        when:
        def actual = activation.isEnabled(Mock(ActivationContext))

        then:
        actual == expected

        where:
        first | second || expected
        false | false  || false
        true  | false  || false
        false | true   || false
        true  | true   || true
    }

    def 'test that empty AndActivation returns false'() {
        given:
        def activation = new AndActivation()

        expect:
        !activation.isEnabled(Mock(ActivationContext))
    }

    def 'test that addActivation overrides existing activation with same id'() {
        given:
        def activation = new AndActivation()

        and:
        def act1 = Mock(Activation)
        def act2 = Mock(Activation)
        def act3 = Mock(Activation)

        when:
        activation.addActivation('first', act1)
        activation.addActivation('second', act2)
        activation.addActivation('first', act3)

        and:
        def activations = activation.activations

        then:
        activations.size() == 2
        activations.get('first') == act3
        activations.get('second') == act2
    }

}
