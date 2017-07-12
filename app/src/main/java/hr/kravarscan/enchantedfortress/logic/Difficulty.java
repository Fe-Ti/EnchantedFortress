package hr.kravarscan.enchantedfortress.logic;

import android.util.Log;

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

public class Difficulty {
    public static final Difficulty Medium = new Difficulty(100, 1);
    public static final Difficulty[] Levels = new Difficulty[]
            {
                    new Difficulty(120, 0.75),
                    Medium,
                    new Difficulty(80, 2),
            };

    private double startingPop;
    private double demonSpawnFactor;

    private Difficulty(double startingPop, double demonSpawnFactor) {
        this.startingPop = startingPop;
        this.demonSpawnFactor = demonSpawnFactor;
    }

    public double getStartingPop() {
        return startingPop;
    }

    public double getDemonSpawnFactor() {
        return demonSpawnFactor;
    }

    public int getIndex(){
        for (int i = 0; i < Levels.length; i++)
            if (Levels[i] == this)
                return i;

        Log.e("Difficulty", "Failed to find index of a difficulty level");
        return 1;
    }
}
