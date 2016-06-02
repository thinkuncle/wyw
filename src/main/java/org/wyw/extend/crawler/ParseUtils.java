/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wyw.extend.crawler;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.wyw.extend.crawler.pojo.Page;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class ParseUtils {

    public static final Logger logger = Logger.getLogger(ParseUtils.class);

    public static void parse(Page page) throws Exception {
        // parse the content
        ByteBuffer contentInOctets = page.getContent();
        InputSource input = new InputSource(new ByteArrayInputStream(contentInOctets.array(), 
                contentInOctets.arrayOffset()+contentInOctets.position(), contentInOctets.remaining()));
        String encoding = page.getEncoding();
        input.setEncoding(encoding);
        Node root = parseToDom(input);
        page.setRoot(root);

        // get meta directives
        HTMLMetaTags metaTags = new HTMLMetaTags();
        HTMLMetaProcessor.getMetaTags(metaTags, root, page.getUrl());
        logger.trace("Meta tags for " + page.getUrl() + ": " + metaTags.toString());
        page.setMetaTags(metaTags);
    }

    private static Node parseToDom(InputSource input) throws Exception {
        DOMFragmentParser parser = new DOMFragmentParser();
        try {
            parser.setFeature("http://cyberneko.org/html/features/augmentations", true);
            parser.setProperty("http://cyberneko.org/html/properties/default-encoding", "utf-8");
            parser.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset", true);
            parser.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content", false);
            parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
            parser.setFeature("http://cyberneko.org/html/features/report-errors", logger.isTraceEnabled());
        } catch (SAXException e) {
        }
        // convert Document to DocumentFragment
        HTMLDocumentImpl doc = new HTMLDocumentImpl();
        doc.setErrorChecking(false);
        DocumentFragment res = doc.createDocumentFragment();
        DocumentFragment frag = doc.createDocumentFragment();
        parser.parse(input, frag);
        res.appendChild(frag);

        while (true) {
            frag = doc.createDocumentFragment();
            parser.parse(input, frag);
            if (!frag.hasChildNodes())
                break;
            res.appendChild(frag);
        }
        return res;
    }

    public static void main(String[] args) throws Exception {
        URL url = new URL("http://www.qishuw.cc/top/allvisit_1");
        Page page = new Page(url);
        Fetcher fetchJob = new Fetcher();
        fetchJob.fetch(page);
        System.out.println(page.getResponseCode());
        ParseUtils parseJob = new ParseUtils();
        parseJob.parse(page);
//      Rank rank = new Rank();
//      rank.extract(page);
//      System.out.println(page.getResponseCode());
    }

}
