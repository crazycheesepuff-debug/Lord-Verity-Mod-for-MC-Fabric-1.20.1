# Lord Verity (Fabric, Minecraft 1.20.1)

Ask a question in chat ("What's the capital of France?", anything ending in `?`, or starting with
what/who/where/when/why/how/which) and:

1. Lightning strikes 12-18 blocks away and Lord Verity (4 blocks tall) is standing there.
2. The time snaps to midnight.
3. "I don't give a damn! ahahaha!" plays globally for every player.
4. After the cackle he sprints at you, slaps you (head whips sideways, never lethal), and vanishes in smoke.
5. He drops **Lord Verity's Hat** (wearable) and the **Lord Verity - Master Of Humanity** music disc.

## Build

You need **JDK 17 or newer** installed.

    ./gradlew build          (Windows: gradlew.bat build)

The mod jar appears in `build/libs/` as `lord-verity-1.0.0.jar` (ignore the `-sources` one).
Drop it in your `mods` folder with **Fabric Loader 0.15+** and **Fabric API 0.92.2+1.20.1**.

To test straight from the project: `./gradlew runClient`.

## Testing without chat

* `/summon lordverity:lord_verity` or the spawn egg (Spawn Eggs tab): he picks the nearest player.
  (The lightning / midnight / sound only happen via the chat trigger.)
* `/give @s lordverity:lord_verity_disc`, `/give @s lordverity:lord_verity_hat`

## Tweak it

Everything is a constant in `src/main/java/com/lordverity/VerityConfig.java`: cooldown, spawn distance,
cackle time, run speed, slap damage/knockback/head spin, and so on.
The question detector is `VerityTrigger.isQuestion(...)`.

## Swap in better art

* Body skin: `assets/lordverity/textures/entity/lord_verity.png` (128x128, placeholder; keep the UV layout
  or edit `VerityModel.java`). The model is hand-built in `VerityModel.java`.
* Sounds: `assets/lordverity/sounds/damn.ogg` and `master_of_humanity.ogg`
* Disc icon: `assets/lordverity/textures/item/lord_verity_disc.png`
* Hat icon: `.../item/lord_verity_hat.png`; worn texture: `assets/minecraft/textures/models/armor/verity_hat_layer_1.png`
