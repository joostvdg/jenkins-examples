/**
 * Courtesy of Pierre Beitz
 */
import jenkins.model.*

disableChildren(Jenkins.instance.items)

def disableChildren(items) {
    items.each {
        if (it instanceof com.cloudbees.hudson.plugins.folder.AbstractFolder) {
            if (((com.cloudbees.hudson.plugins.folder.AbstractFolder)it).supportsMakeDisabled()) {
                disableItem(it)
            }
            disableChildren(((com.cloudbees.hudson.plugins.folder.AbstractFolder) it).getItems())
        } else {
            disableItem(it)
        }
    }
}

def disableItem(item) {
    try {
        item.disabled=true
        item.save()
        println "Disabled ${item.name}"
    } catch(ignored) {
        println "${item.name} of type ${item.class} does not support deactivation"
    }
}