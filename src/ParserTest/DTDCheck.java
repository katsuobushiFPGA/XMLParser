package ParserTest;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.w3c.dom.Document;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.validation.*;
class TestHandler extends DefaultHandler {
  public TestHandler(){
    super();
  }
  @Override
  public  void error(SAXParseException e) throws SAXException {
    System.out.println(e);
  }
}
class Rei {
  private static SAXParser getSAXParserWDTD() throws Exception {
    final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
    parserFactory.setValidating(true);
    parserFactory.setNamespaceAware(true);
    return parserFactory.newSAXParser();
  }
  public static void main(String[] arg) throws Exception {
    final SAXParser parser = getSAXParserWDTD();
    URL url = new URL("http://www.nicovideo.jp/ranking/mylist/daily/all?rss=2.0");
	HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
	urlConn.setRequestMethod("GET");
	urlConn.connect();

	Document doc = null;
	try {
		doc = getDocumet(urlConn.getInputStream());
	} catch (SAXException e) {
		e.printStackTrace();
	} catch (ParserConfigurationException e) {
		e.printStackTrace();
	}
    parser.parse(urlConn.getInputStream(), new TestHandler());
  }
	private static Document getDocumet(InputStream is) throws SAXException,
	IOException, ParserConfigurationException {

		DocumentBuilder docbuilder = DocumentBuilderFactory.newInstance()
		.newDocumentBuilder();
		return docbuilder.parse(is);

	}
}