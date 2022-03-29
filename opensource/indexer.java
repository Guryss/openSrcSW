package opensource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class indexer {
	
	//키워드와 가중치 저장하는 리스트
	private ArrayList <ArrayList<String>> KeywordTF = new ArrayList();
	//이름과 빈도수 저장하는 리스트
	private ArrayList <ArrayList<String>> NameIDF = new ArrayList();
	String route;
	
	public indexer(String route) throws ParserConfigurationException, SAXException, IOException {
		this.route = route;
		splitBody();
	}
	
	//Body tag text
	public String[][] getBody() throws ParserConfigurationException, SAXException, IOException {
		
		String route = "";
		route = "src/data/index.xml";
		String[][] idBody = new String[5][2]; 
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(route);
		doc.getDocumentElement().normalize();
		
		Element root = doc.getDocumentElement();
		
		NodeList nChild = root.getChildNodes();
		int docId = 0;
		
		for(int i=0; i<nChild.getLength(); i++) {
			Node node = nChild.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				Element ele = (Element)node;
				String nName = ele.getNodeName();
				NodeList nChild2 = ele.getChildNodes();
				
				for(int j=0; j<nChild2.getLength(); j++) {
					Node nNode = nChild2.item(j);
					if(nNode.getNodeType()==Node.ELEMENT_NODE) {
						Element ele2 = (Element)nNode;
						String nName2 = ele2.getNodeName();
						
						if(nName2.equals("body")) {
							idBody[docId][0] = Integer.toString(docId);
							idBody[docId][1] = ele2.getTextContent();
							docId++;
						}
						else {
							continue;
						}
					}			
				}
			}
		}
		return idBody;
	}
	public ArrayList<String> calculateIDF(String keyword){
		ArrayList<String> TF = new ArrayList<String>();
		TF.add(keyword);
		int num = 0;
		int [] freq = new int[5];
		double resultTF;
		String realResult;
		
		for(int i=0; i<this.NameIDF.size(); i++) {
			if(this.NameIDF.get(i).contains(keyword)) {
				for(int j=0; j<this.NameIDF.get(i).size(); j++) {
					if(j%2==0) {
						if(this.NameIDF.get(i).get(j).equals(keyword)) {
							num++;
							freq[i] = Integer.parseInt(this.NameIDF.get(i).get(j+1));
						}
					}else
						continue;
				}
			}else
				continue;
		}
		
		for (int i=0; i<freq.length; i++) {
			if(num !=0) {
				resultTF = Math.round(freq[i]*Math.log((double)5/(double)num)*100)/100.0;
				realResult = i +","+resultTF;
				TF.add(realResult);
			}
		}

		return TF;
	}
	
	@SuppressWarnings({ "unchecked", "unlikely-arg-type" })
	public void splitBody() throws ParserConfigurationException, SAXException, IOException {
		String[][] temp = getBody();
		String[][] split = new String[5][];
		String[] result = new String[2];
		
		for(int i=0; i<temp.length; i++)
			split[i] = temp[i][1].split("#");
		
		for (int i=0; i<split.length; i++) {
			ArrayList data = new ArrayList<String>();
			
			for(int j=0; j<split[i].length; j++) {
				result = split[i][j].split(":");
				data.add(result[0]);
				data.add(result[1]);
			}
			this.NameIDF.add(data);
		}
		
		for (int i=0; i<NameIDF.size(); i++) {
			for(int j=0; j<NameIDF.get(i).size(); j++) {
				if(j%2==0) {
					if(!KeywordTF.contains(NameIDF.get(i).get(j))) {
						this.KeywordTF.add(calculateIDF(NameIDF.get(i).get(j)));
					}
					else
						continue;
				}
			}
		}
	
	}
	
	@SuppressWarnings("unchecked")
	public void makeHashMap() throws IOException {
		HashSet<ArrayList<String>> set = new HashSet<>(this.KeywordTF);
		ArrayList <ArrayList<String>> realKeywordTF = new ArrayList(set);
		
		FileOutputStream fStream = new FileOutputStream("src/data/index.post");
		ObjectOutputStream ooStream = new ObjectOutputStream(fStream);
		
		HashMap map = new HashMap();
		
		for(int i=0; i<realKeywordTF.size(); i++) {
			ArrayList<String> key = new ArrayList<>();
			for(int j =0; j<5; j++) {
				key.add(realKeywordTF.get(i).get(j+1));
			}
			map.put(realKeywordTF.get(i).get(0), key);
		}
		ooStream.writeObject(map);
		ooStream.close();
	}
	
	public void printMap(String route) throws IOException, ClassNotFoundException {
		FileInputStream fiStream = new FileInputStream(route);
		ObjectInputStream oiStream = new ObjectInputStream(fiStream);
		
		Object object = oiStream.readObject();
		oiStream.close();
		
		HashMap hashMap = (HashMap)object;
		Iterator<String> it = hashMap.keySet().iterator();
		
		while(it.hasNext()) {
			String key = it.next();
			ArrayList value = (ArrayList)hashMap.get(key);
			System.out.println(key+"->"+value);
		}
	}

}
