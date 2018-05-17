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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Function;

/**
 * The java agent class, that starts before main method.
 * <pre>
 * Receive optional parameters:
 *  <b>port</b> - port number for selenium server
 *  <b>extension</b> - the path to jar file with a extension class.
 *  <b>extension</b> - the path to jar file with a extension class.
 *  That class should implements <i>java.util.function.Function<Properties, Boolean></i> interface.
 *  Concrete class will be loaded using ServiceLoader mechanism.
 *  Method receive agent arguments. If method returns false, agent stops executions.
 *  This method will be executed before agent starts selenium server.
 * </pre>
 */
public class FxAgent
{
    @SuppressWarnings("unchecked")
    public static void premain(String args) throws Exception
    {
        Properties properties = parseArguments(args);

        int port = Integer.valueOf(properties.getProperty("port", "4444"));

        Thread agentThread = new Thread(() -> {
            try
            {
                if (properties.containsKey("extension"))
                {
                    File extensionJar = new File(properties.getProperty("extension"));
                    URLClassLoader extLoader = new URLClassLoader(new URL[] { extensionJar.toURI().toURL() });

                    ServiceLoader<Function> serviceLoader = ServiceLoader.load(Function.class, extLoader);
                    if (serviceLoader.iterator().hasNext())
                    {
                        Function<Properties, Boolean> extension = (Function<Properties, Boolean>)
                                serviceLoader.iterator().next();

                        if (!extension.apply(properties))
                            return;
                    }
                }

                URL self = new File(
                        FxAgent.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
                        .toURI().toURL();

                ClassLoader bootstrapLoader = ClassLoader.getSystemClassLoader().getParent();
                ClassLoader classLoader = new URLClassLoader(new URL[] {self}, bootstrapLoader);

                Class<?> fxAgent = classLoader.loadClass("com._1c.qa.selenium.fxdriver.FxServer");

                fxAgent.getMethod("start", int.class).invoke(null, port);
            }
            catch (Exception e)
            {
                // Print exception to the error stream, because it is dangerous to use logging libraries here
                e.printStackTrace();
            }
        });
        agentThread.setDaemon(true);
        agentThread.start();
    }

    private static Properties parseArguments(String args)
    {
        Properties properties = new Properties();

        if (args == null || args.equals(""))
            return properties;

        for (String param : args.split(","))
        {
            String[] arg = param.split("=");
            properties.setProperty(arg[0], arg[1]);
        }

        return properties;
    }
}

