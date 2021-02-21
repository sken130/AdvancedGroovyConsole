/*
 *  Copyright 2021 Ken Lam
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kenlam.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleLog {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static void commonLog(String message) {
        LocalDateTime now = LocalDateTime.now();
        String nowStr = now.format(formatter);
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + nowStr + "] [" + threadName + "] " + message);
    }

}
