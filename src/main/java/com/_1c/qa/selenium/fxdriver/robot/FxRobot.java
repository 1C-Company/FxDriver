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

import com._1c.qa.selenium.fxdriver.KeysCouple;
import com._1c.qa.selenium.fxdriver.MouseCouple;
import com._1c.qa.selenium.fxdriver.NodeUtils;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.PointerInput;

import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.Toolkit;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implements various interaction with application under test.
 */
public class FxRobot implements IFxRobot
{
    private static final long SLEEP_AS_HUMAN = 32;

    private KeyState keyState = new KeyState();
    private AwtRobot awtRobot = new AwtRobot();

    @Override
    public FxRobot delay(long timeout)
    {
        try
        {
            Thread.sleep(timeout);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }

        return this;
    }

    @Override
    public FxRobot click(int x, int y, PointerInput.MouseButton button)
    {
        Point2D point = new Point2D(x, y);

        return move(point).click(button);
    }

    @Override
    public FxRobot doubleClick(int x, int y, PointerInput.MouseButton button)
    {
        Point2D point = new Point2D(x, y);

        return move(point).doubleClick(button);
    }

    @Override
    public FxRobot click(Node node, PointerInput.MouseButton button)
    {
        Point2D point = NodeUtils.getNodePoint(node);

        return move(point).move(node).click(button);
    }

    @Override
    public FxRobot eraseText(int length)
    {
        for (int i = 0; i < length; i++)
        {
            push(Keys.BACK_SPACE);
            delay(SLEEP_AS_HUMAN);
        }

        return this;
    }

    @Override
    public FxRobot move(Point2D location)
    {
        Point2D source = fromAwtPoint(MouseInfo.getPointerInfo().getLocation());
        if (source.equals(location))
            return this;

        List<Point2D> path = calculatePath(source, location);

        path.forEach(p -> {
            awtRobot.get().mouseMove((int)p.getX(), (int)p.getY());
            delay(SLEEP_AS_HUMAN);
        });

        awtRobot.get().mouseMove((int)location.getX(), (int)location.getY());
        return this;
    }

    @Override
    public FxRobot click(PointerInput.MouseButton button)
    {
        int awtButton = MouseCouple.convertToAwtButton(button);

        awtRobot.get().mousePress(awtButton);
        awtRobot.get().mouseRelease(awtButton);

        return this;
    }

    @Override
    public FxRobot doubleClick(PointerInput.MouseButton button)
    {
        click(button);
        click(button);

        return this;
    }

    @Override
    public FxRobot mouseDown(PointerInput.MouseButton button)
    {
        int awtButton = MouseCouple.convertToAwtButton(button);

        awtRobot.get().mousePress(awtButton);

        return this;
    }

    @Override
    public FxRobot mouseUp(PointerInput.MouseButton button)
    {
        int awtButton = MouseCouple.convertToAwtButton(button);

        awtRobot.get().mouseRelease(awtButton);

        return this;
    }

    @Override
    public FxRobot type(String text)
    {
        Scene scene = NodeUtils.getTargetWindow().getScene();

        text.chars().mapToObj(i -> (char)i).forEach(c ->
        {
            type(c, scene);
            delay(SLEEP_AS_HUMAN);
        });

        return this;
    }

    @Override
    public FxRobot push(Keys ...keys)
    {
        List<Keys> pressKeys = Arrays.asList(keys);
        List<Keys> releaseKeys = new ArrayList<>(pressKeys);
        Collections.reverse(releaseKeys);

        pressKeys.forEach(this::keyDown);
        releaseKeys.forEach(this::keyUp);

        return this;
    }

    @Override
    public FxRobot keyDown(Keys ...keys)
    {
        Scene scene = NodeUtils.getTargetWindow().getScene();

        Platform.runLater(() -> {
            for (Keys key : keys)
            {
                Event.fireEvent(getEventTarget(scene), createKeyEvent(KeyEvent.KEY_PRESSED,
                        KeysCouple.fromSeleniumKey(key).getJavafxKey(), ""));
            }
        });

        return this;
    }

    @Override
    public FxRobot keyUp(Keys ...keys)
    {
        Scene scene = NodeUtils.getTargetWindow().getScene();

        Platform.runLater(() -> {
            for (Keys key : keys)
            {
                Event.fireEvent(getEventTarget(scene), createKeyEvent(KeyEvent.KEY_RELEASED,
                        KeysCouple.fromSeleniumKey(key).getJavafxKey(), ""));
            }
        });

        return this;
    }

    private FxRobot move(Node node)
    {
        Point2D target = NodeUtils.getNodePoint(node);

        awtRobot.get().mouseMove((int)target.getX(), (int)target.getY());

        return this;
    }

    private List<Point2D> calculatePath(Point2D source, Point2D target)
    {
        List<Point2D> points = new ArrayList<>();
        int stepCount = pointDistance(source, target) / 50;
        if (stepCount > 8)
            stepCount = 8;

        for (int i = 0; i <= stepCount; i++)
        {
            double factor = (double)i / (double)stepCount;
            Point2D point = pointBetween(source, target, factor);
            points.add(point);
        }
        return Collections.unmodifiableList(points);
    }

    private int pointDistance(Point2D source, Point2D target)
    {
        return (int)Math.sqrt(Math.pow(target.getX() - source.getX(), 2) + Math.pow(target.getY() - source.getY(), 2));
    }

    private Point2D pointBetween(Point2D point0, Point2D point1, double factor)
    {
        double x = point0.getX() + ((point1.getX() - point0.getX()) * factor);
        double y = point0.getY() + ((point1.getY() - point0.getY()) * factor);

        return new Point2D(x, y);
    }

    private Point2D fromAwtPoint(Point awtPoint)
    {
        return new Point2D(awtPoint.getX(), awtPoint.getY());
    }

    private void type(char symbol, Scene scene)
    {
        KeyCode key = charToKey(symbol);

        Platform.runLater(() -> {
            Event.fireEvent(getEventTarget(scene), createKeyEvent(KeyEvent.KEY_PRESSED, key, ""));
            Event.fireEvent(getEventTarget(scene), createKeyEvent(KeyEvent.KEY_TYPED, key, String.valueOf(symbol)));
            Event.fireEvent(getEventTarget(scene), createKeyEvent(KeyEvent.KEY_RELEASED, key, ""));
        });
    }

    private KeyEvent createKeyEvent(EventType<KeyEvent> eventType, KeyCode keyCode, String character)
    {
        keyState.changeState(eventType, keyCode);

        boolean typed = eventType == KeyEvent.KEY_TYPED;
        String keyChar = typed ? character : KeyEvent.CHAR_UNDEFINED;
        String keyText = typed ? "" : keyCode.getName();
        return new KeyEvent(eventType, keyChar, keyText, keyCode, keyState.isShift(), keyState.isControl(),
                keyState.isAlt(), keyState.isMeta());
    }

    private EventTarget getEventTarget(Scene scene)
    {
        return scene.getFocusOwner() != null ? scene.getFocusOwner() : scene;
    }

    private KeyCode charToKey(char character)
    {
        switch (character)
        {
            case '\n':
                return KeyCode.ENTER;
            case '\t':
                return KeyCode.TAB;
            default:
                return KeyCode.UNDEFINED;
        }
    }

    private class KeyState
    {
        private boolean shift;
        private boolean alt;
        private boolean ctrl;
        private boolean meta;

        KeyState()
        {
            this.shift = false;
            this.alt = false;
            this.ctrl = false;
            this.meta = false;
        }

        boolean isShift()
        {
            return shift;
        }

        boolean isAlt()
        {
            return alt;
        }

        boolean isControl()
        {
            return ctrl;
        }

        boolean isMeta()
        {
            return meta;
        }

        void changeState(EventType<KeyEvent> eventType, KeyCode keyCode)
        {
            switch (keyCode)
            {
                case SHIFT:
                    shift = modifyState(shift, eventType);
                    break;
                case ALT:
                    alt = modifyState(alt, eventType);
                    break;
                case CONTROL:
                    ctrl = modifyState(ctrl, eventType);
                    break;
                case META:
                    meta = modifyState(meta, eventType);
                    break;
                default:
                    break;
            }
        }

        private boolean modifyState(boolean state, EventType<KeyEvent> event)
        {
            if (event == KeyEvent.KEY_PRESSED)
                return true;
            else if (event == KeyEvent.KEY_RELEASED)
                return false;

            return state;
        }
    }

    private class AwtRobot
    {
        private Robot robot;

        AwtRobot()
        {
            this.robot = null;
        }

        Robot get()
        {
            if (robot == null)
                robot = createRobot();

            return robot;
        }

        private Robot createRobot()
        {
            if (GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance())
                throw new WebDriverException("This driver require desktop environment with UI and mouse support");

            // initialize toolkit
            Toolkit.getDefaultToolkit();

            try
            {
                return new Robot();
            }
            catch (AWTException e)
            {
                throw new WebDriverException("Unable to create AWT robot: " + e.getMessage(), e);
            }
        }
    }
}
