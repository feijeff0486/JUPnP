package org.fourthline.cling.android;

import org.seamless.xml.SAXParser;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParserFactory;

/**
 * <p>
 *
 * @author Jeff
 * @date 2020/8/21
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class AndroidUPnPSAXParser extends SAXParser {
    @Override
    protected XMLReader create() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();

            // Configure factory to prevent XXE attacks
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            //commenting
            //factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            //factory.setXIncludeAware(false);

            //factory.setNamespaceAware(true);

            if (getSchemaSources() != null) {
                factory.setSchema(createSchema(getSchemaSources()));
            }

            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            xmlReader.setErrorHandler(getErrorHandler());
            return xmlReader;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}