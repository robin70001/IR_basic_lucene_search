package grp1.art1;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilterFactory;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

//import static org.apache.lucene.util.Version.LATEST;

public class SearchEngine {

	private IndexWriter indexWriter;
	private FSDirectory indexDir;
	String resultPath;
	static String[] fields = { "content", "title" };

	// public static void main(String[] args) throws IOException, ParseException {
	public static void indSearcher(String indP, String queryPath, int valAnalyzer, Boolean iwcSim) throws IOException, ParseException {
		// String indexPath = "C:\\Users\\robin\\Desktop\\TCD\\4 IR & web
		// search\\cran\\index\\";
		String indexPath = indP;

		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
		IndexSearcher isearcher = new IndexSearcher(reader);

		Directory directory = FSDirectory.open(Paths.get(indexPath));

		String Analyzer_name = null;
		
		// Analyzer changing
		Analyzer analyzer = null;
		MultiFieldQueryParser parser = null;
		if (valAnalyzer == 1) {
			analyzer = new StandardAnalyzer();//StandardAnalyzer.ENGLISH_STOP_WORDS_SET);
			parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
			Analyzer_name = "StdAnalyzer";
		} else if (valAnalyzer == 2) {
			analyzer = new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
			parser = new MultiFieldQueryParser(fields, new EnglishAnalyzer());
			Analyzer_name = "EngAnalyzer";
		} else if (valAnalyzer == 3) {
			analyzer = new SimpleAnalyzer();
			parser = new MultiFieldQueryParser(fields, new SimpleAnalyzer());
			Analyzer_name = "SimpleAnalyzer";
		}
		
		//QueryParser parser1 = new QueryParser("content", new StandardAnalyzer());
		/*
		MultiFieldQueryParser parser = null;
		Analyzer analyzer = CustomAnalyzer.builder().withTokenizer(StandardTokenizerFactory.class).addTokenFilter(StandardFilterFactory.class).addTokenFilter(LowerCaseFilterFactory.class).addTokenFilter(PorterStemFilterFactory.class).build();
		parser = new MultiFieldQueryParser(fields, analyzer);
		*/		
		
		// Analyzer analyzer = new StandardAnalyzer();
		// MultiFieldQueryParser parser = new MultiFieldQueryParser(fields,new
		// StandardAnalyzer());

		 	String Similarity_name;
		if (iwcSim == true) {
			Similarity_name = "BM25";
		}else {
			Similarity_name = "VSM";
		}
		
		//String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		//String file_score_path = indP + "score\\score_" + Analyzer_name + "_" + timeStamp + ".txt";
		String file_score_path = indP + "score/score_" + Analyzer_name + "_" + Similarity_name + ".txt";
		
	

		Scanner sc1 = new Scanner(new File(queryPath));
		sc1.useDelimiter(".I");
		int j = 1;

		PrintWriter outp1 = new PrintWriter(file_score_path);
		boolean isFirstLine = true;
		int count =0;
		while (sc1.hasNext()) {
			count = count +1;
			String chunk_query = sc1.next();
			//System.out.println(chunk_query);
			String[] ops5 = chunk_query.split(".W");
			
			String queryString = ops5[1].trim().replaceAll("[-+.^:,?#~@£%*]|\\n", ""); // maybe this
			//System.out.println("queryString \n" + queryString);			 

//			String Queryp1 = serTitle;
//			String Queryp2 = to_search;
//			String fullQuery = Queryp1 + ":" + Queryp2;

			// Query query = parser.parse("content:distance");
			Query query = parser.parse(queryString);
//			Query query = parser1.parse(queryString);

			int TOP = 1000;
			ScoreDoc[] hits = isearcher.search(query, TOP).scoreDocs;
			// System.out.println("Documents: " + hits.length);
			// System.out.println(hits[0]);
			//System.out.println(count);
			// System.out.println(isearcher.doc(hits[0].doc));
			//for (int i = 0; i < hits.length; i++) {
			for(ScoreDoc a:hits) {
			//	System.out.println("...."+a.doc+"...."+a.score);
//			
				Document hitDoc = isearcher.doc(a.doc);
//				// System.out.println(i+1 + ") " + hitDoc.get("path") + ";"+ hits[i].doc + ";" +
//				// " " + hits[i].score);
//				
//				//double random = 1 + Math.random() * (10);
//				
//				String val = hitDoc.get("id").trim().replaceAll("\n","");
//				System.out.println(".................."+val);
//				 
//				 int hitid = Integer.parseInt(hitDoc.get("id").trim().replaceAll("\n","")) + 1;
				 float hitscore = a.score ;
//			
////				System.out.println(hitDoc.get("id").trim().replaceAll("\n",""));
//				// changed Q0 from J ; n - rank 
//				//String all_score = (j + " " + "Q0"  + " " + hitDoc.get("id").trim().replaceAll("\n","") + " " + (i) + " " + hits[i].score + " "+ "IR_SEARCHER");
//				String all_score = (count +" Q0 " + hitid + " " + 0 + " " + hitscore +" IR_SEARCHER");
				String all_score = (count +" Q0 " +  hitDoc.get("id") + " " + 0 + " " + hitscore  +" IR_SEARCHER");
				//String all_score = (count +" Q0 " +  a.doc + " " + 0 + " " + hitscore  +" IR_SEARCHER");
//				//String data_write_to_file = String.valueOf(all_score).concat(System.lineSeparator());
//				//out.write(data_write_to_file.getBytes());
//				//out.write(all_score.getBytes());
//
				if (isFirstLine) { 
					isFirstLine = false;
				}else {
					//System.out.println("inside");
					all_score = "\n" + all_score;
				}
				//-----------------------------Output printing-----------------------------
				outp1.print(all_score);
			}
			//j = j + 1;
		}

//-----------------------------------------------------------		
//		Query q1 = parser.parse("what are the structural and aeroelastic problems associated with flight of high speed aircraft .");
//		//Query query = parser.parse(queryString);
//		//outp1.print(score);
//		int TOP = 1000;
//		boolean isFirstLine = true;
//		PrintWriter outp1 = new PrintWriter(file_score_path);
//		ScoreDoc[] hits1 = isearcher.search(q1,TOP).scoreDocs;
//		for(ScoreDoc a:hits1) {
//			String score = a.doc + " "+ a.score;
//			if (isFirstLine) { 
//				isFirstLine = false;
//			}else {
//				//System.out.println("inside");
//				score = "\n" + score;
//			}
//			outp1.print(score);
//		}
		
		reader.close();
		directory.close();
		outp1.close();
		//out.close();
		sc1.close();
	}
}
