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
package com._1c.qa.selenium.fxdriver.robot;

import org.openqa.selenium.Keys;

public interface IKeyboardFxRobot<T extends IKeyboardFxRobot<?>>
{
    /**
     * Release keyboard keys.
     *
     * @param keys selenium keys
     */
    FxRobot keyUp(Keys...keys);

    /**
     * Press keyboard keys.
     *
     * @param keys selenium keys
     */
    FxRobot keyDown(Keys...keys);

    /**
     * Push several keys.
     *
     * @param keys selenium keys list.
     */
    FxRobot push(Keys ...keys);

    /**
     * Type text into current focused element.
     *
     * @param text text source
     */
    FxRobot type(String text);
}
