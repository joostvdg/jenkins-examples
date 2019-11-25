////////// FIND LOCAL GROUPS
import nectar.plugins.rbac.groups.*  
import com.cloudbees.hudson.plugins.folder.*
import com.cloudbees.jenkins.plugins.foldersplus.*
import com.cloudbees.hudson.plugins.folder.properties.FolderProxyGroupContainer
import nectar.plugins.rbac.strategy.*;
import hudson.security.*;
import nectar.plugins.rbac.groups.*;
import nectar.plugins.rbac.roles.*;


println "=================="
printAllFolders()
String folderName = 'catmins'
println "== Looking for RBAC groups for folder: " + folderName

folder = Jenkins.instance.getAllItems(Folder.class).find{it.name.equals(folderName)}
if (folder != null) {
    println "== Found Folder : " + folder.name + ", looking for groups"
    List<String> adminMembers = new ArrayList<String>()
    adminMembers.add('Catmins-ext')
    addGroupsToFolder(folder, 'Cat-Administrators', 'administer', adminMembers)
    
    List<String> devMembers = new ArrayList<String>()
    devMembers.add('Pirates-ext')
    addGroupsToFolder(folder, 'Cat-Developers', 'develop', devMembers)
} else {
    println "== Did not find folder"
}
println "=================="


/// FUNCTIONS
boolean groupExists(def location, String groupName) {
    GroupContainer container = GroupContainerLocator.locate(location)
    def groups = container.getGroups()
    for (Group group : groups) {
        if (group.name.equals(groupName)) {
            return true
        }
    }
    return false
}

def addGroupsToFolder(def location, String groupName, String roleName, List<String> members) {
    // Get location for ClientMaster
    // locationCM = Jenkins.instance.getAllItems().find{it.name.equals("ClientMaster")}
    // Get location for a FolderA/FolderB
    // locationFolder = Jenkins.instance.getAllItems().find{it.fullName.equals("FolderA/FolderB")}
    // Get location at Root Level 
    // locationRoot = Jenkins.getInstance()

    if(groupExists(location, groupName)) {
        println "= group ${groupName} already exists"
        return
    } 
    println "= Adding group ${groupName}"
    GroupContainer container = GroupContainerLocator.locate(location)
    Group group = new Group(container, groupName)
    group.doGrantRole(roleName, 0, Boolean.TRUE)
    members.each {
        group.doAddMember(it)
    }
    container.addGroup(group)
}

def printAllFolders() {
    println '= Looking for folders'
    def folders = Jenkins.instance.getAllItems(Folder.class)
    println '= Found ' + folders.size() + ' folders'
    folders.each {
        println '= ' + it.name
    } 
}

