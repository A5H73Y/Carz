Economy
======

Carz can link to any Economy plugin that supports Vault.

When linked, each Carz purchase type can have an associated cost. Any economy based purchase will be prevented until the player has earned enough money (for example buying a car).

The configuration option in the `config.yml` is `Vault.Enabled` and must be set to `true`. When Carz starts up, it will print a message whether it connected to Vault Economy successfully.

Vault created by Sleaker, available here: [https://www.spigotmc.org/resources/vault.34315/](https://www.spigotmc.org/resources/vault.34315/)

## Purchase Confirmation

If configured, the player can be asked to confirm purchases before the cost amount is deducted from their account. This acts as a good way to provide a summary of what the user is about to do, for example purchase a car.

![Purchase Confirmation](https://i.imgur.com/l8d6LT2.png "Purchase Confirmation")

## Custom Costs

Each type of purchase will have a default cost defined in the config, with exception to car types which will each have an individual defined cost.

These costs can be overridden through Carz signs, by specifying the new cost on the bottom line.

![Custom Costs](https://i.imgur.com/xOAh7Ce.png "Custom Costs")

## Economy Summary

Entering the command `/carz economy` will display a breakdown of each cost and various economy details.

![Economy Summary](https://i.imgur.com/fVQekYD.png "Economy Summary")
