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

import javafx.geometry.Point2D;
import javafx.scene.Node;
import org.openqa.selenium.interactions.PointerInput.MouseButton;

public interface IMouseFxRobot<T extends IMouseFxRobot<?>>
{
    /**
     * Move cursor to the provided location.
     *
     * @param location in screen coordinates
     */
    T move(Point2D location);

    /**
     * Move cursor to the provided location.
     *
     * @param x horizontal screen coordinate
     * @param y vertical screen coordinate
     */
    default T move(int x, int y)
    {
        return move(new Point2D(x, y));
    }

    /**
     * Left mouse click on the current position.
     */
    default T click()
    {
        return click(MouseButton.LEFT);
    }

    /**
     * Mouse click on the current position
     *
     * @param button mouse button
     */
    T click(MouseButton button);

    /**
     * Click by left mouse button on the provided screen position.
     *
     * @param x horizontal screen position
     * @param y vertical screen position
     */
    default T click(int x, int y)
    {
        return click(x, y, MouseButton.LEFT);
    }

    /**
     * Mouse click on the provided screen position.
     *
     * @param x horizontal screen position
     * @param y vertical screen position
     * @param button mouse button
     */
    T click(int x, int y, MouseButton button);

    /**
     * Double left mouse button click on the provided screen position.
     *
     * @param x horizontal screen position
     * @param y vertical screen position
     */
    default T doubleClick(int x, int y)
    {
        return doubleClick(x, y, MouseButton.LEFT);
    }

    /**
     * Double mouse button click on the provided screen position.
     *
     * @param x horizontal screen position
     * @param y vertical screen position
     * @param button mouse button
     */
    T doubleClick(int x, int y, MouseButton button);

    /**
     * Double mouse click on the current position
     *
     * @param button mouse button
     */
    T doubleClick(MouseButton button);

    /**
     * Left click on the middle of the node.
     *
     * @param node JavaFx node
     */
    default T click(Node node)
    {
        return click(node, MouseButton.LEFT);
    }

    /**
     * Mouse click on the middle of the node.
     *
     * @param node JavaFx node
     * @param button mouse button
     */
    T click(Node node, MouseButton button);

    /**
     * Press mouse button.
     * @param button mouse button
     */
    FxRobot mouseDown(MouseButton button);

    /**
     * Release button.
     * @param button mouse button
     */
    FxRobot mouseUp(MouseButton button);
}
