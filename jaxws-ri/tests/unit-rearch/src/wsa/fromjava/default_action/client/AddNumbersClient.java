/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2006-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package wsa.fromjava.default_action.client;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;
import org.xml.sax.InputSource;

/**
 * @author Rama Pulavarthi
 */
public class AddNumbersClient extends TestCase {

    public AddNumbersClient(String name) {
        super(name);
    }

    private String evaluateXpath(String expr) throws XPathExpressionException, IOException {
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new NSContextImpl());
        URL res = AddNumbersClient.class.getResource("/AddNumbers.wsdl");
        System.out.println("res = " + res);
        InputStream stream = AddNumbersClient.class.getResourceAsStream("/AddNumbers.wsdl");
        return xpath.evaluate(expr, new InputSource(stream));
    }

    private void checkMessageAction(String method, String msg, String expected) throws XPathExpressionException, IOException {
        String expr = "/*[name()='definitions']/*[name()='portType']/*[name()='operation'][@name='" + method + "']/*[name()='" + msg + "']/@wsaw:Action";
        Object result = evaluateXpath(expr);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    //No @Action, so default action must be generated
    public void testaddNumbersNoAction() throws Exception {
        checkMessageAction("addNumbersNoAction", "input", "http://foobar.org/AddNumbers/addNumbersNoActionRequest");
    }

    //@Action(input=""), so default action must be generated
    public void testaddNumbersEmptyAction() throws Exception {
        checkMessageAction("addNumbersEmptyAction", "input", "http://foobar.org/AddNumbers/addNumbersEmptyActionRequest");
    }

    //@WebMethod(action="..."), so action must equal explicit value.
    public void testaddNumbers() throws Exception {
        checkMessageAction("addNumbers", "input", "http://example.com/input");
    }

    //explicit @Action, so action must equal explicit value.
    public void testaddNumbers2() throws Exception {
        checkMessageAction("addNumbers2", "input", "http://example.com/input2");
    }

    //@WebMethod(action=""), since empty String generate default action.
    public void testaddNumbers3() throws Exception {
        checkMessageAction("addNumbers3", "input", "http://foobar.org/AddNumbers/addNumbers3Request");
    }

    //@Oneway with no @Action, so generate default action for oneway input message
    public void testonewayNumbers() throws Exception {
        checkMessageAction("onewayNumbers", "input", "http://foobar.org/AddNumbers/onewayNumbers");
    }

}
