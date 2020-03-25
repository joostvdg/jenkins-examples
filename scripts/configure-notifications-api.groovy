import com.cloudbees.opscenter.plugins.notification.OperationsCenterRouter

println("=== Configuring Notifications API == Start")
NotificationConfiguration config = ExtensionList.lookupSingleton(NotificationConfiguration.class);

def router = new OperationsCenterRouter()
println("= Setting router to &apos;OperationsCenterRouter&apos; - messages will be send via CJOC")

config.setRouter(router);
config.setEnabled(true);
println("= Setting enabled to true, notifications will now enabled after router starts")

config.onLoaded();  // will start the router if needed.
println("= Starting router")

config.save()
println("= Saving configurations")
println("=== Configuring Notifications API == End")
