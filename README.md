[https://thomasdarkson.com/](https://thomasdarkson.com/)

[<img alt="curseforge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg">](https://www.curseforge.com/minecraft/mc-mods/cats-on-head) [<img alt="modrinth" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg">](https://modrinth.com/mod/cats_on_head) 

[<img alt="fabric" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg">](https://fabricmc.net/)

![](https://i.ibb.co/wNv2b4X3/Yeni-Proje-2.png)

Requires [Trinket Canary](https://modrinth.com/mod/trinkets-canary "Trinket Canary") and [Cardinal Components API](https://modrinth.com/mod/cardinal-components-api "Cardinal Components API").

# Cats on Head
Cats on Head is a Fabric mod that allows you to put your tamed cats on your heads! It also includes improvements and fixes to cats. 

This mod will not work with custom cats or cat variants.

### Features:
- Makes tamed cats equippable 
- Cats can be leveled up with fishes
- Improved cat morning gifts

## Cat Items
Cats drop strings, unless you have this mod installed. With this mod, tamed\* cats drop themselves when hit by their owner once!

**Note:** Baby cats will not drop themselves as an item.
![hit](https://i.ibb.co/20ZxkdVV/1.gif "hit")

You can see your cat's information on tooltip.

The item entity of cats will never disappear and only the owner can pick their cat items off the ground.

## Putting Your Cat on Your Head
When you get your cat as an item, you can put it on your head for **Love of The Cat** status effect. Cats use a special slot appointed to them, meaning you can wear helmets and cats simultaneously.

![](https://i.ibb.co/yFZtdG9q/Ekran-g-r-nt-s-2025-07-11-184503.png)

When a cat is on your head, creepers will run away and they will never attack you unless provoked.

## Love of the Cat
Love of the Cat is a status effect you get when you put your cat on your head.
This status effect can be leveled up more by feeding your cat cooked cod or cooked salmon at maximum health.

Your cat's level is the same as Love of the Cat's level, they both depend on how many fish is fed.

Every **64** fish fed to your cat, the effect gets leveled up. Maximum level is **5**.

Level **1** beneficial effects (Fed fish >= 0):
- +**4** attack damage

Level **2** beneficial effects (Fed fish >= 64):
- Every effect from Level **1**
- 100% underwater movement speed

Level **3** beneficial effects (Fed fish >= 128):
- Every effect from Level **2**
- Jump boost
- Immunity to fall damage

Level **4** beneficial effects (Fed fish >= 192):
- Every effect from Level **3**
- 60% damage protection

Level **5** beneficial effects (Fed fish >= 256):
- Every effect from Level **4**
- 50% more luck to get better loot from fishing
- Poisoning is now unlocked, check **Fatal Poison** below

### Fatal Poison
Fatal Poison is an effect that's caused by feeding raw cod, raw salmon, cooked cod and cooked salmon to hostile mobs.
Feeding cod or salmon to hostile mobs will give them this effect, which is permanent and it will kill them unlike regular Poison. You unlock this at Love of the Cat Level 5.

You need 2 fish to poison the mobs, additionally you can upgrade the effect once with 2 extra fish.

![](https://i.ibb.co/5xMJptG8/Newproject-ezgif-com-optimize.gif)

## Sleeping
If you sleep with your cat on your head, your cat will get off your head and will sleep with you instead. 

If your cat's level is higher than 1, your cat's morning gifts will change based on the level.
The chance of your cat giving you a gift increases every level (if it's higher than 1), 15% for every level. When level is 3 or higher the morning gifts are guaranteed.

Level 2 morning gift's loot table is:
- Rolls: 1

|  Gift  |  Weight  |  Chance  | 1 in ...  | Count |
| ------------ | ------------ | ------------ | ------------ | ------------ |
|  Feather  |  5  | 27.77%  |  3.6  | 1 |
|  Iron Ingot  |  4  |  14.8%  |  4.5  | 1 |
|  Gold Ingot  | 3 | 16.6% | 6 | 1 |
|  Redstone  | 2 | 11.11% | 9 | 1 |
|  Phantom Membrane  | 2 | 11.11% | 9 | 1 |
|  Diamond  | 2 | 11.11% | 9 | 1 |

Level 3 morning gift's loot table is:
- Rolls: 1-2

|  Gift  |  Weight  |  Chance  | 1 in ...  |  Count |
| ------------ | ------------ | ------------ | ------------ | ------------ |
|  Salmon  |  5  | %41.6  |  2.4  | 22 |
|  Lapis Lazuli  |  4  |  %33.3  |  3  | 1 |
|  Emerald  | 3 | %25 | 4 | 1-4 |

Level 4 morning gift's loot table is:
- Rolls: 1-3

|  Gift  |  Weight  |  Chance  | 1 in ...  |  Count |
| ------------ | ------------ | ------------ | ------------ | ------------ |
|  String  |  7  | %46.6  |  2.14  | 11 |
|  Emerald  |  6  |  %40  |  2.5  | 1-11 |
|  Totem of Undying  | 2 | %13.3 | 7.5 | 1 |

Level 5 morning gift's loot table is:
- Rolls: 1-4

|  Gift  |  Weight  |  Chance  | 1 in ...  |  Count |
| ------------ | ------------ | ------------ | ------------ | ------------ |
|  Cooked Salmon  |  10  | %26  |  3.8  | 11-33 |
|  Block of Emerald  |  8  |  %21  |  4.75  | 1-3 |
|  Block of Diamond  | 7 | %18 | 5.4 | 1-3 |
|  Block of Raw Gold  | 7 | %18 | 5.4 | 1-3 |
|  Enchanted Golden Apple  | 3 | %7 | 12.6 | 1 |

## Adding Custom Cat Variants
With the release of 2.0.0, custom variant support for added and this mod itself takes advantage of that, with the "Niko" and "Abigail" cat variants.

The item model `cat_item` can be overriden (with a resource pack) to add more variants, it is located at `assets/cats_on_head/items/cat_item.json`.

For more info, check these:
- [Creating a resource pack](https://minecraft.wiki/w/Tutorial:Creating_a_resource_pack "Resource Pack")
- [Items model definition](https://minecraft.wiki/w/Items_model_definition "Items model definition")
- [Model](https://minecraft.wiki/w/Model "Model")
- [Blockbench](https://www.blockbench.net/ "Blockbench")