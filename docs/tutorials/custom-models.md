Custom Car Models
======

Vehiclez allows you to apply a model to the Minecart to allow it to appear differently for every car type.

The way it achieves this is by placing a Material inside the Minecart whilst you drive it. This Material will have a custom model and will take the shape of the Minecart plus any details you'd like to add.

**This is fairly unique to Vehiclez and you aren't likely to find any resources online.**

## Available Models

Here are some models I have created for you to use. I am not a 3D modeller, so they are very basic but will allow you to differentiate between the car types.

### Vehiclez Pack

This Resource pack replaces each colour of GLAZED_TERRACOTTA with a matching Car model. Each Car Colour will need its own matching entry in the config.yml, I have provided [config here](https://a5h73y.github.io/Vehiclez/files/resources/config.yml) which you can copy into your config.yml.

![Vehiclez Pack Picture](https://i.imgur.com/jODhG6j.png "Vehiclez Pack Picture")

Download: [Click Here](https://a5h73y.github.io/Vehiclez/files/resource-packs/VehiclezPack_1.0.zip)

Material: `(COLOUR)_GLAZED_TERRACOTTA`

bbmodel: [Click Here](https://a5h73y.github.io/Vehiclez/files/resources/VehiclezVehicle.bbmodel)

### Hide Minecarts

I've created a Resource Pack which simply removes the Minecart's default texture pack. This means that it will appear invisible (with a shadow), this will allow you to create your own models without the restriction of needing to wrap the default Minecart.

Download: [Click Here](https://a5h73y.github.io/Vehiclez/files/resource-packs/HideMinecart_1.0.zip)

### Blue Car

![Blue Car Picture](https://i.imgur.com/dUpf5D1.png "Blue Car Picture")

Download: [Click Here](https://a5h73y.github.io/Vehiclez/files/resource-packs/BlueCar.zip)

Material: `BLUE_CONCRETE_POWDER`

bbmodel: [Click Here](https://a5h73y.github.io/Vehiclez/files/resources/BlueCar.bbmodel)

## How do I create my own models?

### Required Software

You'll need to install software which allows you to create 3D models and export them into a format of a Minecraft resource pack.

I would suggest [Blockbench](https://blockbench.net/) as it is free and can be configured to create a Minecraft resource pack using a plugin.

### Configure Software

Once installed I would recommend the following plugins using `File -> Plugins -> Available`:

- Plaster
- Resource Pack Exporter
- Shape Generator
- Texture Editor

![Installed Plugins](https://i.imgur.com/Lu1LI7k.png "Installed Plugins")

### Creating the Model

Now you are free to use the Software to create your own models which sit upon a Minecart.  
The Minecart will still be visible but with the shape of the custom model taking form on top, hiding it.

I have created a custom model which should fully consume the default Minecart model and give you some points of reference: [Download here](https://a5h73y.github.io/Vehiclez/files/resources/Minecart.bbmodel)

![Example Model](https://i.imgur.com/BBwWZ0Y.png "Example Model")

### Editing the Model

It is up to you to learn the software and create the Car you desire. I was able to work out the basics after looking on YouTube.

To keep the Minecart hidden, you must cover all parts of it as demonstrated in the Model I have provided.

### Export the Model

Once you're ready to test out your creation, go to `File -> Export -> Export Resource Pack`. Give the Resource Pack a name and description to reference it easily and save it.

![Exporting Model](https://i.imgur.com/5iw9wlm.png "Exporting Model")

The Model will be packaged into a zip file that Minecraft will accept as a Resource Pack. 
Open up Minecraft `Options... -> Resource Packs... -> Open Pack Folder` drop your freshly created zip file in here. Minecraft should update the results to include your new Resource Pack.

![Resource Pack Imported](https://i.imgur.com/0LLZBbG.png "Resource Pack Imported")

Click the Arrow on your Resource Pack, ignore any message about it possibly being incompatible, and click Done.

**By Default, the Exporting Resource Pack will override the `glass` Material.**

**The Material it overrides can be changed by opening the Archive (zip file) and navigating to `\assets\minecraft\models\block\` and renaming the json file to be a valid Material name available here: [https://minecraft.gamepedia.com/Resource_Pack/Folders](https://minecraft.gamepedia.com/Resource_Pack/Folders)**

To test your model, create a new car type `/vehiclez createtype` go through the steps and specify the `Fill Material` as `GLASS`. 

![Created Car Type](https://i.imgur.com/FJHwDiV.png "Created Car Type")

Obtain the car using `/vehiclez spawn (type)` and place the Minecart received, and you should see your new model! 

![Custom Car Model spawned](https://i.imgur.com/BVbMQG0.png "Custom Car Model spawned")

*Some Materials get rotated without me being able to handle it, for example the CONCRETE_POWDER blocks will be rotated 90 degrees; test your model often to make sure it's behaving as you expect.*
