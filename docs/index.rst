Origin Fur - Origin Extension
======


Adding custom models
------

Custom model path: `assets/originalfur/furs/origin_name.json`

Full json file example
::
  {
    
    "model": "originalfur:geo/custommodel.geo.json",
    "texture": "originalfur:textures/custom/customorigin.png",
    "fullbrightTexture": "originalfur:textures/custom/customorigin_emission.png",
    "animation": "originalfur:animations/custommodel.animation.json",
    "elytraTexture": "originalfur:textures/custom/custom_elytra.png"
    "playerInvisible": true,
    "overlay": "originalfur:textures/custom/skin_overlay.png",
    "emissive_overlay": "originalfur:textures/custom/emissive_skin_overlay.png",
    "hidden": [
      "leftArm","rightArm", "body", "jacket", "head", "hat", "leftLeg", "rightLeg", "leftPants", "rightPants"
    ]
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

- emissive_overlay : Emissive texture to draw on top of the player's skin

- rendering_offsets : Offsets for specific rendering parts. See below for description

  - left : Left arm's item rendering offset

  - right : Right arm's item rendering offset
  
