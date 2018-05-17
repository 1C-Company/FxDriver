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

import com._1c.qa.selenium.fxdriver.robot.IFxRobot;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.internal.Locatable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com._1c.qa.selenium.fxdriver.KeysCouple.convertToSeleniumKeys;

public class FxElement implements WebElement, Locatable
{
    private Node node;
    private IFxRobot robot;
    private FxSearchContext context;

    public FxElement(Node node, IFxRobot robot)
    {
        this.node = node;
        this.robot = robot;
        this.context = new FxSearchContext(robot, node);
    }

    @Override
    public String toString()
    {
        return node.toString();
    }

    @Override
    public void click()
    {
        NodeUtils.scrollIntoView(node);

        this.robot.click(node, PointerInput.MouseButton.LEFT);
    }

    @Override
    public void submit()
    {
        throw new UnsupportedOperationException("JavaFX application does not support web forms");
    }

    @Override
    public void sendKeys(CharSequence... keysToSend)
    {
        if (node instanceof TextInputControl)
        {
            NodeUtils.scrollIntoView(node);
            robot.click(node);

            for (CharSequence sequence : keysToSend)
            {
                StringBuilder letters = new StringBuilder();
                StringBuilder keys = new StringBuilder();

                for (int i = 0; i < sequence.length(); i++)
                {
                    Character c = sequence.charAt(i);

                    if (Character.isLetterOrDigit(c) ||
                            Character.isSpaceChar(c) ||
                            Character.getType(c) == Character.CONNECTOR_PUNCTUATION ||
                            Character.getType(c) == Character.OTHER_PUNCTUATION)
                        letters.append(c);
                    else
                        keys.append(c);
                }

                robot.push(convertToSeleniumKeys(keys));
                robot.type(letters.toString());
            }
        }
    }

    @Override
    public void clear()
    {
        if (node instanceof TextInputControl)
        {
            String text = ((TextInputControl)node).getText();
            int length = text == null ? 0 : text.length();

            if (length > 0)
                robot.click(node).push(Keys.END).eraseText(length);
        }
    }

    @Override
    public String getTagName()
    {
        return node.getTypeSelector();
    }

    @Override
    public String getAttribute(String name)
    {
        Map<String, Supplier<Object>> properties = NodeUtils.listProperties(node);

        return properties.getOrDefault(name, String::new).get().toString();
    }

    @Override
    public boolean isSelected()
    {
        return false;
    }

    @Override
    public boolean isEnabled()
    {
        return !node.isDisabled();
    }

    @Override
    public String getText()
    {
        return getText(node);
    }

    private String getText(VBox vbox)
    {
        StringBuilder sb = new StringBuilder();

        vbox.getChildren().stream()
                .map(this::getText)
                .forEach(sb::append);

        return sb.toString();
    }

    private String getText(TextFlow textFlow)
    {
        StringBuilder sb = new StringBuilder();
        textFlow.getChildren().stream()
                .map(this::getText)
                .forEach(sb::append);

        return sb.toString();
    }

    private String getText(Text text)
    {
        return text.getText();
    }

    private String getText(TextInputControl text)
    {
        return text.getText();
    }

    private String getText(Labeled text)
    {
        return text.getText();
    }

    private String getText(Node node)
    {
        if (node.getClass().getName().contains("MenuItemContainer"))
        {
            try
            {
                Object item = node.getClass().getMethod("getItem").invoke(node);

                if (item instanceof CustomMenuItem)
                    return getText(((CustomMenuItem)item).getContent());

                if (item instanceof MenuItem)
                    return ((MenuItem)item).getText();
            }
            catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
            {
                throw new WebDriverException(e);
            }
        }

        if (node instanceof VBox)
            return getText((VBox)node);

        if (node instanceof TextFlow)
            return getText((TextFlow)node);

        if (node instanceof Text)
            return getText((Text)node);

        if (node instanceof TextInputControl)
            return getText((TextInputControl)node);

        if (node instanceof Labeled)
            return getText((Labeled)node);

        return "";
    }

    @Override
    public List<WebElement> findElements(By by)
    {
        return by.findElements(context);
    }

    @Override
    public WebElement findElement(By by)
    {
        return by.findElement(context);
    }

    @Override
    public boolean isDisplayed()
    {
        return node.isVisible();
    }

    @Override
    public Point getLocation()
    {
        Bounds bounds = node.getBoundsInLocal();
        Bounds screenBounds = node.localToScreen(bounds);

        return new Point((int)screenBounds.getMinX(),(int)screenBounds.getMinY());
    }

    @Override
    public Dimension getSize()
    {
        int width = (int)node.getBoundsInLocal().getWidth();
        int height = (int)node.getBoundsInLocal().getHeight();

        return new Dimension(width, height);
    }

    @Override
    public Rectangle getRect()
    {
        Bounds bounds = node.getBoundsInLocal();
        Bounds screenBounds = node.localToScreen(bounds);

        return new Rectangle((int)screenBounds.getMinX(),
                (int)screenBounds.getMinY(),
                (int)screenBounds.getWidth(),
                (int)screenBounds.getHeight());
    }

    @Override
    public String getCssValue(String propertyName)
    {
        return null;
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException
    {
        return null;
    }

    @Override
    public Coordinates getCoordinates()
    {
        return new Coordinates() {

            public Point onScreen()
            {
                Bounds bounds = node.getBoundsInLocal();
                Bounds screenBounds = node.localToScreen(bounds);

                return new Point((int)(screenBounds.getMinX() + screenBounds.getWidth() / 2), (int)(screenBounds.getMinY() + screenBounds.getHeight() / 2));
            }

            public Point inViewPort()
            {
                Bounds bounds = node.getBoundsInLocal();
                Bounds sceneBounds = node.localToScene(bounds);

                return new Point((int)sceneBounds.getMinX(),(int)sceneBounds.getMinY());
            }

            public Point onPage()
            {
                Bounds bounds = node.getBoundsInLocal();
                Bounds sceneBounds = node.localToScene(bounds);

                return new Point((int)sceneBounds.getMinX(),(int)sceneBounds.getMinY());
            }

            public Object getAuxiliary()
            {
                return node;
            }
        };
    }
}
