Frequently Asked Questions
======

#### How do I give Players permission to drive?

If you have a permission plugin, you will find all of the [permissions here](permissions.md). If you don't want to use permissions, you can disable `Other.UsePermissions` in the `config.yml`.

#### How do I drive?

By default, you can spawn or purchase a car, then enter the Minecart and it should give you a key (stick). Right click with the key to start the engine, then look in the direction you want to drive; it's that easy!

#### How can I use the Vehiclez models? ####

I have created a resource pack that replaces each colour of GLAZED_TERRACOTTA with a matching Car model.

![Vehiclez Pack Picture](https://i.imgur.com/jODhG6j.png "Vehiclez Pack Picture")

In the `server.properties` set `resource-pack=https\://a5h73y.github.io/Vehiclez/files/resource-packs/VehiclezPack_1.0.zip`.

Now you can create any type of Car you want, then setting the Fill Material to (COLOUR)_GLAZED_TERRACOTTA.

If you want an example of populated CarTypes, take a look at [my config.yml](https://a5h73y.github.io/Vehiclez/files/resources/config.yml) and copy them into your config.yml.

#### How can I speed up / slow down my Car?

Each Car Type can have its own properties _(MaxSpeed, Acceleration, Cost, etc.)_. There is a default car type found in the `config.yml` which you can modify, or just create your own using `/vehiclez createtype`.

Alternatively, you can use [Speed Blocks](https://a5h73y.github.io/Vehiclez/#/tutorials/block-types?id=speed-block). These will allow you to modify the Car's speed based on which Material they are driving upon. 

## Terms / Meanings

#### Config

Vehiclez is incredibly customisable, allowing you to modify the plugin exactly to what your server requires. In your server, Vehiclez will have a folder of many configuration files, shortened to config. `config.yml` & `strings.yml` are the only files we suggest you edit, unless you know what you're doing. Some server implementations don't save the changes upon restarting the server, so we highly suggest you use the **/vehiclez reload** when you've made any config changes, then you'll be safe to restart your server without losing any changes.
