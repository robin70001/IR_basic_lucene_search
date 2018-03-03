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

public class SearchEngine {

	private IndexWriter indexWriter;
	private FSDirectory indexDir;
	String resultPath;
	static String[] fields = { "content", "title" };

	public static void indSearcher(String indP, String queryPath, int valAnalyzer, Boolean iwcSim) throws IOException, ParseException {
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

		//Similarity name choosing
		 	String Similarity_name;
		if (iwcSim == true) {
			Similarity_name = "BM25";
		}else {
			Similarity_name = "VSM";
		}
		
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
			
			String queryString = ops5[1].trim().replaceAll("[-+.^:,?#~@£%*]|\\n", ""); 

			// Query query = parser.parse("content:distance");
			Query query = parser.parse(queryString);

			int TOP = 1000;
			ScoreDoc[] hits = isearcher.search(query, TOP).scoreDocs;

			for(ScoreDoc a:hits) {
				Document hitDoc = isearcher.doc(a.doc);
				 float hitscore = a.score ;
				 String all_score = (count +" Q0 " +  hitDoc.get("id") + " " + 0 + " " + hitscore  +" IR_SEARCHER");

				 if (isFirstLine) { 
					isFirstLine = false;
				}else {
					all_score = "\n" + all_score;
				}
				outp1.print(all_score);
			}
		}		
		reader.close();
		directory.close();
		outp1.close();
		sc1.close();
		System.out.println("---- Search Completed ----");
		System.out.println("---- Search Query Result file Generated ----");
	}
}
