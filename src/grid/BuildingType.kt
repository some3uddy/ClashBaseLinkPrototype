package grid

enum class BuildingType(val amount: Int, val size: Int = 3) {
    WALL(325, 1),
    TOWNHALL(1, 4),
    CLAN_CASTLE(1),
    ARCHER_TOWER(2),
    WIZARD_TOWER(5),
    AIR_DEFENSE(4),
    MORTAR(3),
    MULTI_MORTAR(1),
    HIDDEN_TESLA(5, 2),
    XBOW(4),
    INFERNO_TOWER(3, 2),
    AIR_SWEEPER(2, 2),
    BOMB_TOWER(2),
    SCATTERSHOT(2),
    SPELL_TOWER(2, 2),
    MONOLITH(1),
    MULTI_GEAR_TOWER(1),
    MULTI_ARCHER_TOWER(3),
    RICOCHET_CANNON(3),
    FIRESPITTER(2),
    HERO_ALTAR(4, 2),
    BUILDERS_HUT(5, 2),
    BOBS_HUT(1, 2),
    ELIXIR_COLLECTOR(7),
    ELIXIR_STORAGE(4),
    GOLD_MINE(7),
    GOLD_STORAGE(4),
    DARK_ELIXIR_DRILL(3),
    DARK_ELIXIR_STORAGE(1),
    HELPER_HUT(1),
    ARMY_CAMP(4, 4),
    BARRACKS(1),
    LABORATORY(1),
    SPELL_FACTORY(1),
    DARK_BARRACKS(1),
    DARK_SPELL_FACTORY(1),
    WORKSHOP(1, 4),
    PET_HOUSE(1),
    BLACKSMITH(1),
    HERO_HALL(1, 4),
    BOMB(8, 1),
    SPRING_TRAP(9, 1),
    GIANT_BOMB(8, 2),
    AIR_BOMB(7, 1),
    SEEKING_AIR_MINE(9, 1),
    SKELETON_TRAP(4, 1),
    TORNADO_TRAP(1, 1),
    GIGA_BOMB(1, 2);

    companion object {
        val buildings: List<Building> by lazy {
            var idCounter = 0
            return@lazy entries.flatMap { type ->
                List(type.size) {
                    Building(idCounter, type).also {
                        idCounter++
                    }
                }
            }
        }

        val idToBuildingMap: Map<Int, Building> by lazy { buildings.associateBy { it.id } }

    }
}