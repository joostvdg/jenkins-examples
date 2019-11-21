import com.cloudbees.hudson.plugins.folder.*
import com.cloudbees.jenkins.plugins.foldersplus.*
import com.cloudbees.hudson.plugins.folder.properties.FolderProxyGroupContainer
import jenkins.model.Jenkins
import hudson.FilePath
import hudson.model.Job
import hudson.model.User
import hudson.tasks.Mailer
import hudson.triggers.*
import nectar.plugins.rbac.groups.*
 
// ####### COMMON METHODS FOR ADMINS #######
adminUserList = []
 
def findAllGroups(FolderProxyGroupContainer fpgc, folderName) {
  def groupName = ""
  def groupMember = ""
  def memberName = ""
  def User u
  if (fpgc != null) {
    fpgc.getGroups().findAll{it != null}.each {
      groupName = it.name
      if (groupName.contains("admin")|| groupName.contains("ADMIN")|| groupName.contains("Admin")) {
        it.getGroupMembership().each {
          groupMember = it.name
          userString = groupName + "," + groupMember
          if (!adminUserList.contains(userString)) {
            adminUserList.add(userString)
          }
        }
        it.getMembers().each {
          memberName = it
          u = User.get(memberName)
          def umail = u.getProperty(Mailer.UserProperty.class)
          userString = folderName + "," + groupName + "," + memberName + "," + umail.getAddress()
          if (!adminUserList.contains(userString)) {
            adminUserList.add(userString)
          }
        }
      }
    }
  }
}
  
def findAllItems(items, folderName) {
  for(item in items)
  {
    if (item instanceof com.cloudbees.hudson.plugins.folder.Folder) {
      AbstractFolder < ? > folderAbs1 = AbstractFolder.class.cast(item)
      FolderProxyGroupContainer propertyFPG = folderAbs1.getProperties().get(FolderProxyGroupContainer.class);
      findAllGroups(propertyFPG, folderName)
      //Drill into folders
      findAllItems(((com.cloudbees.hudson.plugins.folder.Folder) item).getItems(), folderName)
    }
  }
}
  
def gatherAdmins() {
  Jenkins.instance.getAllItems().each { i ->
    if (i instanceof com.cloudbees.hudson.plugins.folder.Folder) {
      folderName = i.getName()
      folderItem = Jenkins.instance.getAllItems(Folder.class).find { it.name.equals(folderName) }
      try {
        AbstractFolder < ? > folderAbs1 = AbstractFolder.class.cast(folderItem)
        FolderProxyGroupContainer propertyFPG = folderAbs1.getProperties().get(FolderProxyGroupContainer.class)
  
        findAllGroups(propertyFPG, folderName)
        findAllItems(((com.cloudbees.hudson.plugins.folder.Folder) folderItem).getItems(), folderName)
      }
      catch (Exception e) {
        println("Error getting groups for folder: " + folderName + " " + e.message)
      }
    }
  }
}
 
String getAdminEmail(String adminDetails) {
    //example adminDetails are as follows:
    //"rootFolderName,ProjectAdminGroupName,LDAP_ID,EmailAddress"
    //so we can tokenize by ',' and pick the 4th element
    return adminDetails.tokenize(',')[3]
}
  
String getAdminsForRootFolder(String rootFolder) {
  def adminsForThisfolder = []
  def jsonString = '['
  def adminEmail = ''
  adminUserList.each {admin ->
    if (admin.toString().startsWith(rootFolder)) {
      adminEmail = getAdminEmail(admin)
      adminsForThisfolder.add(adminEmail)
    }
  }
  for (int i = 0; i < adminsForThisfolder.size(); i++) {
    jsonString += '\"' + adminsForThisfolder[i] + '\"'
    if (i < adminsForThisfolder.size() - 1) {
      jsonString += ','
    }
  }
  jsonString += ']}'
  return jsonString
}
 
// ##### END COMMON METHODS FOR ADMINS #####
 
// ######## COMMON METHODS FOR JOBS ########
def addJobFolder(String folder, List folderList) {
  rootFolder = getFolderRootFolderName(folder)
  if (!folderList.contains(rootFolder)) {
    folderList.add(rootFolder)
  }
}
 
String getJobRootFolderName(String job) {
  def rootFolder = job.substring(0, job.indexOf('/',0))
  return rootFolder
}
 
String getFolderRootFolderName(String folder) {
  def rootFolder = folder
  if (folder.toString().contains('/')) {
    rootFolder = getJobRootFolderName(folder)
  }
  return rootFolder
}
 
String getJobsForFolderJsonString(String folder, List jobList) {
  def jobsForThisFolder = []
  def jsonString = '['
  String rootFolder = ""
  jobList.each { job ->
    rootFolder = getJobRootFolderName(job)
    if (rootFolder.equalsIgnoreCase(folder)) {
      jobsForThisFolder.add(job)
    }
  }
  for (int i = 0; i < jobsForThisFolder.size(); i++) {
    jsonString += '\"' + jobsForThisFolder[i] + '\"'
    if (i < jobsForThisFolder.size() - 1) {
      jsonString += ','
    }
  }
  jsonString += ']'
  return jsonString
}
 
def createJsonFile(String fileName, List jobList, List folderList) {
  def jobs
  def rootFolder = ""
  def jsonString = "{\"list\":["
  for (int i = 0; i < folderList.size(); i++) {
    folder = folderList[i]
    rootFolder = getFolderRootFolderName(folder)
    jobs = getJobsForFolderJsonString(rootFolder, jobList)
    admins = getAdminsForRootFolder(rootFolder)
    jsonString += "{\"FolderName\":\"${rootFolder}\", \"jobs\":${jobs},\"Admins\":${admins}"
    if (i < folderList.size() - 1)
      jsonString += ","
  }
  jsonString += "]}"
  println "JSON STRING FOR " + fileName + " IS: "
  println jsonString
 
  if (build.workspace.isRemote()) {
    channel = build.workspace.channel;
    fp = new FilePath(channel, build.workspace.toString() + "/" + fileName)
  } else {
    fp = new FilePath(new File(build.workspace.toString() + "/" + fileName))
  }
 
  if (fp != null) {
    fp.write(jsonString, null); //writing to file
  }
}
// ###### END COMMON METHODS FOR JOBS ######
 
def gatherJobs() {
  def staleDays = 90
  staleDate = new Date() - staleDays
 
  def lb     // Last Build
  def lbtime // Last Build Date
  def lastBuildDate = new Date()
  def cronexp    // Cron expression            
 
  staleJobList = []
  staleJobFolderList = []
  triggeredJobList = []
  triggeredJobFolderList = []
  
  println("****************************************************************************")
  println("PROCESSING ALL JOBS ON THIS MASTER WITH TIME TRIGGER AND/OR A STALE DATE SET AS: " + staleDate.format("YYYY-MMM-dd HH:mm:ss"))
  println("****************************************************************************")
  
  jobs = Jenkins.instance.getAllItems()
  jobs.each { job ->
    failure = false
    //Cannot disable workflow jobs until the Pipeline Job Plugin is upgraded to V2.11.  just do Maven and feestyles for now.
    if ((job instanceof org.jenkinsci.plugins.workflow.job.WorkflowJob)
     || (job instanceof hudson.maven.MavenModuleSet)
     || (job instanceof hudson.model.FreeStyleProject)) { 
      lb = job.getLastBuild()
      if (lb != null && lb.causes[0] != null && (job.nextBuildNumber > 1)) {
        lbtime = lb.getTime().format("YYYY-MMM-dd HH:mm:ss")
        lastBuildDate = Date.parse("yyyy-MMM-dd HH:mm:ss", lbtime)
      }
      if (job.nextBuildNumber > 1) {
        if (lastBuildDate.before(staleDate)) {
          //job is stale so add it to the job list
          if (job.fullName.toString().contains('/')){
            //Jobs in root folder are not being processed
            staleJobList.add(job.fullName)
            // to delete the stale jobs, use the job.delete() method
            addJobFolder(job.getParent().getFullName(), staleJobFolderList)
          }
        }
      }
      cronexp = null
      if (job.triggers != null) {
        for(trigger in job.triggers.values()) {
          if(trigger instanceof TimerTrigger) {
            cronexp = trigger.spec
            break
          }
        }
        if (cronexp != null) {
          if (job.fullName.toString().contains('/')) {
            //Jobs in root folder are not being processed
            triggeredJobList.add(job.fullName)
            addJobFolder(job.getParent().getFullName(), triggeredJobFolderList)
          }
        }
      }
    }
  }
  createJsonFile("StaleJobDetails.json", staleJobList, staleJobFolderList)
  createJsonFile("TriggeredJobDetails.json", triggeredJobList, triggeredJobFolderList)
}
 
// ######### MAIN #########
 
gatherAdmins()
gatherJobs()