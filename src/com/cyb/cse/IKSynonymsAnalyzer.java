package com.cyb.cse;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.apache.solr.core.SolrResourceLoader;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKTokenizer;
/**
 * 可以加载同义词库的Lucene
 * 专用IK分词器
 * */
public class IKSynonymsAnalyzer extends Analyzer {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		// 下面这个分词器，是经过修改支持同义词的分词器
		IKSynonymsAnalyzer analyzer = new IKSynonymsAnalyzer();
		String text = "期权";
		TokenStream ts = analyzer.tokenStream("field", new StringReader(text));
		CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
		ts.reset();// 重置做准备
		while (ts.incrementToken()) {
			System.out.println("xx:"+term.toString());
		}
		ts.end();//
		ts.close();// 关闭流
		Iktest();
	}
	@SuppressWarnings({ "resource", "deprecation" })
	public static void Iktest() throws IOException{
		 String txt = "我是中国人";  
         Analyzer analyzer1 = new StandardAnalyzer(Version.LUCENE_36);// 标准分词器  
         Analyzer analyzer2 = new SimpleAnalyzer(Version.LUCENE_36);// 简单分词器  
         Analyzer analyzer3 = new CJKAnalyzer(Version.LUCENE_36);// 二元切分  
         Analyzer analyzer4 = new IKAnalyzer(false);// 语意分词  
         TokenStream tokenstream1 = analyzer1.tokenStream("content", new StringReader(txt));// 生成一个分词流  
         TokenStream tokenstream2 = analyzer2.tokenStream("content", new StringReader(txt));  
         TokenStream tokenstream3 = analyzer3.tokenStream("content", new StringReader(txt));  
         TokenStream tokenstream4 = analyzer4.tokenStream("content", new StringReader(txt));  
	        CharTermAttribute termAttribute1 = tokenstream1.addAttribute(CharTermAttribute.class);// 为token设置属性类  
	        tokenstream1.reset();// 重新设置  
	        while (tokenstream1.incrementToken()) {// 遍历得到token  
	            System.out.print(new String(termAttribute1.buffer(), 0, termAttribute1.length()) + "  ");  
	        } 
	        System.out.println();
	        CharTermAttribute termAttribute2 = tokenstream2.addAttribute(CharTermAttribute.class);// 为token设置属性类  
	        tokenstream2.reset();// 重新设置  
	        while (tokenstream2.incrementToken()) {// 遍历得到token  
	            System.out.print(new String(termAttribute2.buffer(), 0, termAttribute2.length()) + "  ");  
	        } 
	        System.out.println();
	        CharTermAttribute termAttribute3 = tokenstream3.addAttribute(CharTermAttribute.class);// 为token设置属性类  
	        tokenstream3.reset();// 重新设置  
	        while (tokenstream3.incrementToken()) {// 遍历得到token  
	            System.out.print(new String(termAttribute3.buffer(), 0, termAttribute3.length()) + "  ");  
	        } 
	        System.out.println();
	        CharTermAttribute termAttribute4 = tokenstream4.addAttribute(CharTermAttribute.class);// 为token设置属性类  
	        tokenstream4.reset();// 重新设置  
	        while (tokenstream4.incrementToken()) {// 遍历得到token  
	            System.out.print(new String(termAttribute4.buffer(), 0, termAttribute4.length()) + "  ");  
	        } 
	}
	@SuppressWarnings("deprecation")
	@Override
	protected TokenStreamComponents createComponents(String arg0, Reader arg1) {
		Tokenizer token = new IKTokenizer(arg1, true);// 开启智能切词
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("luceneMatchVersion", "LUCENE_47");
		paramsMap.put("synonyms", "d:\\lucene\\synonyms.txt");//文件格式必须是utf-8 无bom 同义词
		SynonymFilterFactory factory = new SynonymFilterFactory(paramsMap);
		SolrResourceLoader loader = new SolrResourceLoader("");
		try {
			factory.inform(loader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new TokenStreamComponents(token, factory.create(token));
	}
}
