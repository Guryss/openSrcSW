package opensource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.xml.sax.SAXException;

public class searcher {
	String[] title = new String[5];
	String route;
	String query;  //사용자에게 질의받는 쿼리
	
	public searcher(String file, String query) throws ClassNotFoundException, IOException, ParserConfigurationException, SAXException {
		
		this.route = file;
		this.query = query;
		
		makeKeyword mkwrd = new makeKeyword("src/data/collection.xml");
		for(int i=0; i<mkwrd.parsingXML().length; i++)
			this.title[i] = mkwrd.parsingXML()[i][0];
//		for (int i=0; i<title.length; i++)
//			System.out.println(title[i]);
	}
	
	public HashMap readPost() throws IOException, ClassNotFoundException {
		FileInputStream fileStream = new FileInputStream("src/data/index.post");
		ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);
		
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		
		HashMap hashMap = (HashMap)object;
		
		//post파일 해쉬맵 리턴
		return hashMap;

	}
	
	@SuppressWarnings("unchecked")
	public double[] calcSim() throws ClassNotFoundException, IOException{
		double finalResult = 0.0;
		int id = 0;
		KeywordExtractor extractor = new KeywordExtractor();
		KeywordList list = extractor.extractKeyword(this.query, true);
		ArrayList<String> qList = new ArrayList<>();
		
		for(int i=0; i<list.size(); i++) {
			Keyword keyword = list.get(i);
			String word = keyword.getString();
			qList.add(word);
		}
		
//		for(int i=0; i<qList.size(); i++) {
//			System.out.println(qList.get(i));
//		}
		
		//qList : query에 있는 단어를 추출한 리스트
		
		HashMap hashmap = readPost();
		double[] result = new double[qList.size()];
		
		for (int i=0; i<qList.size(); i++) {
			Iterator<String> it = hashmap.keySet().iterator();
			
			while(it.hasNext()) {
				String key = it.next();
				if(key.equals(qList.get(i))) {
					String value = (String) hashmap.get(key);
					double weight = split(id, value);
					if(weight !=-1) {
						result[i] = weight*Double.parseDouble(value);
						it.remove();
						break;
					}
				}
			}
		}
		
		for (int i=0; i<qList.size(); i++) {
			finalResult = finalResult+result[i];
			result[i] = finalResult;
		}
		
		return result;
		
	}
	
	public double split(int id, String value) {
		String[] temp = value.split(" ");
		for(int i=0; i<(temp.length)/2; i++) {
			if(id == Double.parseDouble(temp[i])) {
				return Double.parseDouble(temp[i+1]);
			}
				
		}
		return 0;
	}
	
	
	public void rank() throws ClassNotFoundException, IOException {
		double[] finalResult = calcSim();
		String[] rankResult = new String[3];
		double temp = 0.0;
		String tempTitle = "" ;
		for(int i=0; i<finalResult.length-1; i++) {
			for(int j=0; j<finalResult.length-1; j++) {
				if(finalResult[j] < finalResult[j+1]) {
					temp = finalResult[j];
					finalResult[j+1] = finalResult[j];
					tempTitle = title[j];
					title[j] = title[j+1];
					title[j+1] = tempTitle;
				}
			}
		}
		if(finalResult[0] == 0.0) {
			System.out.println("검색된 문서 결과가 없습니다.");
		}
		else {
			for(int i=0; i<3; i++) {
				if(finalResult[i] == 0.0)
					break;
				else {
					System.out.println(i+"등 : "+title[i]);
				}
			}
		}
		
	}
		

}
