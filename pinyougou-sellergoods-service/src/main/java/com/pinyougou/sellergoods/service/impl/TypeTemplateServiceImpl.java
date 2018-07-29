package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbSpecificationOptionExample.Criteria;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;

import entity.PageResult;
import entity.Result;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {
	
	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	@Autowired
	private RedisTemplate redisTemplate;

	
	/**
	 * 将数据存入缓存
	 */
	private void saveToRedis(){
		//获取模板数据
		List<TbTypeTemplate> list = findAll();
		//循环列表
		for(TbTypeTemplate typeTemplate:list) {
			//存储品牌列表数据
			List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(),Map.class);
			redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);
			//存储规格列表数据
			List<Map> specList = findSpecList(typeTemplate.getId());
			redisTemplate.boundHashOps("specList").put(typeTemplate.getId(),specList);
		}
	}

	
	@Override
	public PageResult findPage(int page, int rows) {
		
		PageHelper.startPage(page, rows);
		Page<TbTypeTemplate> p = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		saveToRedis();//存入数据到缓存
		System.out.println("模板数据存入到了缓存中。。。。。");
		return new PageResult(p.getTotal(), p.getResult());
	}

	@Override
	public void add(TbTypeTemplate tbTypeTemplate) {
		typeTemplateMapper.insert(tbTypeTemplate); 
	}

	@Override
	public TbTypeTemplate findOne(long id) {
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	@Override
	public void update(TbTypeTemplate tbTypeTemplate) {
		typeTemplateMapper.updateByPrimaryKey(tbTypeTemplate);
	}

	@Override
	public void delete(String[] ids) {
		for(String id:ids) {
			typeTemplateMapper.deleteByPrimaryKey(Long.parseLong(id));
		}
	}

	@Override
	public List<Map> findSpecList(long id) {
		//根据模板ID查询到相应的模板
		TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
		//根据模板规格ID查询规格选项列表
		List<Map> list = JSON.parseArray(typeTemplate.getSpecIds(),Map.class);
		for(Map map:list) {
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(new Long((Integer)map.get("id")));
			List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
			map.put("options", options);
		}
		
		return list;
	}


	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

}
