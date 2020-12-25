Developer Tutorials
======

## Description

Are you a developer? Great! You can make the most of out Carz's functionality to create the perfect experience for your server. I've made the plugin super easy to expand and integrate into, first we will import Carz project to begin working with it.

## Importing Carz into your Project

You will want to add Carz's repository to the list of repositories so that you can bring in the Carz project.

```
<repository>
    <id>a5h73y-repo</id>
    <url>https://dl.bintray.com/a5h73y/repo/</url>
</repository>
```

Add the following dependency to your list of dependencies and let Maven import the project.

```
<dependency>
    <groupId>io.github.a5h73y</groupId>
    <artifactId>Carz</artifactId>
    <version>(INSERT LATEST VERSION)</version>
    <type>jar</type>
    <scope>provided</scope>
</dependency>
```

Maven should now import the Carz project and its dependencies, make sure your project builds correctly and then continue.

You can also add the Carz.jar to your classpath if you wish for the same outcome, but Maven makes this stage considerably easier.

## Setting up your Plugin

You'll need to decide if your plugin depends on my plugin, or is just an optional dependency. In your plugin.yml enter either of the following:

```
depend: [Carz]
softdepend: [Carz]
```

This will allow Carz to fully initialize before you start to use it.

Now you need to check if the Carz plugin has started correctly within the code, this is for you to check and handle. But will look something like: 

```
Plugin Carz = getServer().getPluginManager().getPlugin("Carz");
if (Carz != null && Carz.isEnabled()) {
    System.out.println("Found Carz v" + Carz.getDescription().getVersion())
} else {
    /* oh no, Carz isn't installed */
}
```

If your Plugin successfully links with Carz, your plugin can now interact with Carz and listen to the events it fires.

## Carz Events

There are a list of Events that Carz creates, that you can listen to:

- CarStashEvent
- EngineStartEvent
- EngineStopEvent
- PurchaseCarEvent
- PurchaseFuelEvent

Each of these will give you at least the Player object and the relevant objects for you to use.

To listen for a Carz event you must create a Listener class and register it correctly, then create an EventHandler like any normal event:

```
@EventHandler
public void onCarzEngineStart(EngineStartEvent event) {
    Car startedCar = event.getCar();
    Player player = event.getPlayer();

    player.sendMessage("You started car " + startedCar.getEntityId());
}
```

    Do you feel like Carz and its users could benefit from the changes you've made?
    
    Create a Pull Request and I will take a look at it.


