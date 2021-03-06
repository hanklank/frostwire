/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011-2017, FrostWire(R). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.frostwire.android.util;

import android.app.Fragment;
import android.content.Context;
import android.view.View;

import com.frostwire.android.BuildConfig;

import java.lang.reflect.Field;

/**
 * Utility class for runtime debugging.
 * <p>
 * None of the methods perform any operation is debug is not enabled.
 *
 * @author gubatron
 * @author aldenml
 */
public final class Debug {

    private Debug() {
    }

    /**
     * Returns if the application is running under a debug configuration.
     * <p>
     * The current implementation delegates the check to the native
     * android {@code BuildConfig} but that's not necessary the only way.
     *
     * @return {@code true} if running under debug
     */
    public static boolean isEnable() {
        return BuildConfig.DEBUG;
    }

    /**
     * Detects if any field/property owned by object can potentially
     * pin a context to memory.
     * <p>
     * This check can be used in places where some task is sent to
     * a background with a hard reference to a context, creating a
     * possible context leak.
     *
     * @param obj the object to inspect
     * @return {@code true} if the object can have a hard reference
     * to a context, {@code false} otherwise.
     */
    public static boolean hasContext(Object obj) {
        if (!isEnable()) {
            return false;
        }

        try {
            Class<?> clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();

            // BFS variation algorithm

            // noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < fields.length; i++) {
                Object value = fields[i].get(obj);

                if (value instanceof Context) {
                    return true;
                }
                if (value instanceof Fragment) {
                    return true;
                }
                if (value instanceof View) {
                    return true;
                }
            }

            // noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < fields.length; i++) {
                Object value = fields[i].get(obj);

                if (hasContext(value)) {
                    return true;
                }
            }

            return false;

        } catch (Throwable e) {
            // in case of a fatal error, this is just a runtime
            // check under debug, just let it run
            return false;
        }
    }
}
