package HTTPGet;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.MalformedInputException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLGet {

	/* �Ώ�     :{view,res,mylist}
	        ����     :{newarrival,hourly,daily,weekly,monthly,total}
	         �J�e�S��:{all,music,ent,anime,game,animal,que,radio,sport,politics,chat,
		      science,history,cooking,nature,diary,dance,sing,play,lecture,owner,
			  tw,other,test,r18}
	   	       ��:http://www.nicovideo.jp/ranking/�Ώ�/����/�J�e�S��?rss=2.0
	   	  �Q�l:URL:http://nicowiki.com/?RSS%E3%83%95%E3%82%A3%E3%83%BC%E3%83%89%E4%B8%80%E8%A6%A7
	 */

	public static void main(String[] args) throws MalformedInputException,
	ProtocolException, IOException {

		URL url = new URL("http://www.nicovideo.jp/ranking/mylist/hourly/all?rss=2.0");
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		urlConn.connect();

		Document doc = null;
		try {
			doc = getDocument(urlConn.getInputStream());
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		FileWriter f=new FileWriter("C:\\test\\test.txt");
		
		// ���[�g�̗v�f���ɂȂ��Ă���q�m�[�h���擾����
		Element root = doc.getDocumentElement();
		System.out.println("���[�g�v�f���F" + root.getTagName());
		f.write("���[�g�v�f���F" + root.getTagName()+"\n");
		
		// �e�m�[�h���X�g���擾
		NodeList nodeList = root.getElementsByTagName("channel");
		System.out.println("�m�[�h���X�g�̐��́F" + nodeList.getLength());
		f.write("�m�[�h���X�g�̐��́F" + nodeList.getLength()+"\n");
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element)nodeList.item(i);
			System.out.println(getChildren(element, "title"));
			System.out.println(getChildren(element, "description"));
			System.out.println(getChildren(element, "pubDate"));
			f.write(getChildren(element, "title")+"\r\n");			
			f.write(getChildren(element, "description")+"\r\n");
			f.write(getChildren(element, "pubDate")+"\r\n");
			
			// �e�m�[�h���X�g���擾
			NodeList list = element.getElementsByTagName("item");
			System.out.println("���X�g�̐��́F" + nodeList.getLength());
			f.write("���X�g�̐��́F" + nodeList.getLength()+"\r\n");
			
			for (int j = 0; j< list.getLength(); j++) {
				Element element2 = (Element)list.item(j);
				System.out.println(getChildren(element2, "title"));
				System.out.println("Link�F" + getChildren(element2, "link"));
				//System.out.println("description�F" + getChildren(element2, "description"));
				System.out.println(getChildren(element2, "pubDate"));
				f.write(getChildren(element2, "title")+"\r\n");
				f.write("���X�g�̐��́F" + nodeList.getLength()+"\r\n");
				//f.write("description�F" + getChildren(element2, "description")+"\n");
				f.write(getChildren(element2, "pubDate")+"\r\n");
				
			}
		}
		urlConn.disconnect();
		f.close();
	}

	private static Document getDocument(InputStream in) throws SAXException,
	IOException, ParserConfigurationException {
		DocumentBuilder docbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return docbuilder.parse(in);
	}

	/**
	 * �w�肳�ꂽ�G�������g����q�v�f�̓��e���擾�B
	 *
	 * @param   element �w��G�������g
	 * @param   tagName �w��^�O��
	 * @return  �擾�������e
	 */
	public static String getChildren(Element element, String tagName) {
		NodeList list = element.getElementsByTagName(tagName);
		Element cElement = (Element)list.item(0);
		return cElement.getFirstChild().getNodeValue();
	}
}