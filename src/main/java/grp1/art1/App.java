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
		}

		path = Paths.get(indexPath);
		if (Files.notExists(path)) {
			System.out.println("no dir exists");
			new File(indexPath).mkdirs();
			System.out.println("\n Index dir created");
		}

		path = Paths.get(scorePath);
		if (Files.notExists(path)) {
			System.out.println("no dir exists");
			new File(scorePath).mkdirs();
			System.out.println("\n Score dir created");			
		}

		indP = indexPath;
		docsPath = documentPath;
		queryPath = query_path;
		query_result_Path = qres_path;

		IndexFiles.cranIndexer(indP, docsPath, iwcSim, valAnalyzer);
		SearchEngine.indSearcher(indP, queryPath, valAnalyzer, iwcSim);

		String file_score_path = indP + "score/original_scores.txt";
		
		PrintWriter outp1 = new PrintWriter(file_score_path);
		List<String> lines = Files.readAllLines(new File(query_result_Path).toPath());
		
		int ij = 1;

		String all_score = null;
		String data_write_to_file = null;
		ArrayList<String> ar = null;
		ar = new ArrayList<String>();

		for (String line : lines) {
			// System.out.println("p" + ij + " = " + line);
			String[] op = line.split(" ");
			// System.out.println(op[0]);
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

			data_write_to_file = (op[0] + " 0 " + op[1] + " " + inverse_rel_num);//.concat(System.lineSeparator());
			if (ij!=1) {
				data_write_to_file = "\n" + data_write_to_file;
			}
			outp1.print(data_write_to_file);
			ij++;
		}
		outp1.close();
		System.out.println("Orignal Result file transformation completed");
	}
}
