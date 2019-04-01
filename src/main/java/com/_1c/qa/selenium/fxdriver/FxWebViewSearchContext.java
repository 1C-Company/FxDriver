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

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByXPath;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLElement;

import javafx.scene.Node;
import javafx.scene.web.WebView;

import com._1c.qa.selenium.fxdriver.robot.IFxRobot;

import static javax.xml.xpath.XPathConstants.NODESET;

public class FxWebViewSearchContext extends FxSearchContext implements SearchContext, FindsByXPath
{
    FxWebViewSearchContext(IFxRobot robot, Node root)
    {
        super(robot, root);
    }

    @Override
    public WebElement findElementByXPath(String using)
    {
        List<WebElement> elements = findElementsByXPath(using);
        if (elements.isEmpty())
            throw new NoSuchElementException("Not found element for xpath '" + using + "'");
        return elements.get(0);
    }

    @Override
    public List<WebElement> findElementsByXPath(String xpath)
    {
        return NodeUtils.execute(() -> {
            XPath xPath = XPathFactory.newInstance().newXPath();
            WebView webView = (WebView)root;
            NodeList nodes = (NodeList)xPath.compile(xpath).evaluate(webView.getEngine().getDocument(), NODESET);
            List<WebElement> result = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); ++i)
            {
                result.add(new FxWebViewDomElement(webView, (HTMLElement)nodes.item(i), robot));
            }
            return result;
        });
    }
}
