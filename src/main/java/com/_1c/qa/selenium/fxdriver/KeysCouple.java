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

import javafx.scene.input.KeyCode;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriverException;

import java.util.ArrayList;
import java.util.List;

/**
 * Map Selenium keys to the JavaFx keys
 */
public enum KeysCouple
{
    ESC(Keys.ESCAPE, KeyCode.ESCAPE),
    INSERT(Keys.INSERT, KeyCode.INSERT),
    HOME(Keys.HOME, KeyCode.HOME),
    END(Keys.END, KeyCode.END),
    PAGE_UP(Keys.PAGE_UP, KeyCode.PAGE_UP),
    PAGE_DOWN(Keys.PAGE_DOWN, KeyCode.PAGE_DOWN),
    SHIFT(Keys.SHIFT, KeyCode.SHIFT),
    LEFT_SHIFT(Keys.LEFT_SHIFT, KeyCode.SHIFT),
    CONTROL(Keys.CONTROL, KeyCode.CONTROL),
    LEFT_CONTROL(Keys.LEFT_CONTROL, KeyCode.CONTROL),
    F1(Keys.F1, KeyCode.F1),
    F2(Keys.F2, KeyCode.F2),
    F3(Keys.F3, KeyCode.F3),
    F4(Keys.F4, KeyCode.F4),
    F5(Keys.F5, KeyCode.F5),
    F6(Keys.F6, KeyCode.F6),
    F7(Keys.F7, KeyCode.F7),
    F8(Keys.F8, KeyCode.F8),
    F9(Keys.F9, KeyCode.F9),
    F10(Keys.F10, KeyCode.F10),
    F11(Keys.F11, KeyCode.F11),
    F12(Keys.F12, KeyCode.F12),
    ENTER(Keys.ENTER, KeyCode.ENTER),
    RETURN(Keys.RETURN, KeyCode.ENTER),
    DOWN(Keys.DOWN, KeyCode.DOWN),
    ARROW_DOWN(Keys.ARROW_DOWN, KeyCode.DOWN),
    ARROW_LEFT(Keys.ARROW_LEFT, KeyCode.LEFT),
    ARROW_RIGHT(Keys.ARROW_RIGHT, KeyCode.RIGHT),
    ARROW_UP(Keys.ARROW_UP, KeyCode.UP),
    TAB(Keys.TAB, KeyCode.TAB),
    ALT(Keys.ALT, KeyCode.ALT),
    NUMPAD0(Keys.NUMPAD0, KeyCode.NUMPAD0),
    NUMPAD1(Keys.NUMPAD1, KeyCode.NUMPAD1),
    NUMPAD2(Keys.NUMPAD2, KeyCode.NUMPAD2),
    NUMPAD3(Keys.NUMPAD3, KeyCode.NUMPAD3),
    NUMPAD4(Keys.NUMPAD4, KeyCode.NUMPAD4),
    NUMPAD5(Keys.NUMPAD5, KeyCode.NUMPAD5),
    NUMPAD6(Keys.NUMPAD6, KeyCode.NUMPAD6),
    NUMPAD7(Keys.NUMPAD7, KeyCode.NUMPAD7),
    NUMPAD8(Keys.NUMPAD8, KeyCode.NUMPAD8),
    NUMPAD9(Keys.NUMPAD9, KeyCode.NUMPAD9),
    SPACE(Keys.SPACE, KeyCode.SPACE),
    BACK_SPACE(Keys.BACK_SPACE, KeyCode.BACK_SPACE);

    private Keys seleniumKey;
    private KeyCode javafxKey;

    KeysCouple(Keys seleniumKey, KeyCode javafxKey)
    {
        this.seleniumKey = seleniumKey;
        this.javafxKey = javafxKey;
    }

    public Keys getSeleniumKey()
    {
        return seleniumKey;
    }

    public KeyCode getJavafxKey()
    {
        return javafxKey;
    }

    public static KeysCouple fromSeleniumKey(Keys seleniumKey)
    {
        for (KeysCouple key : values())
            if (key.getSeleniumKey() == seleniumKey)
                return key;

        throw new WebDriverException("Unable to find key record for: " + seleniumKey);
    }

    public static Keys[] convertToSeleniumKeys(CharSequence sequence)
    {
        List<Keys> keys = new ArrayList<>(sequence.length());

        for (int i = 0; i < sequence.length(); i++)
        {
            Keys key = Keys.getKeyFromUnicode(sequence.charAt(i));

            if (key == Keys.NULL)
                continue;

            keys.add(key);
        }

        return keys.toArray(new Keys[0]);
    }

    public static KeyCode[] convertToJavaFxKeys(CharSequence sequence)
    {
        List<KeyCode> keys = new ArrayList<>(sequence.length());

        for (int i = 0; i < sequence.length(); i++)
        {
            Keys key = Keys.getKeyFromUnicode(sequence.charAt(i));

            if (key == Keys.NULL)
                continue;

            keys.add(KeysCouple.fromSeleniumKey(key).getJavafxKey());
        }

        return keys.toArray(new KeyCode[0]);
    }

    public static KeyCode convertKey(Keys key)
    {
        return fromSeleniumKey(key).getJavafxKey();
    }
}
