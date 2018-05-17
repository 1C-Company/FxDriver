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

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.server.DriverProvider;

public class FxDriverProvider implements DriverProvider
{
    private static final String BROWSER_NAME = "javafx";

    @Override
    public Capabilities getProvidedCapabilities()
    {
        return new DesiredCapabilities(BROWSER_NAME, "", Platform.ANY);
    }

    @Override
    public boolean canCreateDriverInstanceFor(Capabilities capabilities)
    {
        return BROWSER_NAME.equals(capabilities.getBrowserName());
    }

    @Override
    public WebDriver newInstance(Capabilities capabilities)
    {
        return new FxDriver(capabilities);
    }
}
