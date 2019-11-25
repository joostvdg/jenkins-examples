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

RoleMatrixAuthorizationConfig config = RoleMatrixAuthorizationPlugin.getConfig();
List<Group> rootGroups = config.getGroups()
for (Group group : rootGroups ) {
    println "---------------------"
    println "Found group: " + group.getName()
    List<String> members = group.getMembers()
    for (String member : members) {
        println " - Found Member: " + member
    }
    println "---------------------"
}

