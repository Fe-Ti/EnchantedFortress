/*
 * Copyright 2020 Ivan Kravarščan
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

package hr.kravarscan.enchantedfortress.storage;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.kravarscan.enchantedfortress.BuildConfig;
import hr.kravarscan.enchantedfortress.logic.Difficulty;
import hr.kravarscan.enchantedfortress.logic.Game;
import hr.kravarscan.enchantedfortress.logic.Utils;

public class HighScores {
    private static final String LOG_TAG = "HighScores";
    private static final int MaxScores = 10;

    private static final String ScoresFileName = "highscores.dat";
    private static HighScores instance = null;
    private boolean loaded = false;

    public static HighScores get() {
        if (instance == null)
            instance = new HighScores();

        return instance;
    }

    private HighScores() {
    }

    private final Map<Integer, List<ScoreEntry>> scores = new HashMap<>();

    public void add(Game game, Context context) {
        if (!game.isPlayerAlive()) {
            Log.d(LOG_TAG, "add, player dead, no new high score");
            return;
        }

        this.load(context);

        Integer diffIndex = game.getDifficulty().getIndex();
        Log.d(LOG_TAG, "add, difficulty index: " + diffIndex);

        if (!this.scores.containsKey(diffIndex)) {
            Log.d(LOG_TAG, "add, entry for difficulty level");
            this.scores.put(diffIndex, new ArrayList<ScoreEntry>());
        }
        List<ScoreEntry> modeScores = this.scores.get(diffIndex);

        ScoreEntry score = new ScoreEntry(game.turn, diffIndex, game.getName());
        int putIndex = 0;

        for (; putIndex < modeScores.size(); putIndex++)
            if (score.isBetter(modeScores.get(putIndex)))
                break;
        modeScores.add(putIndex, score);
        Log.d(LOG_TAG, "add, inserted at " + putIndex + ", list size: " + modeScores.size());

        while (modeScores.size() > MaxScores)
            modeScores.remove(MaxScores);

        save(context);
    }

    public boolean hasAny(Context context)
    {
        this.load(context);

        for (int diff = Difficulty.Levels.length - 1; diff >= 0; diff--)
            if (this.scores.containsKey(diff) && this.scores.get(diff).size() > 0)
                return true;

        return false;
    }

    public List<ScoreEntry> getAll(Context context) {
        List<ScoreEntry> list = new ArrayList<>();

        this.load(context);

        for (int diff = Difficulty.Levels.length - 1; diff >= 0; diff--)
            if (this.scores.containsKey(diff))
                list.addAll(this.scores.get(diff));

        return list;
    }

    private void load(Context context) {
        if (this.loaded)
            return;

        this.scores.clear();
        byte[] byteBuffer = new byte[Double.SIZE / Byte.SIZE];

        Log.d(LOG_TAG, "Loading");
        try {
            FileInputStream stream = context.openFileInput(ScoresFileName);

            Utils.readStream(stream, byteBuffer);
            int version = (int)ByteBuffer.wrap(byteBuffer).getDouble();

            if (version < 8) {
                stream.close();
                Log.i(LOG_TAG, "load rejected, was faulty in v1.7");
                return;
            }

            while (stream.available() > 0) {
                int mode, count;
                if (version < 15) {
                    mode = stream.read();
                    count = stream.read();
                }
                else
                {
                    byteBuffer = new byte[2 * Integer.SIZE / Byte.SIZE];
                    Utils.readStream(stream, byteBuffer);
                    ByteBuffer wrapper = ByteBuffer.wrap(byteBuffer);

                    mode = wrapper.getInt();
                    count = wrapper.getInt();
                }
                List<ScoreEntry> modeScores = new ArrayList<>();

                Log.d(LOG_TAG, "load mode: " + mode + ", entry count: " + count);
                for (int i = 0; i < count; i++) {
                    modeScores.add(ScoreEntry.Load(stream, version));
                }

                this.scores.put(mode, modeScores);
            }

            stream.close();
            Log.i(LOG_TAG, "loaded");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Loading high scores failed", e);
        }

        this.loaded = true;
    }

    private void save(Context context) {
        byte[] byteBuffer = new byte[Double.SIZE / Byte.SIZE];

        Log.d(LOG_TAG, "Saving");
        try {
            FileOutputStream stream = context.openFileOutput(ScoresFileName, Context.MODE_PRIVATE);

            ByteBuffer.wrap(byteBuffer).putDouble(BuildConfig.VERSION_CODE);
            stream.write(byteBuffer);

            for (Map.Entry<Integer, List<ScoreEntry>> group : this.scores.entrySet()) {
                byteBuffer = new byte[2 * Integer.SIZE / Byte.SIZE];
                ByteBuffer bufferBuilder = ByteBuffer.wrap(byteBuffer);
                bufferBuilder.putInt(group.getKey());
                bufferBuilder.putInt(group.getValue().size());
                stream.write(byteBuffer);

                for (ScoreEntry score : group.getValue()) {
                    stream.write(score.saveData());
                }
            }

            stream.close();
            Log.d(LOG_TAG, "Saved");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Saving high scores failed", e);
        }
    }

    public void clear(Context context) {
        this.scores.clear();

        try
        {
            context.deleteFile(ScoresFileName);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Clearing high scores failed", e);
        }
    }
}
