package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public Map<String, Object> search(Map searchMap) {
		Map<String, Object> resultMap = new HashMap<>();
		// 根据关键字将查询结果高亮显示
		resultMap.putAll(searchList(searchMap));
		// 根据关键字查询所有分类名称
		List<String> categoryList = searchCategoryList(searchMap);
		resultMap.put("categoryList", categoryList);
		// 查询品牌和规格列表
		String categoryName = (String) searchMap.get("category");
		if (categoryName != null && !"".equals(categoryName)) {
			// 有分类名称
			resultMap.putAll(searchBrandAndSpecList(categoryName));
		} else {
			// 没有分类名称，按照第一个分类来查询
			if (categoryList.size() > 0) {
				resultMap.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}

		}

		return resultMap;
	}

	/**
	 * 查询品牌和规格列表
	 * 
	 * @param category
	 *            分类名称
	 * @return
	 */
	private Map searchBrandAndSpecList(String category) {
		Map map = new HashMap<>();

		// 根据分类名称找到模板ID
		Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
		if (typeId != null) {
			// 根据模板ID查找品牌列表
			List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
			// 根据模板ID查找规格列表
			List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);

			map.put("brandList", brandList);
			map.put("specList", specList);
		}

		return map;
	}

	/**
	 * 根据关键词查询所有分类名称
	 * 
	 * @param searchMap
	 * @return
	 */
	private List searchCategoryList(Map searchMap) {
		List<String> list = new ArrayList<>();
		Query query = new SimpleQuery();
		// 根据关键字查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		// 设置分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		// 得到分组页(查询结果)
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		// 根据域得到分组结果结果集
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		// 得到分组结果入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		// 得到分组入口集合
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		for (GroupEntry<TbItem> c : content) {
			list.add(c.getGroupValue());// 将分组结果的名称封装到结果集中
		}
		return list;
	}

	/**
	 * 高亮显示查询结果
	 * 
	 * @param searchMap
	 * @return
	 */
	private Map searchList(Map searchMap) {
		
		//多关键字空格处理
		searchMap.put("keywords", searchMap.get("keywords").toString().replace(" ", ""));
		
		Map map = new HashMap<>();
		// 高亮显示功能实现
		HighlightQuery query = new SimpleHighlightQuery();

		// 设置查询条件(按关键字查询)
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);

		// 按分类过滤
		if (!"".equals(searchMap.get("category"))) {
			Criteria filterCategory = new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCategory);
			query.addFilterQuery(filterQuery);
		}
		// 按品牌过滤
		if (!"".equals(searchMap.get("brand"))) {
			Criteria filterCategory = new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCategory);
			query.addFilterQuery(filterQuery);
		}

		// 按规格过滤
		if (searchMap.get("spec") != null) {
			Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
			for (String specName : specMap.keySet()) {
				// 获取value
				String specValue = specMap.get(specName);

				Criteria filterCategory = new Criteria("item_spec_" + specName).is(specValue);
				FilterQuery filterQuery = new SimpleFilterQuery(filterCategory);
				query.addFilterQuery(filterQuery);
			}
		}
		
		// 按价格过滤
		if(!"".equals(searchMap.get("price"))) {
			String[] price = ((String) searchMap.get("price")).split("-"); //得到价格区间
			String startPrice = price[0]; //获取起始价格
			String endPrice = price[1]; //获取截至价格
			
			//构建查询条件
			if(!startPrice.equals("0")) {    //起始价格是0
				Criteria filterCategory = new Criteria("item_price").greaterThanEqual(startPrice);
				FilterQuery filterQuery = new SimpleFilterQuery(filterCategory );
				query.addFilterQuery(filterQuery );
			}
			
			if(!endPrice.equals("*")) {		//截止价格为*（无穷大）
				Criteria filterCategory = new Criteria("item_price").lessThanEqual(endPrice);
				FilterQuery filterQuery = new SimpleFilterQuery(filterCategory );
				query.addFilterQuery(filterQuery );
			}
		}
		
		//分页查询
		Integer pageNo = (Integer) searchMap.get("pageNo"); //提取当前页码
		Integer pageSize = (Integer) searchMap.get("pageSize"); //提取每页显示的记录数
		if(pageNo==null) { 
			pageNo = 1;
		}
		if(pageSize==null) {
			pageSize = 20;
		}
		query.setOffset(pageNo);//从第几条记录开始查询
		query.setRows(pageSize);//每页显示多少条记录数
		
		
		//排序
		String sortField = (String) searchMap.get("sortField");
		String sortValue = (String) searchMap.get("sort");
		
		if(!"".equals(sortValue)&&!"".equals(sortField)) {
			if(sortValue.equals("ASC")) {
				Sort sort = new Sort(Sort.DEFAULT_DIRECTION.ASC, "item_"+sortField);
				query.addSort(sort);
			}else if(sortValue.equals("DESC")) {
				Sort sort = new Sort(Sort.DEFAULT_DIRECTION.DESC, "item_"+sortField);
				query.addSort(sort);
			}
		}
		
		
		
		// 设置高亮选项
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title"); // 添加高亮域
		highlightOptions.setSimplePrefix("<em style='color:red'>");// 添加高亮前缀
		highlightOptions.setSimplePostfix("</em>");// 添加高亮后缀
		query.setHighlightOptions(highlightOptions);// 添加高亮选项
		
		//设置一次查询多少条记录数
		//query.setRows(1000);
		 // 添加查询条件
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		
		// 获取查询结果
		List<HighlightEntry<TbItem>> resultList = page.getHighlighted();
		for (HighlightEntry<TbItem> h : resultList) { // 循环高亮入口集合
			TbItem item = h.getEntity(); // 获取原实体类
			if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
				item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
			}
		}

		map.put("rows", page.getContent());    //每页显示的记录
		map.put("totalPages", page.getTotalPages()); //总页数
		map.put("total", page.getTotalElements()); //总记录数
		return map;

	}

	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}

	@Override
	public void deleteList(Long[] ids) {
		System.out.println("删除商品ID"+ids);
		Query query = new SimpleQuery();
		Criteria criteria = new Criteria("item_goodsid").in(Arrays.asList(ids));
		query.addCriteria(criteria );
		solrTemplate.delete(query);
		solrTemplate.commit();
	}

}
