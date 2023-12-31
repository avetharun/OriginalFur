Origin Fur - Origin Extension
======


Adding custom models
------

Custom model path: `assets/some_namespace/furs/origin_name.json`

Optionally, if you want a specific model to not override origins with the same name (conflicting names, eg if Origin pack opa uses a name called "phantom" and so does Origin Pack originb, you can do opa.phantom and originb.phantom ) you can append your origin's namespace, as follows:

`assets/some_namespace/furs/namespace.origin_name.json`


Using Origins Classes, if you create a custom class, you can also create a fur with the same format. 

In code, this is done using origins-classes.class.json as the filename.
As with the rest of the files, you can remove the namespace, but it's better to keep it there for redundancy.

If your origin is in a subfolder of origins, for example the below image, you NEED to use a namespace! Subfolders work as namespaces, so roiraces.impact1.elf.json would be used for the below image. Removing the namespace (roiraces in this instance) will most likely not work.

.. image:: https://user-images.githubusercontent.com/34732532/267094238-b3b796dc-29ac-4c52-9bfb-d304c2326b13.png
  :width: 255
  :alt: Alternative text

When creating a new model, make sure if cubes are zero-width (flat on any axis) ONE of the faces is fully transparent. 
This is because translucency is supported, and can cause clipping if this change isn't made.

Note: Using the keyword `orif-defaults` for namespaces for a new fur will NOT override existing ones!
Full json file example
::
  {
    
    "model": "some_namespace:geo/custommodel.geo.json",
    "texture": "some_namespace:textures/custom/customorigin.png",
    "fullbrightTexture": "some_namespace:textures/custom/customorigin_emission.png",
    "animation": "some_namespace:animations/custommodel.animation.json",
    "elytraTexture": "some_namespace:textures/custom/custom_elytra.png"
    "playerInvisible": true,
    "overlay": "some_namespace:textures/custom/skin_overlay.png",
    "emissive_overlay": "some_namespace:textures/custom/emissive_skin_overlay.png",
    "hidden": [
      "leftArm","rightArm", "body", "jacket", "head", "hat", "leftLeg", "rightLeg", "leftPants", "rightPants"
    ],
    "rendering_offsets": {
      "left": [0,0.3,0],
      "right": [0,0.3,0]
    }
  }

Description of each option in the fur json file:
-----
- model : Custom GeckoLib / AzureLib model

- texture : Geo model's texture

- fullbrightTexture : Geo model's emissive texture

- animation : Geo model's animation (plays indefinitely)

- elytraTexture : Elytra texture specific to this origin

- playerInvisible : Completely hides the player's default model

- hidden : List of player parts to hide. Note: It is more efficient to set the entire player to be invisible than to hide each part individually

- overlay : Texture to draw on top of the player's skin (Note: hidden parts will not be drawn)

- overlay_slim : Texture to draw on top of the player's skin, which will only display for Slim (Alex) skins.

  - *Note: if this is defined, overlay will also only display for Wide (Steve) skins.*

- emissive_overlay : Emissive texture to draw on top of the player's skin

- emissive_overlay_slim : Emissive texture to draw on top of the player's skin, which will only display for Slim (Alex) skins

  - *Note: if this is defined, emissive_overlay will also only display for Wide (Steve) skins.*

- rendering_offsets : Offsets for specific rendering parts. See below for description

  - left : Left arm's item rendering offset

  - right : Right arm's item rendering offset
  
