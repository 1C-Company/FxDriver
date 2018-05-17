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

import com._1c.qa.selenium.fxdriver.robot.IKeyboardFxRobot;
import org.openqa.selenium.interactions.Keyboard;

import static com._1c.qa.selenium.fxdriver.KeysCouple.convertToSeleniumKeys;

public class FxKeyboard implements Keyboard
{
    private IKeyboardFxRobot<?> robot;

    public FxKeyboard(IKeyboardFxRobot<?> robot)
    {
        this.robot = robot;
    }

    @Override
    public void sendKeys(CharSequence... keysToSend)
    {
        for (CharSequence sequence : keysToSend)
        {
            robot.push(convertToSeleniumKeys(sequence));
        }
    }

    @Override
    public void pressKey(CharSequence keyToPress)
    {
        robot.keyDown(convertToSeleniumKeys(keyToPress));
    }

    @Override
    public void releaseKey(CharSequence keyToRelease)
    {
        robot.keyUp(convertToSeleniumKeys(keyToRelease));
    }
}
