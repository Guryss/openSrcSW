package opensource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class makeCollection {
	
private String output_file = "/collection.xml";
	
	public static File[] makeFileList(String path) {
		File dir = new File(path);
		return dir.listFiles();
	}
	
	public makeCollection(String path) throws IOException, ParserConfigurationException, TransformerException {
		
		path = "C:\\Users\\리림\\SimpleIR\\html";
		File[] files = makeFileList(path);
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document docs = docBuilder.newDocument();
		
		Element doc = docs.createElement("docs");
		docs.appendChild(doc);
		
		for (int i=0; i<files.length; i++) {
			org.jsoup.nodes.Document html = Jsoup.parse(files[i], "UTF-8");
			String titleData = html.title();
			String bodyData = html.body().text();
		
		Element docid = docs.createElement("doc");
		doc.appendChild(docid);
		
		//속성값 type
		docid.setAttribute("id", Integer.toString(i));
		
		Element title = docs.createElement("title");
		title.appendChild(docs.createTextNode(titleData));
		docid.appendChild(title);
		
		Element body = docs.createElement("body");
		body.appendChild(docs.createTextNode(bodyData));
		docid.appendChild(body);
	}
		
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		
		DOMSource source = new DOMSource(docs);
		StreamResult result = new StreamResult(new FileOutputStream(new File("src/data"+output_file)));
		
		transformer.transform(source, result);
		
		System.out.println("2주차 실행완료");
	}

}
