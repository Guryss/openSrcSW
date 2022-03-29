package opensource_week4;

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

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class makeKeyword {
	
	private String input_file;
	private String output_file = "/index.xml";
	
	public makeKeyword(String file) throws ParserConfigurationException, SAXException, IOException {
		this.input_file=file;
	}
		
	public String[][] parsingXML() throws SAXException, IOException, ParserConfigurationException{
	   String file = " ";
       file = "src/data/collection.xml";
		
		String [][] toIndex = new String[5][2];

		//xml parsing
		DocumentBuilderFactory  dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		
		Element root = doc.getDocumentElement();
		String result = " ";
		
		NodeList nodeChild = root.getChildNodes();
		int docId = 0;
		
		for(int i=0; i<nodeChild.getLength(); i++) {
			Node node = nodeChild.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				Element ele = (Element)node;
				String nodeName = ele.getNodeName();  //doc
				//System.out.println("nodeName: "+nodeName);
				NodeList nodeChild2 = ele.getChildNodes();
				
				for(int j=0; j<nodeChild2.getLength(); j++) {
					Node nNode = nodeChild2.item(j);
					
					if(nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element ele2 = (Element)nNode;
						String nodeName2 = ele2.getNodeName();  //title, body
						
						if(nodeName2.equals("title")) {
							toIndex[docId][0] = ele2.getTextContent();
						}
						else if(nodeName2.equals("body")) {
							result = ele2.getTextContent();
							
							KeywordExtractor ke = new KeywordExtractor();
							KeywordList kl = ke.extractKeyword(result, true);
							String indexresult="";
							
							for(int a=0; a<kl.size(); a++) {
								Keyword kwrd = kl.get(a);
								if(a !=kl.size()-1) {
									indexresult = indexresult+kwrd.getString()+":"+kwrd.getCnt()+"#";
								}
								else {
									indexresult = indexresult+kwrd.getString()+":"+kwrd.getCnt();
								}
							}
							toIndex[docId][1] = indexresult;
							docId++;
						}
						else {
							continue;
						}
						
						
					}
				}
			}
		}
		return toIndex;
		
	}
	

	public void convertXml() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		String [][] finalIndex = parsingXML();
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document docu = docBuilder.newDocument();
		
		Element docs = docu.createElement("docs");
		docu.appendChild(docs);
		
		for (int i=0; i<finalIndex.length; i++) {
			String finalTitle = finalIndex[i][0];
			String finalBody = finalIndex[i][1];

			Element docid = docu.createElement("doc");
			docs.appendChild(docid);
			docid.setAttribute("id", Integer.toString(i));
			
			Element title = docu.createElement("title");
			title.appendChild(docu.createTextNode(finalTitle));
			docid.appendChild(title);
			
			Element body = docu.createElement("body");
			body.appendChild(docu.createTextNode(finalBody));
			docid.appendChild(body);
			
	}
		
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		
		DOMSource source = new DOMSource(docu);
		StreamResult result = new StreamResult(new FileOutputStream(new File("src/data"+output_file)));
		
		transformer.transform(source, result);

		System.out.println("3주차 실행완료");
	}

}
