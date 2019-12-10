import com.cloudbees.plugins.credentials.Credentials
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import java.lang.reflect.*;

Set<Credentials> allCredentials = new HashSet<Credentials>();

def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
    com.cloudbees.plugins.credentials.Credentials.class
);

println 'ROOT'
for (c in creds) {
    printCredential(c)
}

// iterates over folders recursively
getCredentialsFromFolder(Jenkins.instance)


//only for Team Masters
Jenkins.instance.getAllItems(com.cloudbees.opscenter.bluesteel.folder.BlueSteelTeamFolder.class).each{ f ->
    creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
        com.cloudbees.plugins.credentials.Credentials.class, f)
    println f.getName()
    for (c in creds) {
        printCredential(c)
    }
    getCredentialsFromFolder(f)
}

def getCredentialsFromFolder(root) {
    root.getAllItems(com.cloudbees.hudson.plugins.folder.Folder.class).each{ f ->
        creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
            com.cloudbees.plugins.credentials.Credentials.class, f)
        println f.getName()
        for (c in creds) {
            printCredential(c)
        }
        getCredentialsFromFolder(f)
    }
}

def printCredential(c) {
    println('- ' + c.id + ', ' + c.description + ', class: ' + c.getClass().getName())
    
    // print out the methods of the class, to figure out how to print the credential's secret value
    // printClassMethods(c.getClass())

    // CAREFUL -> THIS PRINTS THE ACTUAL USERNAME AND PASSWORD!!!
    if (c instanceof UsernamePasswordCredentialsImpl) {
        println('  > values: ' + ((UsernamePasswordCredentialsImpl)c).getUsername() + ":" + ((UsernamePasswordCredentialsImpl)c).getPassword())
    }
}

// prints out methods of a class, to understand what it can do
def printClassMethods(thisClass) {
    Method[] methods = thisClass.getDeclaredMethods()
    println('Methods of: ' + thisClass.getName())
    for (int i = 0; i < methods.length; i++) {
        println('  > ' + methods[i].toString())
    }
}