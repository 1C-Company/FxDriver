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

import javafx.scene.input.MouseButton;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.PointerInput;

import java.awt.event.InputEvent;

/**
 * Map selenium buttons to the javafx button and the awt buttons.
 */
public enum MouseCouple
{
    LEFT(PointerInput.MouseButton.LEFT, MouseButton.PRIMARY, InputEvent.BUTTON1_MASK),
    RIGHT(PointerInput.MouseButton.RIGHT, MouseButton.SECONDARY, InputEvent.BUTTON3_MASK),
    MIDDLE(PointerInput.MouseButton.MIDDLE, MouseButton.MIDDLE, InputEvent.BUTTON2_MASK);

    private PointerInput.MouseButton seleniumButton;
    private MouseButton javafxButton;
    private int awtButton;

    MouseCouple(PointerInput.MouseButton seleniumButton, MouseButton javafxButton, int awtButton)
    {
        this.seleniumButton = seleniumButton;
        this.javafxButton = javafxButton;
        this.awtButton = awtButton;
    }

    public PointerInput.MouseButton getSeleniumButton()
    {
        return this.seleniumButton;
    }

    public MouseButton getJavafxButton()
    {
        return javafxButton;
    }

    public int getAwtButton()
    {
        return awtButton;
    }

    public static MouseCouple fromSeleniumButton(PointerInput.MouseButton seleniumButton)
    {
        for (MouseCouple button : values())
            if (button.getSeleniumButton() == seleniumButton)
                return button;

        throw new WebDriverException("Unable to find button record for: " + seleniumButton);
    }

    public static MouseButton convertToFxButton(PointerInput.MouseButton seleniumButton)
    {
        return fromSeleniumButton(seleniumButton).getJavafxButton();
    }

    public static int convertToAwtButton(PointerInput.MouseButton seleniumButton)
    {
        return fromSeleniumButton(seleniumButton).getAwtButton();
    }
}
