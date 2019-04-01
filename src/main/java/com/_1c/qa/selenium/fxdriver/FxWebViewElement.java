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

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;

import javafx.scene.Node;
import javafx.scene.web.WebView;

import com._1c.qa.selenium.fxdriver.robot.IFxRobot;

import static javax.xml.xpath.XPathConstants.NODESET;

public class FxWebViewElement extends FxElement
{
    private WebView webView;

    FxWebViewElement(Node node, IFxRobot robot)
    {
        super(node, robot);
        webView = (WebView)node;
        setDocumentIds(webView);
    }

    @Override
    public String getText()
    {
        return NodeUtils.execute(() -> {
            try
            {
                TransformerFactory tFactory = TransformerFactory.newInstance();
                Transformer transformer = tFactory.newTransformer();

                DOMSource source = new DOMSource(webView.getEngine().getDocument());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                StreamResult result = new StreamResult(out);
                transformer.transform(source, result);
                return new String(out.toByteArray(), StandardCharsets.UTF_8);
            }
            catch (TransformerException e)
            {
                throw new IllegalStateException(e);
            }
        });
    }

    private void setDocumentIds(WebView webView)
    {
        NodeUtils.execute(() -> {
            try
            {
                XPath xPath = XPathFactory.newInstance().newXPath();
                NodeList nodes = (NodeList)xPath.compile("//*").evaluate(webView.getEngine().getDocument(), NODESET);
                for (int i = 0; i < nodes.getLength(); i++)
                {
                    org.w3c.dom.Node node = nodes.item(i);
                    org.w3c.dom.Node idNode = node.getAttributes().getNamedItem("id");
                    if (idNode == null)
                    {
                        Attr attr = webView.getEngine().getDocument().createAttribute("id");
                        attr.setValue(UUID.randomUUID().toString());
                        node.getAttributes().setNamedItem(attr);
                    }
                }
            }
            catch (XPathExpressionException e)
            {
                throw new IllegalStateException(e);
            }
        });
    }


}
