Block Types
======

## Description

For each Block Type, you are able to specify a list of Materials which make up that action. The available actions include: `climb, placeable, speed & launch`.

Each of these can be added to using `/carz add (type) (Material)`. Some may need an extra argument for the amount, such as the strength of the speed block, or the amount of height from a launch block.

_The contents of each block type are stored in the `blocks.yml` config file._

## Climb Block

Climb blocks provide the ability for a car to climb up certain types of Materials. The climb mechanic will check the block in front of the Minecart for a valid Material, check if there is space above it to climb to, then proceed to elevate the car up the block.

**If there are no specified Climb Blocks, EVERY Material is climbable.**

## Placeable Block

Placeable blocks provide the ability to restrict where players are able to physically place Cars.

**If there are no specified Placeable Blocks, EVERY Material is placeable.**

## Speed Block

Speed blocks provide the ability to give the car an extra speed boost when driven over, before gradually returning your car to its normal speed.

Speed blocks are also able to lower the cars speed when driven on, for example if the car should drive slowly on grass then set the speed block to a value below 1.0.

The value specified is a multiplier of the car's maximum speed, so `10` would be 1000% their speed, and `0.1` would be 10% of their speed.

There is validation to prevent numbers below 0 and over 100. Anything exceeding 100 will cause the Car to almost teleport forward which isn't advised. Anything under 0 will seemingly reverse the car, and could be used to restrict the car from entering certain areas. If desired, manual editing of the _blocks.yml_ file is required.

#### Examples

Setting GRASS_BLOCK to 0.1 Speed:

[Slow Block](https://thumbs.gfycat.com/PiercingMetallicItaliangreyhound-mobile.mp4 ':include :type=.mp4')

## Launch Block

Launch blocks provide the ability to add a y velocity of the car, giving the illusion of launching them into the air.

It's worth noting that values between 0.0 and 2.0 seem to be most effective, anything over 2 will launch too high.
