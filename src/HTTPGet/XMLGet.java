package HTTPGet;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLGet {

	/* 対象     :{view,res,mylist}
	        期間     :{newarrival,hourly,daily,weekly,monthly,total}
	         カテゴリ:{all,music,ent,anime,game,animal,que,radio,sport,politics,chat,
		      science,history,cooking,nature,diary,dance,sing,play,lecture,owner,
			  tw,other,test,r18}
	   	       例:http://www.nicovideo.jp/ranking/対象/期間/カテゴリ?rss=2.0
	   	  参考:URL:http://nicowiki.com/?RSS%E3%83%95%E3%82%A3%E3%83%BC%E3%83%89%E4%B8%80%E8%A6%A7
	   	  URLのパラメータを変更するだけで別のものを取得できる。
	 */

	public static void main(String[] args) throws MalformedInputException,
	ProtocolException, IOException {

//URL url = new URL("http://www.nicovideo.jp/tag/?sort=f&rss=2.0");

		URL url = new URL("http://www.nicovideo.jp/ranking/mylist/daily/anime?rss=2.0");
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
		FileWriter f=new FileWriter("C:\\test\\test.csv");

		// ルートの要素名になっている子ノードを取得する
		Element root = doc.getDocumentElement();
//		f.write("ルート要素名：" + root.getTagName()+"\n");

		// 各ノードリストを取得する。
		NodeList nodeList = root.getElementsByTagName("channel");
//		f.write("ノードリストの数は：" + nodeList.getLength()+"\n");

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element)nodeList.item(i);
			f.write(getChildren(element, "title")+"\r\n");
			f.write(getChildren(element, "description")+"\r\n");
			f.write(getChildren(element, "pubDate")+"\r\n");

			// 各ノードリストを取得
			NodeList list = element.getElementsByTagName("item");


			for (int j = 0; j< list.getLength(); j++) {
				Element element2 = (Element)list.item(j);
				f.write(titleFormatter(getChildren(element2, "title"))+",");
				f.write(urlFormatter(getChildren(element2, "link")));
				//description
				f.write(vcmExtract(textFormatter(getChildren(element2, "description")+ ",")));
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
	 * 指定されたエレメントから子要素の内容を取得。
	 *
	 * @param   element 指定エレメント
	 * @param   tagName 指定タグ名
	 * @return  取得した内容
	 */
	public static String getChildren(Element element, String tagName) {
		NodeList list = element.getElementsByTagName(tagName);
		Element cElement = (Element)list.item(0);
		return cElement.getFirstChild().getNodeValue();
	}
	//再生、コメント、マイリストを抽出
	public static String vcmExtract(String t){
        //判定するパターンを生成
        Pattern p = Pattern.compile("(再生)+[0-9]+|(コメント)+[0-9]+|(マイリスト)+[0-9]+");
        Matcher m = p.matcher(t);
        StringBuilder stb = new StringBuilder();
        while(m.find()){
        	String s = m.group();
        	stb.append(s + ",");
        }
        return stb.toString();
	}

	public static String urlFormatter(String t){
		//参考:http://dic.nicovideo.jp/a/id ニコニコ大百科id
		//判定するパターンを生成
		//ニコニコチャンネルはsoである。
		//sm,nmなどの数字がない！
        Pattern p = Pattern.compile("[(so)|(sm)|(nm)]+[0-9]+");
        Matcher m = p.matcher(t);
        StringBuilder stb = new StringBuilder();
        while(m.find()){
        	String s = m.group();
        	stb.append(s + ",");
        }
        return stb.toString();
	}
	//[,]を削除する
	public static String titleFormatter(String str){
        return str.replaceAll(",","");
	}
	//	HTMLのタグ除去と「:」と「,」を削除する。
	public static String textFormatter(String str){
		str = str.replaceAll("<.+?>", "");
		return str.replaceAll("：|,", "");
	}
}