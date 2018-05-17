/*
 * Copyright 2018 1C-Soft LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com._1c.qa.selenium.fxdriver;

import org.awaitility.Duration;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.remote.server.SeleniumServer;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * Stops SeleniumServer after JavaFX app will exit
 */
public class ShutdownHook extends Thread
{
    private SeleniumServer server;

    ShutdownHook(SeleniumServer server)
    {
        this.server = server;

        setDaemon(true);
    }

    @Override
    public void run()
    {
        Thread javaFxThread = await().forever()
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> getThreadByName("JavaFX Application Thread"), is(notNullValue()));

        try
        {
            javaFxThread.join();

            System.exit(0);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    private Thread getThreadByName(String threadName)
    {
        for (Thread thread : Thread.getAllStackTraces().keySet())
        {
            if (thread.getName().equals(threadName))
                return thread;
        }

        return null;
    }
}
