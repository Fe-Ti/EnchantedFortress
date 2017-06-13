package hr.kravarscan.enchantedfortress.logic;

import java.util.Random;

import hr.kravarscan.enchantedfortress.storage.LatestSaveKeys;

/**
 * Copyright 2017 Ivan Kravarščan
 *
 * This file is part of Enchanted Fortress.
 *
 * Enchanted Fortress is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Enchanted Fortress is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Enchanted Fortress.  If not, see <http://www.gnu.org/licenses/>.
 */

public class Game {
    private static final double WallCost = 100;
    private static final int DemonStrength = 5;
    private static final int CombatRounds = 10;
    private static final int MaxBanishCost = 10000000;
    private static final int MaxDemonGates = 100000000;
    private static final int MaxPopulation = 100000;
    private static final double NatalityPerFood = 1 / 12.0;
    private static final double Mortality = 1 / 50.0;

    private static final double FarmingBase = 3;
    private static final double BuilderBase = 10;
    private static final double CivilStrength = 1;
    private static final double WallCivilStrength = 4;
    private static final double SoldierStrength = 10;
    private static final double WallSoldierStrength = 20;
    private static final double ResearchBase = 0.1;
    private static final double ScholarResearch = 1 - ResearchBase;
    private static final double FarmingTechBouns = 1 / 12.0;
    private static final double BuildingTechBouns = 1;
    private static final double SoldieringTechBouns = 1 / 1.0;
    private static final double ResearchTechBouns = 1 / 8.0;

    private static Random rand = new Random();

    public int turn = 1;
    public double population = 100;
    public double walls = 0;
    private int demons = 0;
    private int demonGates = 0;
    public int demonBanishCost = 10000;

    public static final double SliderTicks = 20;
    public int farmerSlider = 10;
    public int builderSlider = 2;
    public int soldierSlider = 0;
    private int selectedTech = 0;

    public Technology farming = new Technology();
    public Technology building = new Technology();
    public Technology soldiering = new Technology();
    public Technology scholarship = new Technology();

    public int reportAttackers = 0;
    public int reportVictims = 0;
    public int reportScoutedDemons = 0;

    /*
        Sliders
     */
    public int getScholarSlider() {
        return (int) SliderTicks - this.farmerSlider - this.builderSlider - this.soldierSlider;
    }

    public void decBuilders() {
        this.builderSlider = moveSlider(this.builderSlider, -1, minBuilders());
    }

    public void incBuilders() {
        this.builderSlider = moveSlider(this.builderSlider, 1, 0);
    }

    public void decFarmers() {
        this.farmerSlider = moveSlider(this.farmerSlider, -1, minFarmers());
    }

    public void incFarmers() {
        this.farmerSlider = moveSlider(this.farmerSlider, 1, 0);
    }

    public void decSoldiers() {
        this.soldierSlider = moveSlider(this.soldierSlider, -1, 0);
    }

    public void incSoldiers() {
        this.soldierSlider = moveSlider(this.soldierSlider, 1, 0);
    }

    private int moveSlider(int sliderValue, int delta, double minimumPop) {
        int sum = this.builderSlider + this.farmerSlider + this.soldierSlider + delta;
        if (sum < 0 || sum > SliderTicks)
            return sliderValue;

        sliderValue += delta;
        int minSlider = (int) Math.ceil(SliderTicks * minimumPop / this.effectivePop());

        if (sliderValue < minSlider) {
            sliderValue = minSlider;
        }
        if (sliderValue > SliderTicks) {
            sliderValue = (int) SliderTicks;
        }

        return sliderValue;
    }

    private double minBuilders() {
        return this.effectivePop() / this.builderEfficiency();
    }

    private double minFarmers() {
        return this.effectivePop() * (1 + Mortality / NatalityPerFood) / this.farmerEfficiency();
    }

    public void selectTech(int i) {
        if (i < 0 || i > 5)
            return;

        this.selectedTech = i;
    }

    /*
        Derivative values
     */
    private double effectivePop()
    {
        return Math.floor(this.population);
    }
    private int farmers() {
        return (int) (this.effectivePop() * this.farmerSlider / SliderTicks);
    }

    private int builders() {
        return (int) (this.effectivePop() * this.builderSlider / SliderTicks);
    }

    private int soldiers() {
        return (int) (this.effectivePop() * this.soldierSlider / SliderTicks);
    }

    private int scholars() {
        return (int)this.effectivePop() - this.farmers() - this.builders() - this.soldiers();
    }

    private double builderEfficiency() {
        return BuilderBase + this.building.level * BuildingTechBouns;
    }

    private double farmerEfficiency() {
        return FarmingBase + this.farming.level * FarmingTechBouns;
    }

    public int realDeltaPop() {
        return Utils.integerDelta(this.population, this.deltaPop());
    }

    public double deltaWalls() {
        return Math.max(
                (this.builders() * builderEfficiency() - this.effectivePop() - (int) this.walls) / WallCost,
                0);
    }

    public double militaryStrength() {
        int civilians = (int)this.effectivePop() - this.soldiers();

        int wallSoldiers = (int) Math.min(this.soldiers(), this.walls);
        int groundSoldiers = this.soldiers() - wallSoldiers;
        int wallCivils = Math.min((int) this.walls - wallSoldiers, civilians);
        int groundCivils = civilians - wallCivils;

        return groundCivils * CivilStrength +
                wallCivils * WallCivilStrength +
                (groundSoldiers * SoldierStrength +
                wallSoldiers * WallSoldierStrength) * (1 + this.soldiering.level * SoldieringTechBouns);
    }

    public double deltaResearch() {
        return (this.effectivePop() * ResearchBase + this.scholars() * ScholarResearch) * (1 + this.scholarship.level * ResearchTechBouns);
    }

    public boolean isOver() {
        return this.population <= 0 || this.demonBanishCost <= 0;
    }

    private double deltaPop() {
        return (this.farmers() * farmerEfficiency() - this.effectivePop()) * NatalityPerFood - this.effectivePop() * Mortality;
    }
    /*
        Turn processing
     */
    public void endTurn() {
        this.reportAttackers = 0;
        this.reportVictims = 0;
        this.turn++;

        this.population += this.deltaPop();
        this.population = Utils.clamp(this.population, 0, MaxPopulation);

        this.walls += this.deltaWalls();
        if (this.walls > MaxPopulation)
            this.walls = MaxPopulation;

        this.doResearch();
        this.doCombat();
        this.spawnDemons();
        this.doScouting();
        this.correctSliders();
    }

    private void doResearch() {
        double researchPoints = this.deltaResearch();

        switch (this.selectedTech) {
            case 0:
                this.farming.Invest(researchPoints);
                break;
            case 1:
                this.building.Invest(researchPoints);
                break;
            case 2:
                this.soldiering.Invest(researchPoints);
                break;
            case 3:
                this.scholarship.Invest(researchPoints);
                break;
            case 4:
                this.demonBanishCost -= (int) researchPoints;
                if (this.demonBanishCost < 0)
                    this.demonBanishCost = 0;

                this.demonGates -= (int) (researchPoints / 100);
                if (this.demonGates < 0)
                    this.demonGates = 0;
                break;
        }
    }

    private void doCombat() {
        if (rand.nextDouble() * this.effectivePop() > this.demons)
            return;

        int attackers = rand.nextInt(this.demons + 1);
        double defenderStr = this.militaryStrength();
        double peopleStr = defenderStr / this.effectivePop();

        this.demons -= attackers;
        this.reportAttackers = attackers;
        attackers *= DemonStrength;

        for (int i = 0; attackers > 0 && defenderStr > 0 && i < CombatRounds; i++) {
            if (rand.nextDouble() > 0.5) {
                defenderStr -= (int) (attackers * rand.nextDouble());
                if (defenderStr < 0)
                    continue;
                attackers -= (int) (defenderStr * rand.nextDouble());
            } else {
                attackers -= (int) (defenderStr * rand.nextDouble());
                if (attackers < 0)
                    continue;
                defenderStr -= (int) (attackers * rand.nextDouble());
            }
        }

        if (attackers < 0)
            attackers = 0;
        if (defenderStr < 0)
            defenderStr = 0;

        attackers /= DemonStrength;
        this.demons += attackers / DemonStrength;
        if (this.demons < 0)
            this.demons = 0;

        this.reportVictims = (int) ((this.militaryStrength() - defenderStr) / peopleStr);
        this.population -= this.reportVictims;

        this.reportScoutedDemons -= this.reportAttackers;
        if (this.reportScoutedDemons < 0)
            this.reportScoutedDemons = 0;
    }

    private void spawnDemons() {
        if (this.demonBanishCost > 0) {
            this.demonGates += this.effectivePop() + this.demonBanishCost / 100;
            if (this.demonGates > MaxDemonGates)
                this.demonGates = MaxDemonGates;

            int deltaCost = this.demonGates / 100;
            this.demonBanishCost += deltaCost < this.effectivePop() ? this.effectivePop() : deltaCost;
            if (this.demonBanishCost > MaxBanishCost)
                this.demonBanishCost = MaxBanishCost;
        }

        if (this.demonGates > 0) {
            this.demons += this.demonGates / 1000;
            if (this.demons > MaxPopulation) this.demons = MaxPopulation;
        }
    }

    private void doScouting() {
        if (this.demons > this.reportScoutedDemons)
            this.reportScoutedDemons += rand.nextInt(this.demons - this.reportScoutedDemons);
        else {
            this.reportScoutedDemons = this.demons;
        }
    }

    private void correctSliders() {
        this.farmerSlider = moveSlider(this.farmerSlider, 0, minFarmers());
        this.builderSlider = moveSlider(this.builderSlider, 0, minBuilders());

        while (sliderOverflow() > 0 && moveSlider(this.farmerSlider, -1, minFarmers()) != this.farmerSlider)
            this.farmerSlider--;

        while (sliderOverflow() > 0 && this.soldierSlider > 0)
            this.soldierSlider--;

        while (sliderOverflow() > 0 && moveSlider(this.builderSlider, -1, minBuilders()) != this.builderSlider)
            this.builderSlider--;
    }

    private int sliderOverflow() {
        return this.farmerSlider + this.builderSlider + this.soldierSlider - (int) SliderTicks;
    }

    /*
        Saving and loading
     */
    public double[] save() {
        return new double[]
                {
                        this.turn,
                        this.population,
                        this.walls,
                        this.demons,
                        this.demonGates,
                        this.demonBanishCost,

                        this.farmerSlider,
                        this.builderSlider,
                        this.soldierSlider,
                        this.selectedTech,

                        this.farming.level,
                        this.farming.points,
                        this.building.level,
                        this.building.points,
                        this.soldiering.level,
                        this.soldiering.points,
                        this.scholarship.level,
                        this.scholarship.points,

                        this.reportAttackers,
                        this.reportVictims,
                        this.reportScoutedDemons,
                };
    }

    public void load(double[] data) {
        this.turn = (int) data[LatestSaveKeys.TURN.ordinal()];
        this.population = (int) data[LatestSaveKeys.POPULATION.ordinal()];
        this.walls = data[LatestSaveKeys.WALLS.ordinal()];
        this.demons = (int) data[LatestSaveKeys.DEMONS.ordinal()];
        this.demonGates = (int) data[LatestSaveKeys.DEMON_GATES.ordinal()];
        this.demonBanishCost = (int) data[LatestSaveKeys.DEMON_BANISH_COST.ordinal()];

        this.farmerSlider = (int) data[LatestSaveKeys.FARMER_SLIDER.ordinal()];
        this.builderSlider = (int) data[LatestSaveKeys.BUILDER_SLIDER.ordinal()];
        this.soldierSlider = (int) data[LatestSaveKeys.SOLDIER_SLIDER.ordinal()];
        this.selectedTech = (int) data[LatestSaveKeys.SELECTED_TECH.ordinal()];

        this.farming.level = (int) data[LatestSaveKeys.FARMING_LEVEL.ordinal()];
        this.farming.points = data[LatestSaveKeys.FARMING_POINTS.ordinal()];
        this.building.level = (int) data[LatestSaveKeys.BUILDING_LEVEL.ordinal()];
        this.building.points = data[LatestSaveKeys.BUILDING_POINTS.ordinal()];
        this.soldiering.level = (int) data[LatestSaveKeys.SOLDIERING_LEVEL.ordinal()];
        this.soldiering.points = data[LatestSaveKeys.SOLDIERING_POINTS.ordinal()];
        this.scholarship.level = (int) data[LatestSaveKeys.SCHOLARSHIP_LEVEL.ordinal()];
        this.scholarship.points = data[LatestSaveKeys.SCHOLARSHIP_POINTS.ordinal()];

        this.reportAttackers = (int) data[LatestSaveKeys.REPORT_ATTACKERS.ordinal()];
        this.reportVictims = (int) data[LatestSaveKeys.REPORT_VICTIMS.ordinal()];
        this.reportScoutedDemons = (int) data[LatestSaveKeys.REPORT_SCOUTED_DEMONS.ordinal()];
    }
}
