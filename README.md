Zero [![Build Status](https://travis-ci.org/flaiker/zero.svg?branch=master)](https://travis-ci.org/flaiker/zero)
====

LibGDX Platformer using Box2D
![ingame image](http://i.imgur.com/kBFkgzD.png)

## Features

- Dynamic level/map loading from [Tiled](http://www.mapeditor.org/) map files
- Physics (Box2d)
- Dynamic lighting (box2dlights)
- Entity animations
- Abilities [WIP]


## Technical documentation

### Used libraries
| Name | Version |
|------|---------|
| [LibGDX](https://github.com/libgdx/libgdx) | 1.7.1 |
| [Box2D](https://github.com/libgdx/libgdx/wiki/Box2d) | 1.7.1 |
| [box2DLights](https://github.com/libgdx/box2dlights) | 1.4 |
| [reflections.org](https://github.com/ronmamo/reflections) | 0.9.10 |
| [mockito](https://github.com/mockito/mockito) | 1.9.5 |


### Project setup

To setup the project in your IDE you can follow the LibGDX wiki's
[guide](https://github.com/libgdx/libgdx/wiki/Setting-up-your-Development-Environment-%28Eclipse%2C-Intellij-IDEA%2C-NetBeans%29).
When using IntelliJ IDEA I recommend not using the integrated gradle
integration and instead running `./gradlew idea` for performance reasons.

1. `git clone git@github.com/flaiker/zero`
1. `cd zero`
1. `./gradlew idea` -> Open folder in IDEA
1. `./gradlew desktop:run` -> Start the game


### Asset regeneration

To make regeneration of texture atlas files easier and to help with
synchronising atlas files and tiled tilesets there is a second launchable
application in [desktop/.../AssetProductionLauncher](desktop/src/com/flaiker/zero/desktop/AssetProductionLauncher.java)
which recreates texture atlases based on the individual image files in the
asset directory.

One thing to note is that block-assets have a number prefix to provide
backward compatibility with map files that have been created using older
tilesets.

The process can be started using gradle with `./gradlew desktop:assets`.

More information on the asset regeneration progress can be found
in the inline documentation in the package [com.flaiker.zero.assetpipeline](core/src/com/flaiker/zero/assetpipeline).


### Release packaging
TODO


## Copyright

```
Copyright 2016 Fabian Lupa
```
