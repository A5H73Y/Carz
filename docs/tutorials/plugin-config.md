Plugin Configuration
======

## config.yml

This is the default configuration file that will allow you to customise the Vehiclez plugin to match your server requirements.

To make changes, edit to the value you want, save the file, then enter `/vehiclez reload` into game or `/vehiclezc reload` into the console. Restarting the server can apply changes also, but it's very inconsistent. It is recommended to use the plugin's reload command where possible.

Some properties require the server to restart to apply the changes, these include adding 3rd party plugin support, etc.

View the annotated configuration file here: [https://pastebin.com/pPNWtwMC](https://pastebin.com/pPNWtwMC)

_This is correct as of Vehiclez v7.0_

## strings.yml

You are able to modify the contents of this file, then enter `/vehiclez reload` for the changes to immediately apply.

If you are having problems, it may be because the yml is considered invalid if it requires `'` either side of the string, when using certain characters such as %.

## blocks.yml

This file contains the various Block Types defined, you can edit these however it is recommended to do it using the in-game commands to avoid any issues.

Addition and removal of Materials should be done through `/vehiclez add (type)` and `/vehiclez remove (type)` accordingly.
