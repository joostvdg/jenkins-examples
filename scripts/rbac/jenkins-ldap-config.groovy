
import hudson.*
import hudson.util.Secret;
import hudson.util.Scrambler;
import hudson.util.FormValidation;
import jenkins.*
import jenkins.model.*
import hudson.security.*
import org.jenkinsci.plugins.*
import jenkins.security.plugins.ldap.LDAPConfiguration;
import jenkins.security.plugins.ldap.LDAPGroupMembershipStrategy;
import jenkins.security.plugins.ldap.FromGroupSearchLDAPGroupMembershipStrategy;
import nectar.plugins.rbac.strategy.*;
import nectar.plugins.rbac.groups.*;
import nectar.plugins.rbac.importers.TypicalSetup

import java.util.*
import java.lang.reflect.*
import java.util.Collection;
import java.util.Collections;

println("=== Configuring the LDAP == Start")
String server = this.args[0]
String rootDN = 'dc=example,dc=com'
boolean inhibitInferRootDN = true
String managerDN = 'uid=idm, ou=Administrators,dc=example,dc=com'
String managerPassword = this.args[1]
Secret managerPasswordSecret = Secret.fromString(managerPassword);

String userSearchBase = 'ou=People'
String userSearch = ''
String groupSearchBase = ''
String groupSearchFilter = ''
LDAPGroupMembershipStrategy groupMembershipStrategy = new FromGroupSearchLDAPGroupMembershipStrategy('') // search filter
String displayNameAttributeName = 'cn'
String mailAddressAttributeName = 'mail'

LDAPConfiguration conf = new LDAPConfiguration(server, rootDN, inhibitInferRootDN, managerDN, managerPasswordSecret)
conf.setUserSearchBase(userSearchBase)
conf.setUserSearch(userSearch)
conf.setGroupSearchBase(groupSearchBase)
conf.setGroupSearchFilter(groupSearchFilter)
conf.setGroupMembershipStrategy(groupMembershipStrategy)
conf.setDisplayNameAttributeName(displayNameAttributeName)
conf.setMailAddressAttributeName(mailAddressAttributeName)
List<LDAPConfiguration> configurations = Collections.singletonList(conf)
boolean disableMailAddressResolver = false
LDAPSecurityRealm.CacheConfiguration cache = new LDAPSecurityRealm.CacheConfiguration(100, 1000)
IdStrategy userIdStrategy = null
IdStrategy groupIdStrategy = null

SecurityRealm ldap_realm = new LDAPSecurityRealm(configurations, disableMailAddressResolver, cache, userIdStrategy, groupIdStrategy)
LDAPConfiguration.LDAPConfigurationDescriptor confDescriptor = Jenkins.getActiveInstance().getDescriptorByType(LDAPConfiguration.LDAPConfigurationDescriptor.class);
def connnectionCheck = confDescriptor.doCheckServer(server, managerDN, managerPasswordSecret);
println "= validation: " + connnectionCheck.kind
println "= connnectionCheck: " + connnectionCheck

Jenkins.instance.setSecurityRealm(ldap_realm)
Jenkins.instance.save()
println("=== Configuring the LDAP == End")

println("=== Configuring the RBAC == Start")
def currentAuthenticationStrategy = Jenkins.instance.getAuthorizationStrategy()
if (currentAuthenticationStrategy instanceof RoleMatrixAuthorizationStrategyImpl) {
    println "= RBAC authorisation already enabled."
    println "= Exiting script..."
    return
} else {
    println "= Enabling role based authorisation strategy..."
}
println "= Initializing RBAC with Typical Setup"
RoleMatrixAuthorizationStrategyImpl rbac = new RoleMatrixAuthorizationStrategyImpl();
Jenkins.instance.setAuthorizationStrategy(rbac)
TypicalSetup typicalSetup = new TypicalSetup()
boolean weCanDoTheTypicalSetup = typicalSetup.isApplicable(rbac)
println "= Can we do the Typical Setup? " + weCanDoTheTypicalSetup
typicalSetup.doImport(rbac)

// add ldap Catmins to Role "administer", via Group "Administrators"
// RoleMatrixAuthorizationConfig config = RoleMatrixAuthorizationPlugin.getConfig();
RoleMatrixAuthorizationConfig config = RoleMatrixAuthorizationPlugin.getConfig();
// List<Group> rootGroups = new ArrayList<Group>();
List<Group> updatedRootGroups = new ArrayList<Group>()
List<Group> rootGroups = config.getGroups()
for (Group group : rootGroups ) {
    println "= Found group: " + group.getName()
    if (group.getName().equals("Administrators")) {
        List<String> members = group.getMembers()
        members.add("barbossa") // so we have atleast 1 admin
        members.add("Catmins-ext")
        group.setMembers(members)
    }
    if (group.getName().equals("Browsers")) {
        List<String> members = group.getMembers()
        members.add("Pirates-ext")
        members.add("Continental-ext")
        group.setMembers(members)
    }
    updatedRootGroups.add(group)
}

Group catmins = new Group('Catmins-ext')
List<String> catminsMembers = new ArrayList<String>()
catminsMembers.add("Catmins")
catmins.setMembers(catminsMembers)

Group pirates = new Group('Pirates-ext')
List<String> piratesMembers = new ArrayList<String>()
piratesMembers.add("Pirates")
pirates.setMembers(piratesMembers)

Group continental = new Group('Continental-ext')
List<String> continentalMembers = new ArrayList<String>()
continentalMembers.add("Continental")
continental.setMembers(continentalMembers)

updatedRootGroups.add(catmins)
updatedRootGroups.add(pirates)
updatedRootGroups.add(continental)
config.setGroups(updatedRootGroups)

Jenkins.instance.save();
println("=== Configuring the RBAC == End")
