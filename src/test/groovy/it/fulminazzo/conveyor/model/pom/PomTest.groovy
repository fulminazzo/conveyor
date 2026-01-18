package it.fulminazzo.conveyor.model.pom

import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.dependency.Exclusions
import it.fulminazzo.conveyor.model.dependency.RawDependency
import it.fulminazzo.conveyor.model.pom.metadata.Contributor
import it.fulminazzo.conveyor.model.pom.metadata.Developer
import it.fulminazzo.conveyor.model.pom.metadata.License
import it.fulminazzo.conveyor.model.pom.metadata.MailingList
import it.fulminazzo.conveyor.model.pom.metadata.Notifier
import it.fulminazzo.conveyor.model.pom.metadata.Organization
import it.fulminazzo.conveyor.model.pom.metadata.PomMetadata
import spock.lang.Specification

class PomTest extends Specification {

    def 'test that getProperty of #key returns #expected'() {
        given:
        def pom = Pom.builder()
                .parent(new Artifact('it.fulminazzo', 'conveyor-parent', '1.0'))
                .project(new Artifact('it.fulminazzo', 'conveyor', '1.0'))
                .packaging('war')
                .metadata(PomMetadata.builder()
                        .modelVersion('4.0.0')
                        .name('conveyor')
                        .description('A maven library...')
                        .url('fulminazzo.it')
                        .inceptionYear('2024')
                        .organization(Organization.builder()
                                .name('fulminazzo')
                                .url('fulminazzo.it')
                                .build())
                        .licenses([
                                License.builder()
                                        .name('license1')
                                        .url('license1.com')
                                        .distribution('distribution1')
                                        .comments('comments1')
                                        .build(),
                                License.builder()
                                        .name('license2')
                                        .url('license2.com')
                                        .distribution('distribution2')
                                        .comments('comments2')
                                        .build()
                        ].toSet())
                        .developers(new LinkedHashSet<>([
                                Developer.builder()
                                        .id('fulminazzo')
                                        .name('Alex')
                                        .email('alex@fulminazzo.it')
                                        .url('alex.fulminazzo.it')
                                        .organization('Fulminazzo')
                                        .organizationUrl('fulminazzo.it')
                                        .roles(['founder'])
                                        .timezone('+1')
                                        .properties(['contribution': '0%'])
                                        .build(),
                                Developer.builder()
                                        .id('camu')
                                        .name('Camilla')
                                        .email('camu@fulminazzo.it')
                                        .url('camu.fulminazzo.it')
                                        .organization('Fulminazzo')
                                        .organizationUrl('fulminazzo.it')
                                        .roles(['co-founder', 'accountant'])
                                        .timezone('+1')
                                        .properties(['contribution': '100%'])
                                        .build()
                        ]))
                        .contributors(new LinkedHashSet<>([
                                Contributor.builder()
                                        .name('Alex')
                                        .email('alex@fulminazzo.it')
                                        .url('alex.fulminazzo.it')
                                        .organization('Fulminazzo')
                                        .organizationUrl('fulminazzo.it')
                                        .roles(['founder'])
                                        .timezone('+1')
                                        .properties(['contribution': '0%'])
                                        .build(),
                                Contributor.builder()
                                        .name('Camilla')
                                        .email('camu@fulminazzo.it')
                                        .url('camu.fulminazzo.it')
                                        .organization('Fulminazzo')
                                        .organizationUrl('fulminazzo.it')
                                        .roles(['co-founder', 'accountant'])
                                        .timezone('+1')
                                        .properties(['contribution': '100%'])
                                        .build()
                        ]))
                        .mailingLists([
                                MailingList.builder()
                                        .name('first')
                                        .subscribe('first-subscribe@fulminazzo.it')
                                        .unsubscribe('first-unsubscribe@fulminazzo.it')
                                        .post('first-post@fulminazzo.it')
                                        .archive('first1.fulminazzo.it')
                                        .otherArchives(['first2.fulminazzo.it'])
                                        .build(),
                                MailingList.builder()
                                        .name('second')
                                        .subscribe('second-subscribe@fulminazzo.it')
                                        .unsubscribe('second-unsubscribe@fulminazzo.it')
                                        .post('second-post@fulminazzo.it')
                                        .archive('second1.fulminazzo.it')
                                        .otherArchives(['second2.fulminazzo.it', 'second3.fulminazzo.it'])
                                        .build()
                        ])
                        .prerequisites(new PomMetadata.Prerequisites('2.1.0'))
                        .modules(['first', 'second', 'third'])
                        .issueManagement(new PomMetadata.IssueManagement('fulminazzo', 'fulminazzo.it'))
                        .ciManagement(PomMetadata.CiManagement.builder()
                                .system('fulminazzo')
                                .url('fulminazzo.it')
                                .notifiers([
                                        Notifier.builder()
                                                .type('email')
                                                .sendOnError('true')
                                                .sendOnFailure('true')
                                                .sendOnSuccess('true')
                                                .sendOnWarning('true')
                                                .address('support@fulminazzo.it')
                                                .configuration(['active': 'true'])
                                                .build(),
                                        Notifier.builder()
                                                .type('blog')
                                                .sendOnError('false')
                                                .sendOnFailure('false')
                                                .sendOnSuccess('false')
                                                .sendOnWarning('false')
                                                .address('blog-support@fulminazzo.it')
                                                .configuration(['active': 'false'])
                                                .build(),
                                ])
                                .build())
                        .build())
                .properties(['java-version': '17'])
                .dependencyManagement([
                        RawDependency.builder()
                                .groupId('it.fulminazzo')
                                .artifactId('conveyor')
                                .version('1.0')
                                .type('war')
                                .classifier('sources')
                                .scope('IMPORT')
                                .exclusions(new Exclusions().add('it.fulminazzo.conveyor', 'common'))
                                .optional('true')
                                .build()
                ].toSet())
                .build()

        when:
        def value = pom.getProperty(key)

        then:
        value == expected

        where:
        key                                                             || expected
        'modelVersion'                                                  || '4.0.0'
        'parent.groupId'                                                || 'it.fulminazzo'
        'parent.artifactId'                                             || 'conveyor-parent'
        'parent.version'                                                || '1.0'
        // 'parent.relativePath' || NOT SUPPORTED
        'groupId'                                                       || 'it.fulminazzo'
        'artifactId'                                                    || 'conveyor'
        'version'                                                       || '1.0'
        'packaging'                                                     || 'war'
        'name'                                                          || 'conveyor'
        'description'                                                   || 'A maven library...'
        'url'                                                           || 'fulminazzo.it'
        'inceptionYear'                                                 || '2024'
        'organization.name'                                             || 'fulminazzo'
        'organization.url'                                              || 'fulminazzo.it'
        'licenses[0].name'                                              || 'license1'
        'licenses[0].url'                                               || 'license1.com'
        'licenses[0].distribution'                                      || 'distribution1'
        'licenses[0].comments'                                          || 'comments1'
        'licenses[1].name'                                              || 'license2'
        'licenses[1].url'                                               || 'license2.com'
        'licenses[1].distribution'                                      || 'distribution2'
        'licenses[1].comments'                                          || 'comments2'
        'developers[0].id'                                              || 'fulminazzo'
        'developers[0].name'                                            || 'Alex'
        'developers[0].email'                                           || 'alex@fulminazzo.it'
        'developers[0].url'                                             || 'alex.fulminazzo.it'
        'developers[0].organization'                                    || 'Fulminazzo'
        'developers[0].organizationUrl'                                 || 'fulminazzo.it'
        'developers[0].roles[0]'                                        || 'founder'
        'developers[0].timezone'                                        || '+1'
        'developers[0].properties'                                      || '{contribution=0%}'
        'developers[1].id'                                              || 'camu'
        'developers[1].name'                                            || 'Camilla'
        'developers[1].email'                                           || 'camu@fulminazzo.it'
        'developers[1].url'                                             || 'camu.fulminazzo.it'
        'developers[1].organization'                                    || 'Fulminazzo'
        'developers[1].organizationUrl'                                 || 'fulminazzo.it'
        'developers[1].roles[0]'                                        || 'co-founder'
        'developers[1].roles[1]'                                        || 'accountant'
        'developers[1].timezone'                                        || '+1'
        'developers[1].properties'                                      || '{contribution=100%}'
        'contributors[0].name'                                          || 'Alex'
        'contributors[0].email'                                         || 'alex@fulminazzo.it'
        'contributors[0].url'                                           || 'alex.fulminazzo.it'
        'contributors[0].organization'                                  || 'Fulminazzo'
        'contributors[0].organizationUrl'                               || 'fulminazzo.it'
        'contributors[0].roles[0]'                                      || 'founder'
        'contributors[0].timezone'                                      || '+1'
        'contributors[0].properties'                                    || '{contribution=0%}'
        'contributors[1].name'                                          || 'Camilla'
        'contributors[1].email'                                         || 'camu@fulminazzo.it'
        'contributors[1].url'                                           || 'camu.fulminazzo.it'
        'contributors[1].organization'                                  || 'Fulminazzo'
        'contributors[1].organizationUrl'                               || 'fulminazzo.it'
        'contributors[1].roles[0]'                                      || 'co-founder'
        'contributors[1].roles[1]'                                      || 'accountant'
        'contributors[1].timezone'                                      || '+1'
        'contributors[1].properties'                                    || '{contribution=100%}'
        'mailingLists[0].name'                                          || 'first'
        'mailingLists[0].subscribe'                                     || 'first-subscribe@fulminazzo.it'
        'mailingLists[0].unsubscribe'                                   || 'first-unsubscribe@fulminazzo.it'
        'mailingLists[0].post'                                          || 'first-post@fulminazzo.it'
        'mailingLists[0].archive'                                       || 'first1.fulminazzo.it'
        'mailingLists[0].otherArchives[0]'                              || 'first2.fulminazzo.it'
        'mailingLists[1].name'                                          || 'second'
        'mailingLists[1].subscribe'                                     || 'second-subscribe@fulminazzo.it'
        'mailingLists[1].unsubscribe'                                   || 'second-unsubscribe@fulminazzo.it'
        'mailingLists[1].post'                                          || 'second-post@fulminazzo.it'
        'mailingLists[1].archive'                                       || 'second1.fulminazzo.it'
        'mailingLists[1].otherArchives[0]'                              || 'second2.fulminazzo.it'
        'mailingLists[1].otherArchives[1]'                              || 'second3.fulminazzo.it'
        'prerequisites.maven'                                           || '2.1.0'
        'modules[0]'                                                    || 'first'
        'modules[1]'                                                    || 'second'
        'modules[2]'                                                    || 'third'
        'issueManagement.system'                                        || 'fulminazzo'
        'issueManagement.url'                                           || 'fulminazzo.it'
        'ciManagement.system'                                           || 'fulminazzo'
        'ciManagement.url'                                              || 'fulminazzo.it'
        'ciManagement.notifiers[0].type'                                || 'email'
        'ciManagement.notifiers[0].sendOnError'                         || 'true'
        'ciManagement.notifiers[0].sendOnFailure'                       || 'true'
        'ciManagement.notifiers[0].sendOnSuccess'                       || 'true'
        'ciManagement.notifiers[0].sendOnWarning'                       || 'true'
        'ciManagement.notifiers[0].address'                             || 'support@fulminazzo.it'
        'ciManagement.notifiers[0].configuration'                       || '{active=true}'
        'ciManagement.notifiers[1].type'                                || 'blog'
        'ciManagement.notifiers[1].sendOnError'                         || 'false'
        'ciManagement.notifiers[1].sendOnFailure'                       || 'false'
        'ciManagement.notifiers[1].sendOnSuccess'                       || 'false'
        'ciManagement.notifiers[1].sendOnWarning'                       || 'false'
        'ciManagement.notifiers[1].address'                             || 'blog-support@fulminazzo.it'
        'ciManagement.notifiers[1].configuration'                       || '{active=false}'
        'properties'                                                    || '{java-version=17}'
        'dependencyManagement.dependencies[0].groupId'                  || 'it.fulminazzo'
        'dependencyManagement.dependencies[0].artifactId'               || 'conveyor'
        'dependencyManagement.dependencies[0].version'                  || '1.0'
        'dependencyManagement.dependencies[0].type'                     || 'war'
        'dependencyManagement.dependencies[0].classifier'               || 'sources'
        'dependencyManagement.dependencies[0].scope'                    || 'IMPORT'
        //'dependencyManagement.dependencies[0].systemPath' || NOT SUPPORTED
        'dependencyManagement.dependencies[0].exclusions[0].groupId'    || 'it.fulminazzo.conveyor'
        'dependencyManagement.dependencies[0].exclusions[0].artifactId' || 'common'
        'dependencyManagement.dependencies[0].optional'                 || 'true'
    }

}
