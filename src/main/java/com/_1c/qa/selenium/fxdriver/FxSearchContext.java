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
import javafx.scene.Node;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.internal.FindsById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FxSearchContext implements SearchContext, FindsById, FindsByClassName, FindsByCssSelector
{
    private IFxRobot robot;
    private Node root;

    public FxSearchContext(IFxRobot robot)
    {
        this.robot = robot;
    }

    public FxSearchContext(IFxRobot robot, Node root)
    {
        this(robot);
        this.root = root;
    }

    private List<Node> getRoots()
    {
        if (root != null)
            return Arrays.asList(root);
        else
            return NodeUtils.listWindows().stream().map(w -> (Node)w.getScene().getRoot()).collect(Collectors.toList());
    }

    @Override
    public List<WebElement> findElements(By by)
    {
        return null;
    }

    @Override
    public WebElement findElement(By by)
    {
        return null;
    }

    @Override
    public WebElement findElementById(String id)
    {
        for (Node root : getRoots())
        {
            Node node = NodeUtils.execute(() -> root.lookup("#" + id));
            if (node != null)
                return new FxElement(node, robot);
        }

        throw new NoSuchElementException("Element with id '" + id + "' not found");
    }

    @Override
    public List<WebElement> findElementsById(String id)
    {
        List<WebElement> elements = new ArrayList<>();

        for (Node root : getRoots())
        {
            NodeUtils.execute(() -> root.lookupAll("#" + id).stream()
                    .map(n -> new FxElement(n, robot))
                    .forEach(elements::add));

        }

        return elements;
    }

    @Override
    public WebElement findElementByClassName(String className)
    {
        for (Node root : getRoots())
        {
            Node node = NodeUtils.execute(() -> root.lookup("." + className));

            if (node != null)
                return new FxElement(node, robot);
        }

        throw new NoSuchElementException("Element with class '" + className + "' not found");
    }

    @Override
    public List<WebElement> findElementsByClassName(String className)
    {
        List<WebElement> elements = new ArrayList<>();

        for (Node root : getRoots())
        {
            NodeUtils.execute(() -> root.lookupAll("." + className).stream()
                    .map(n -> new FxElement(n, robot))
                    .forEach(elements::add));
        }

        return elements;
    }

    @Override
    public WebElement findElementByCssSelector(String cssSelector)
    {
        for (Node root : getRoots())
        {
            Node node = NodeUtils.execute(() -> root.lookup(cssSelector));

            if (node != null)
                return new FxElement(node, robot);
        }

        throw new NoSuchElementException("Element with selector '" + cssSelector + "' not found");
    }

    @Override
    public List<WebElement> findElementsByCssSelector(String cssSelector)
    {
        List<WebElement> elements = new ArrayList<>();

        for (Node root : getRoots())
        {
            NodeUtils.execute(() -> root.lookupAll(cssSelector).stream()
                    .map(n -> new FxElement(n, robot))
                    .forEach(elements::add));
        }

        return elements;
    }
}
