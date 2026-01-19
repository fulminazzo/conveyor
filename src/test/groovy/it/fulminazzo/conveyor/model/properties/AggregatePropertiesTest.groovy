package it.fulminazzo.conveyor.model.properties

import spock.lang.Specification

class AggregatePropertiesTest extends Specification {

    private final AggregateProperties properties = new AggregateProperties()

    def 'test that AggregateProperties prioritizes delegate'() {
        given:
        this.properties.add('first', 'Hello')

        and:
        this.properties
                .addProperties(new BaseMutableProperties().add('first', 'world'))
                .addProperties(new BaseMutableProperties().add('first', 'friend'))
                .addProperties(new BaseMutableProperties().add('first', 'family'))

        expect:
        this.properties['first'] == 'Hello'
    }

    def 'test that AggregateProperties loops through other properties to find requested property'() {
        given:
        this.properties.add('first', 'Hello')

        and:
        this.properties
                .addProperties(new BaseMutableProperties().addAll([first: 'world', second: 'world']))
                .addProperties(new BaseMutableProperties().addAll([first: '!', second: '!', third: '!']))

        expect:
        this.properties['first'] == 'Hello'
        this.properties['second'] == 'world'
        this.properties['third'] == '!'
        this.properties['fourth'] == null
    }

}
