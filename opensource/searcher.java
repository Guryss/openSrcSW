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
	}
	
	public HashMap readPost() throws IOException, ClassNotFoundException {
		FileInputStream fileStream = new FileInputStream("src/data/index.post");
		ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);
		
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		
		HashMap hashMap = (HashMap)object;
		
		return hashMap;

	}
	
	public ArrayList<String> calcSim() throws ClassNotFoundException, IOException{
		
		KeywordExtractor extractor = new KeywordExtractor();
		KeywordList list = extractor.extractKeyword(this.query, true);
		ArrayList<String> qList = new ArrayList<>();
		
		for(int i=0; i<list.size(); i++) {
			Keyword keyword = list.get(i);
			String word = keyword.getString();
			qList.add(word);
		}
		//qList : query에 있는 단어를 추출한 리스트
		
		HashMap innerProduct = readPost();
		ArrayList<String> inner = new ArrayList<>();
		double wSum = 0;
		//나온 가중치들의 합을 저장하는 변수
		for(int i=0; i<5; i++) {
			for(int j=0; j<qList.size(); j++) {
				ArrayList<String> temp = (ArrayList<String>) innerProduct.get(qList.get(j));
				String [] split = temp.get(i).split(",");
				wSum = wSum+Double.parseDouble(split[1]);
			}
			inner.add(Double.toString(wSum));
			wSum = 0; //초기화
		}
		return inner;
	}
	
	public ArrayList<String> calcInner() throws ClassNotFoundException, IOException{
		KeywordExtractor ex = new KeywordExtractor();
		KeywordList kl = ex.extractKeyword(this.query, true);
		ArrayList<String> qList = new ArrayList<>();
		
		for(int i=0; i<kl.size(); i++) {
			Keyword keyword = kl.get(i);
			String word = keyword.getString();
			qList.add(word);
		}
		
		HashMap innerProduct = readPost();
		ArrayList<String> innerPro = new ArrayList<>();
		
		ArrayList<String> weight = new ArrayList<>();
		double wSum = 0;
		double tmp = 0;
		double tmpResult=0;
		for(int i=0; i<5; i++) {
			for(int j=0; j<qList.size(); j++) {
				ArrayList<String> temp = (ArrayList<String>) innerProduct.get(qList.get(j));
				String [] split = temp.get(i).split(",");
				wSum = wSum + Math.pow(Double.parseDouble(split[1]), 2.0);
			}
			tmp = Double.parseDouble(calcSim().get(i));
			wSum = Math.sqrt(wSum);
			tmpResult = tmp/(Math.sqrt((double)(qList.size()))*wSum);
			
			if(Double.isNaN(tmpResult)) {
				innerPro.add("0.0");
			}
			else
				innerPro.add(Double.toString(tmpResult));
			wSum=0;
		}
		return innerPro;
	}
		
	
	
	public void rank() throws ClassNotFoundException, IOException {
		ArrayList<String> result = calcInner();
		String[] rankResult = new String[3];
		rankResult[2] = " ";
		
		double num = 0;
		int ber=0;
		int j=0;
		int k=0;
		while(rankResult[2].equals(" ")) {
			for(int i=0; i<result.size(); i++) {
				if(num < Double.parseDouble(result.get(i))) {
					ber = i;
					num = Double.parseDouble(result.get(i));
				}
			}
			rankResult[k] = this.title[ber];
			result.set(ber, "-1");
			
			for(int i=0; i<result.size(); i++) {
				if(Double.parseDouble(result.get(i))==0) {
					ber = i;
					break;
				}
				else
					continue;
			}
			k++;
			num = 0;
		}
		for(int i=0; i<rankResult.length; i++)
			System.out.println((i+1)+"등: "+rankResult[i]);
	}
		
	
	
	
	
	
}
