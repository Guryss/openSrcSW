package opensource;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MidTerm {

	
	private String input_file;
	String query;  //사용자에게 질의받은 쿼리
	
	public MidTerm(String file, String query) {
		this.input_file = file;
		this.query = query;
	}
	
	//collection.xml에서 title,body tag의 내용을 저장한 2차원 배열 리턴하는 함수
	public String[][] getTitleBody() throws ParserConfigurationException, SAXException, IOException {
		String file = "";
		file = "src/data/collection.xml";
		String [][] docTitleBody = new  String[5][2];
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		
		Element root = doc.getDocumentElement();
		String result = "";
		
		NodeList nodeChild = root.getChildNodes();
		int docId = 0;
		
		for(int i=0; i<nodeChild.getLength(); i++) {
			Node node = nodeChild.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element ele = (Element)node;
				String nodeName = ele.getNodeName(); //doc
				NodeList nodeChild2 = ele.getChildNodes();
				
				for(int j=0; j<nodeChild2.getLength(); j++) {
					Node nNode = nodeChild2.item(j);
					
					if(nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element ele2 = (Element)nNode;
						String nodeName2 = ele2.getNodeName();  //title, body
						
						if(nodeName2.equals("title")) {
							docTitleBody[docId][0]=ele.getTextContent();
						}
						else if(nodeName2.equals("body")) {
							result = ele2.getTextContent();
							docTitleBody[docId][1] = result;
							docId++;
						}
						
					}
					else {
						continue;
					}
				}
			}
		}
		return docTitleBody;
	}
	//body tag의 내용을 문장 단위로 끊어서 저장한 2차원 배열 리턴하는 함수
	public String[][] splitBody() throws ParserConfigurationException, SAXException, IOException { 
		String[][] temp = getTitleBody();
		String[][] split = new String[5][];
		String[][] result = new String[5][];
		
		for(int i=0; i<temp.length; i++) {
			//body tag 속 내용을 한문장씩 끊어서 저장.
			split[i] = temp[i][1].split("."); 
		}
		for (int i=0; i<temp.length; i++) {
			result[i][0] = temp[i][0];  //title 복사
			for (int j=0; j<temp[i].length; j++) {
				result[i][j] = split[i][j];  //body 복사 
			}
		}
		return result;
	}
	
	//타이틀, 스닛펫, 매칭점수를 문서아이디가 앞선 문서부터 출력하는 함수
	public void showSnippet(String query) {
		//각 문서의 타이틀
		String title = "";
		//질의어에 포함된 키워드를 가장 많이 포함하는 30음절 
		String snippet="";
		//스닛펫에 포함된 키워드의 수
		int matchingScore = 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
