<p align="center"><img src="http://i.imgur.com/xPrxoVX.jpg" alt="Carz Logo"></p>

[![travis-ci](https://travis-ci.org/A5H73Y/Carz.svg?branch=master)](https://travis-ci.org/A5H73Y/Carz/branches)
[![tutorials](https://img.shields.io/badge/tutorials-github-brightgreen.svg)](https://a5h73y.github.io/Carz/)
[![bStats](https://img.shields.io/badge/statistics-bstats-brightgreen.svg)](https://bstats.org/plugin/bukkit/Carz)
[![license: MIT](https://img.shields.io/badge/license-MIT-lightgrey.svg)](https://tldrlegal.com/license/mit-license)
[![repo](https://api.bintray.com/packages/a5h73y/repo/Carz/images/download.svg)](https://bintray.com/a5h73y/repo/Carz/_latestVersion)

Carz has finally been updated from 2014, bringing with it improved code, car ownership, fuel, economy, better permissions, and of course open-source.
First released in July 2012, becoming my most downloaded plugin to date. Carz is now open source, allowing you to contribute ideas and enhancements, or create your own spin on the plugin.<p />
Add a whole new level of role-play to any server that uses roads, perfect for city servers.<p />

[<img src="https://i.imgur.com/jcFOb37.png" alt="Discord Support">](https://discord.gg/h9d2fSd)<p />

## Installation
* Install [Spigot](https://www.spigotmc.org/threads/buildtools-updates-information.42865/) _(v1.8 to 1.14)_
* Download Carz from [dev.bukkit.org/projects/carz/files](https://dev.bukkit.org/projects/carz/files)
* Place the _Carz.jar_ into the _/plugins_ folder of the server.
* Start your server and check the server logs to ensure the plugin started successfully.
* Check the _config.yml_ and configure it to your preference before fully implementing the plugin.

## Supported plugins
| Plugin        | Description  |
| ------------- | ------------- |
| [Vault](https://dev.bukkit.org/projects/vault) | Add economy support to the plugin, reward or penalise the player. <br>[GitHub Project by MilkBowl](https://github.com/MilkBowl/Vault) |
| [BountifulAPI](https://www.spigotmc.org/resources/bountifulapi-1-8-1-9-1-10.1394/) | Add title and actionbar support to the plugin. Works very nicely with the plugin. <br>[GitHub Project by ConnorLinfoot](https://github.com/ConnorLinfoot/BountifulAPI) |

## Maven
```
<repository>
    <id>a5h73y-repo</id>
    <url>https://dl.bintray.com/a5h73y/repo/</url>
</repository>
```

```
<dependency>
    <groupId>me.A5H73Y</groupId>
    <artifactId>Carz</artifactId>
    <version>7.0</version>
    <type>jar</type>
    <scope>provided</scope>
</dependency>
```

## Gradle
```
repositories { 
    maven { 
        url "https://dl.bintray.com/a5h73y/repo"
    } 
}
```

```
compile 'me.A5H73Y:Carz:7.0'
```
