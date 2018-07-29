package com.pinyougou.solrutil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

@Component
public class SolrUtil {

	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	public void importItemData() {
		
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1"); //已审核
		List<TbItem> itemList = itemMapper.selectByExample(example);
		
		System.out.println("****商品列表****");
		
		for(TbItem item:itemList) {
			System.out.println(item.getTitle());
			item.getSpec();
			Map specMap = JSON.parseObject(item.getSpec());
			item.setSpecMap(specMap);
		}
		
		solrTemplate.saveBeans(itemList);
		solrTemplate.commit();
		System.out.println("****结束****");
	}
	
	/*
	 * 删除数据
	 */
	public void deleteItemData() {
		Query query = new SimpleQuery("*:*");;
		solrTemplate.delete(query);
		solrTemplate.commit();
		System.out.println("删除完成");
	}
	
	public static void main(String[] args) {
		
		
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
		
		solrUtil.deleteItemData(); //删除数据
		//solrUtil.importItemData(); //导入数据
	}
}
