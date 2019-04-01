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

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriverException;

import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

public class NodeUtils
{
    private static String TOOLTIP_PROP_KEY = "javafx.scene.control.Tooltip";

    public static int getCurrentProcessId() throws NumberFormatException
    {
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');

        return Integer.parseInt(jvmName.substring(0, index));
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    public static List<Window> listWindows()
    {
        try
        {
            try
            {
                return (ObservableList<Window>)Window.class.getMethod("getWindows")
                        .invoke(null);
            }
            catch (NoSuchMethodException e)
            {
                // fallback to java 8
                Iterator<Window> it = (Iterator<Window>)Window.class.getMethod("impl_getWindows")
                        .invoke(null);
                List<Window> windows = new ArrayList<>();
                while(it.hasNext())
                    windows.add(it.next());
                return windows;
            }
        }
        catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
        {
            throw new WebDriverException(e);
        }
    }

    public static Window getTargetWindow()
    {
        List<Window> windows = listWindows();

        if (windows.isEmpty())
            throw new WebDriverException("There are no windows on the screen");

        Optional<Window> window = windows.stream().filter(Window::isFocused).findAny();

        return window.orElseGet(() -> windows.get(0));
    }

    public static Point2D getNodePoint(Node node)
    {
        Bounds bounds = node.localToScreen(node.getBoundsInLocal());

        return new Point2D(
                bounds.getMinX() + bounds.getWidth() / 2.0,
                bounds.getMinY() + bounds.getHeight() / 2.0);
    }

    public static Map<String, Supplier<Object>> listProperties(Node node)
    {
        Map<String, Supplier<Object>> properties = new HashMap<>();

        for (Method method : Node.class.getMethods())
        {
            if (method.getName().endsWith("Property"))
            {
                String name = StringUtils.remove(method.getName(), "Property");
                Supplier<Object> supplier = () -> {
                    try
                    {
                        return ((ObservableValue) method.invoke(node)).getValue();
                    }
                    catch (IllegalAccessException | InvocationTargetException e)
                    {
                        // Returns empty string if something goes wrong.
                        // This corresponds to Selenium getAttributes() behavior. This method never returns null.
                        return "";
                    }
                };

                properties.put(name, supplier);
            }
        }

        if (node instanceof ProgressBar)
            properties.put("progress", () -> {
                ProgressBar progressBar = (ProgressBar)node;

                double progress = progressBar.getProgress();

                if (progress < 0)
                    return "-1";
                else if (progress > 1)
                    return "100";
                else
                    return String.valueOf((int)(progress * 100.0));
            });

        if (node instanceof CheckBox)
            properties.put("selected", ((CheckBox)node)::isSelected);

        properties.put("tooltip", () -> {
            if(node.hasProperties() && node.getProperties().containsKey(TOOLTIP_PROP_KEY))
            {
                String tooltip = ((Tooltip) node.getProperties().get(TOOLTIP_PROP_KEY)).getText();

                if (!StringUtils.isBlank(tooltip))
                    return tooltip;
            }

            if (node instanceof Control)
            {
                Tooltip tooltip = ((Control) node).getTooltip();

                if (tooltip != null)
                {
                    if (!StringUtils.isBlank(tooltip.getText()))
                        return tooltip.getText();
                }
            }

            return "";
        });

        return properties;
    }

    private static ScrollPane getScrollPane(Node node)
    {
        Parent parent = node.getParent();

        if (parent == null)
            return null;

        if (parent instanceof ScrollPane)
            return (ScrollPane)parent;

        return getScrollPane(parent);
    }

    private static Bounds getBoundsInScroll(Node node, Bounds local)
    {
        Parent parent = node.getParent();

        if (parent == null)
            return null;

        if (parent instanceof ScrollPane)
            return node.localToParent(local);

        return getBoundsInScroll(parent, node.localToParent(local));
    }

    public static Node scrollIntoView(Node node)
    {
        ScrollPane scrollPane = getScrollPane(node);

        if (scrollPane == null)
            return node;

        double width = scrollPane.getContent().getBoundsInLocal().getWidth();
        double height = scrollPane.getContent().getBoundsInLocal().getHeight();

        Bounds boundsInScroll = getBoundsInScroll(node, node.getBoundsInLocal());
        if (boundsInScroll != null)
        {
            double x = boundsInScroll.getMaxX();
            double y = boundsInScroll.getMaxY();

            scrollPane.setVvalue(y / height);
            scrollPane.setHvalue(x / width);
        }

        return node;
    }

    public static void execute(Runnable runnable)
    {
        try
        {
            FutureTask<Void> task = new FutureTask<>(runnable, null);

            Platform.runLater(task);

            task.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new WebDriverException(e);
        }
    }

    public static <T> T execute(Callable<T> callable)
    {
        try
        {
            FutureTask<T> task = new FutureTask<T>(callable);

            Platform.runLater(task);

            return task.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new WebDriverException(e);
        }
    }
}
