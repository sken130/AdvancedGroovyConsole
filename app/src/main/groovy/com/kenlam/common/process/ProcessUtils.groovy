/*
 *  Copyright 2016 Ken Lam
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

package com.kenlam.common.process

import org.apache.commons.io.output.TeeOutputStream
import org.apache.commons.io.output.ByteArrayOutputStream
import com.kenlam.common.io.StreamUtil

public class ProcessUtils {
    public static String getProcessOutput(Process process) {
        String processOutput = process.getInputStream().withStream{ InputStream istream ->
            return istream.getText()
        }
        return processOutput
    }
    
    public static Process startCommonProcess(File targetDir, List<String> commands) {
        def processBuilder = new ProcessBuilder(commands)
        processBuilder.redirectErrorStream(true)
        processBuilder.directory(targetDir)
        Process process = processBuilder.start()
        return process
    }
    
    public static String startProcessAndGetOutput(File targetDir, List<String> commands) {
        Process process = startCommonProcess(targetDir, commands)
        return getProcessOutput(process)
    }
    
    public static String startProcessAndStreamToSystemOut(File targetDir, List<String> commands) {
        Process process = startCommonProcess(targetDir, commands)
		ByteArrayOutputStream outputStreamCopy = new ByteArrayOutputStream()
		TeeOutputStream outputStream = new TeeOutputStream(System.out, outputStreamCopy)
		StreamUtil.transferStreamWithYieldSleep(process.getInputStream(), outputStream, 50)
		outputStreamCopy.close()
        String processOutput = outputStreamCopy.toString("utf8")
		return processOutput
    }
    
    public static void startProcess(File targetDir, List<String> commands) {
        startProcessAndStreamToSystemOut(targetDir, commands)
    }
}