Origin Fur - Origin Extension
======


Adding custom models
------

Custom model path: `assets/origin_namespace/furs/origin_name.json`
When creating a new model, make sure if cubes are zero-width (flat on any axis) ONE of the faces is fully transparent. 
This is because translucency is supported, and can cause clipping if this change isn't made.

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

  - priority : Order of which offset will take priority. Highest is picked (Default: -32767)

  - left : Left arm's item rendering offset

  - right : Right arm's item rendering offset

  - elytra : Offset to render the entire elytra from

  - cape : Offset to render the cape from

  - left_elytra : Offset to render the left elytra wing from

  - right_elytra : Offset to render the right elytra wing from

  - first_person_left : Offset to move the left hand in first person by

  - first_person_right : Offset to move the right hand in first person by
