package hr.kravarscan.enchantedfortress.storage;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hr.kravarscan.enchantedfortress.logic.Game;

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

public class HighScores {
    private static final String ScoresFileName = "highscores.dat";
    private static HighScores instance = null;

    public static HighScores get() {
        if (instance == null)
            instance = new HighScores();

        return instance;
    }

    private HighScores() {
    }

    private final Map<Integer, List<ScoreEntry>> scores = new HashMap<>();

    public void add(Game game, Context context) {
        Integer diffIndex = new Integer(game.getDifficulty().getIndex());

        if (!this.scores.containsKey(diffIndex))
            this.scores.put(diffIndex, new ArrayList<ScoreEntry>());
        List<ScoreEntry> modeScores = this.scores.get(diffIndex);

        ScoreEntry score = new ScoreEntry(game.turn, diffIndex);
        int putIndex = 0;

        for(; putIndex < modeScores.size(); putIndex++)
            if (score.isBetter(modeScores.get(putIndex)))
                break;
        modeScores.add(putIndex, score);

        save(context);
    }

    private void save(Context context) {
        try {
            FileOutputStream stream = context.openFileOutput(ScoresFileName, Context.MODE_PRIVATE);
            byte[] byteBuffer = new byte[Double.SIZE / Byte.SIZE];

            for(Map.Entry<Integer, List<ScoreEntry>> group : this.scores.entrySet()) {
                stream.write(group.getKey());
                stream.write(group.getValue().size());

                for(ScoreEntry score : group.getValue()) {
                    double[] data = score.saveData();

                    for (double val : data) {
                        ByteBuffer.wrap(byteBuffer).putDouble(val);
                        stream.write(byteBuffer);
                    }
                }
            }

            stream.close();
            Log.i("HighScores", "Saved");
        } catch (Exception e) {
            Log.e("HighScores", "Autosave failed", e);
        }
    }
}
