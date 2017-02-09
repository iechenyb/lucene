package com.cyb.cse;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.solr.core.SolrResourceLoader;
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
		String text = "三劫散仙是一个菜鸟chenyb陈远豹iechenyb";
		TokenStream ts = analyzer.tokenStream("field", new StringReader(text));
		CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
		ts.reset();// 重置做准备
		while (ts.incrementToken()) {
			System.out.println("xx:"+term.toString());
		}
		ts.end();//
		ts.close();// 关闭流
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
