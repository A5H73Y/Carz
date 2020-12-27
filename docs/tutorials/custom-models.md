Custom Car Models
======

Carz allows you to apply a model to the Minecart to allow it to appear differently for every car type.

The way it achieves this is by placing a Material inside the Minecart whilst you drive it. This Material will have a custom model and will take the shape of the Minecart plus any details you'd like to add.

**This is fairly unique to Carz and you aren't likely to find any resources online.**

## How do I create my own models?

### Required Software

You'll need to install software which allows you to create 3D models and export them into a format of a Minecraft resource pack.

I would suggest [Blockbench](https://blockbench.net/) as it is free and can be configured to create a Minecraft resource pack using a plugin.

### Configure Software

Once installed I would recommend the following plugins using `Filter -> Plugins`:

- Plaster
- Resource Pack Exporter
- Shape Generator
- Texture Editor

### Creating the Model

Now you are free to use the Software to create your own models which sit upon a Minecart.  
The Minecart will still be visible but with the shape of the custom model taking form on top, hiding it.

I have created a custom model which should fully consume the default Minecart model and give you some points of reference: [Download here](/files/Minecart.bbmodel)

### Export the Model

Once you're ready to test out your creation, go to `File -> Export -> Export Resource Pack`. Give the Resource Pack a name and description to reference it easily and save it.

The Model will be packaged into a zip file that Minecraft will accept as a Resource Pack. 
Open up Minecraft `Options... -> Resource Packs... -> Open Pack Folder` drop your freshly created zip file in here. Minecraft should update the results to include your new Resource Pack.

Click the Arrow on your Resource Pack, ignore any message about it possibly being incompatible, and click Done.

**By Default, the Exporting Resource Pack will override the `GLASS` Material (You could change this).**

To test your model, create a new car type `/carz createtype` go through the steps and specify the `Fill Material` as `GLASS`. Obtain the car using `/carz spawn (type)` and place the Minecart received and you should see your new model! 
