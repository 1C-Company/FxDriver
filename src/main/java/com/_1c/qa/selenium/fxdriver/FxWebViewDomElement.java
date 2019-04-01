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

import java.util.List;
import java.util.concurrent.Callable;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.w3c.dom.html.HTMLElement;

import javafx.geometry.Bounds;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import com._1c.qa.selenium.fxdriver.robot.IFxRobot;

public class FxWebViewDomElement extends FxElement
{
    private final WebView webView;
    private final HTMLElement element;
    private final Coordinates coordinates;

    FxWebViewDomElement(WebView webView, HTMLElement element, IFxRobot robot)
    {
        super(webView, robot);
        this.webView = webView;
        this.element = element;
        this.coordinates = createCoordinate();
    }

    @Override
    public void click()
    {
        String js = String.format("document.getElementById('%s').click();", element.getId());
        NodeUtils.execute(() -> webView.getEngine().executeScript(js));
    }

    @Override
    public void submit()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendKeys(CharSequence... keysToSend)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTagName()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAttribute(String name)
    {
        return null;
    }

    @Override
    public boolean isSelected()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEnabled()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getText()
    {
        return element.getTextContent();
    }

    @Override
    public List<WebElement> findElements(By by)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebElement findElement(By by)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDisplayed()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Coordinates getCoordinates()
    {
        return coordinates;
    }

    @Override
    public Point getLocation()
    {
        Rectangle rectangle = getRect();
        return rectangle.getPoint();
    }

    @Override
    public Dimension getSize()
    {
        return getRect().getDimension();
    }

    @Override
    public Rectangle getRect()
    {
        return getBoundingClientRect();
    }


    @Override
    public String getCssValue(String propertyName)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException
    {
        return null;
    }

    private Rectangle getBoundingClientRect()
    {
        return NodeUtils.execute(new Callable<Rectangle>()
        {
            @Override
            public Rectangle call() throws Exception
            {
                String rectJs = new StringBuilder()
                        .append(String.format("var element = document.getElementById('%s');", element.getId()))
                        .append('\n').append("element.getBoundingClientRect();")
                        .toString();

                JSObject pointJs = (JSObject)webView.getEngine().executeScript(rectJs);

                double top = Double.valueOf(pointJs.getMember("top").toString());
                double left = Double.valueOf(pointJs.getMember("left").toString());
                double right = Double.valueOf(pointJs.getMember("right").toString());
                double bottom = Double.valueOf(pointJs.getMember("bottom").toString());

                return new Rectangle((int)left, (int)top, (int)(bottom - top), (int)(right - left));
            }
        });
    }

    private Coordinates createCoordinate()
    {
        return new Coordinates()
        {
            public Point onScreen()
            {
                Rectangle rect = getBoundingClientRect();

                Bounds bounds = node.getBoundsInLocal();
                Bounds screenBounds = node.localToScreen(bounds);

                // indent for webView + indent for web element relative to webView + middle of element
                return new Point((int)(screenBounds.getMinX() + rect.getX() + rect.getWidth() / 2.0),
                        (int)(screenBounds.getMinY() + rect.getY() + rect.getHeight() / 2.0));
            }

            public Point inViewPort()
            {
                Bounds bounds = node.getBoundsInLocal();
                Bounds sceneBounds = node.localToScene(bounds);

                Rectangle rect = getBoundingClientRect();

                return new Point((int)sceneBounds.getMinX() + rect.getX(), (int)sceneBounds.getMinY() + rect.getY());
            }

            public Point onPage()
            {
                Bounds bounds = node.getBoundsInLocal();
                Bounds sceneBounds = node.localToScene(bounds);

                Rectangle rect = getBoundingClientRect();

                return new Point((int)sceneBounds.getMinX() + rect.getX(), (int)sceneBounds.getMinY() + rect.getY());
            }

            public Object getAuxiliary()
            {
                return node;
            }
        };
    }
}
