package grp1.art1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.BM25Similarity;

/**
 * Hello world!
 *
 */
public class App {
	static void cranIndex(String queryPath) throws IOException {

	}

	public static void main(String[] args) throws IOException, ParseException {
		Boolean iwcSim = true;
		int valAnalyzer = 1;
		String indP = null;
		String docsPath = null;
		String queryPath = null;
		String query_result_Path = null;

		// String usage = "java org.apache.lucene.demo.IndexFiles"
		// + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
		// + "This indexes the documents in DOCS_PATH, creating a Lucene index"
		// + "in INDEX_PATH that can be searched with SearchFiles";
		//
		String indexPath = null;
		String scorePath = null;
		String documentPath = null;
		String query_path = null;
		String qres_path = null;
		String cranPath = null;

		int count = args.length;
		if (count == 0) {
			System.out.println("No arguments given");
			System.exit(1);
		} else if (count == 1) {
			System.out.println("2nd argument is null");
			System.exit(1);
		} else if (count == 2) {
			System.out.println("3rd argument is null");
			System.exit(1);
		}
		// else if( count == 3){
		// System.out.println("2nd argument is null");
		// }
		else if (count > 3) {
			System.out.println("arguments exceeded");
			System.exit(1);
		}

		cranPath = args[0];

		if (cranPath.substring(cranPath.length() - 1).equals("/")) {
			indexPath = cranPath + "index/";
			scorePath = cranPath + "index/score/";
			documentPath = cranPath + "cran.all.1400";
			query_path = cranPath + "cran.qry";
			qres_path = cranPath + "cranqrel";
		} else {
			indexPath = cranPath + "/index//";
			scorePath = cranPath + "/index/score/";
			documentPath = cranPath + "/cran.all.1400";
			query_path = cranPath + "/cran.qry";
			qres_path = cranPath + "/cranqrel";
		}

		if (args[1].equals("true") || args[1].equals("True") || args[1].equals("TRUE")) {
			iwcSim = true;
		} else if (args[1].equals("false") || args[1].equals("False") || args[1].equals("FALSE")) {
			iwcSim = false;
		} else {
			System.out
					.println("2nd argument regarding similarity not given / incorrect,So default values will be used");

		}

		if (args[2].equals("1") || args[2].equals("2") || args[2].equals("3")) {
			valAnalyzer = Integer.parseInt(args[2]);
		} else {
			System.out.println("3rd argument regarding Analyzer not given / incorrect,So default values will be used");
		}

		Path path = Paths.get(cranPath);
		if (Files.notExists(path)) {
			System.out.println("no cran dir exists");
			System.exit(1);
		} else {
			// System.out.println("cran dir exists");
		}

		path = Paths.get(indexPath);
		if (Files.notExists(path)) {
			System.out.println("no dir exists");
			new File(indexPath).mkdirs();
		} else {
			// System.out.println("dir exists");
		}

		path = Paths.get(scorePath);
		if (Files.notExists(path)) {
			System.out.println("no dir exists");
			new File(scorePath).mkdirs();
		} else {
			// System.out.println("dir exists");
		}

		// indexPath = args[0];
		// documentPath = args[1];
		// query_path = args[2];
		// qres_path = args[3];
		// create = args[4];
		// numAnalyzer = args[5];

		// for (int i = 0; i < args.length; i++) {
		// if ("-index".equals(args[i])) {
		// indexPath = args[i + 1];
		// i++;
		// } else if ("-docs".equals(args[i])) {
		// documentPath = args[i + 1];
		// i++;
		// } else if ("-query".equals(args[i])) {
		// query_path = args[i + 1];
		// i++;
		// } else if ("-qres".equals(args[i])) {
		// qres_path = args[i + 1];
		// i++;
		// } else if ("-update".equals(args[i])) {
		// create = false;
		// }
		// }
		//

		// if (documentPath == null) {
		// System.err.println("dir error"); //"Usage: " + usage);
		// System.exit(1);
		// }
		//
		// final Path docDir = Paths.get(documentPath);
		// if (!Files.isReadable(docDir)) {
		// System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does
		// not exist or is not readable, please check the path");
		// System.exit(1);
		// }

		// System.out.println( "Hello World!" );

		// indP = "C:\\Users\\robin\\Desktop\\TCD\\4 IR & web search\\cran\\index\\";
		// docsPath = "C:\\Users\\robin\\Desktop\\TCD\\4 IR & web
		// search\\cran\\cran.all.1400";
		// queryPath = "C:\\Users\\robin\\Desktop\\TCD\\4 IR & web
		// search\\cran\\cran.qry";
		// query_result_Path = "C:\\Users\\robin\\Desktop\\TCD\\4 IR & web
		// search\\cran\\cranqrel";

		indP = indexPath;
		docsPath = documentPath;
		queryPath = query_path;
		query_result_Path = qres_path;

		IndexFiles.cranIndexer(indP, docsPath, iwcSim, valAnalyzer);
		SearchEngine.indSearcher(indP, queryPath, valAnalyzer, iwcSim);
		// System.out.println("Score File Writing completed");

		//String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		//String file_score_path = indP + "score\\qscores_" + timeStamp + ".txt";
		String file_score_path = indP + "score/original_scores.txt";
		
		PrintWriter outp1 = new PrintWriter(file_score_path);
		
		
		//FileOutputStream out = new FileOutputStream(file_score_path);
		 //BufferedWriter outWriter = new BufferedWriter(new OutputStreamWriter((file_score_path));
		List<String> lines = Files.readAllLines(new File(query_result_Path).toPath());
		// String p1 = lines.get(0);
		// String p2 = lines.get(1);
		int ij = 1;

		String all_score = null;
		String data_write_to_file = null;
		ArrayList<String> ar = null;
		ar = new ArrayList<String>();

		for (String line : lines) {
			// Do whatever you want
			// System.out.println("p" + ij + " = " + line);
			String[] op = line.split(" ");
			// System.out.println(op[0]);
			// System.out.println(op[1]);
			// System.out.println(op[2]);
			// System.out.println(op[3]);
			int num_rel_code = 0;
			int inverse_rel_num = 0;
			try {
				num_rel_code = Integer.parseInt(op[2]);
				inverse_rel_num = 5 - num_rel_code;
				// System.out.println(ij + " op[2] " + num_rel_code);
			} catch (RuntimeException e) {
				num_rel_code = Integer.parseInt(op[3]);
				inverse_rel_num = 5 - num_rel_code;
				// System.out.println(ij + " op[3] " + num_rel_code);
				continue;
			}

			// System.out.println(ij + ") " + op[0] +" " + op[1] + " " + num_rel_code + " "
			// + inverse_rel_num );
			data_write_to_file = (op[0] + " 0 " + op[1] + " " + inverse_rel_num);//.concat(System.lineSeparator());
			if (ij!=1) {
				data_write_to_file = "\n" + data_write_to_file;
			}
			//String s1 = data_write_to_file;
			
			outp1.print(data_write_to_file);
			
			//ar.add(s1);
			// out.write(data_write_to_file.getBytes());

			ij++;
		}

//		int j = 0;
//		String str1;
//		while (ar.size() > j) {
//			// System.out.println(ar.get(j));
//			str1 = ar.get(j);
//			out.write(str1.getBytes());
//			j++;
//		}
		outp1.close();
		//out.close();
		System.out.println("Query file completed");

	}
}
