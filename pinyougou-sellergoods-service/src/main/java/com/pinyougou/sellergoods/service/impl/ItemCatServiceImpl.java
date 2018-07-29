package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.pojo.TbItemCatExample.Criteria;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.ItemCatService;

@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public List<TbItemCat> findByParentId(long ParentId) {
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(ParentId);

		// 每次执行查询的时候，一次性读取缓存进行存储 (因为每次增删改都要执行此方法)
		List<TbItemCat> list = findAll();
		for (TbItemCat cat : list) {
			redisTemplate.boundHashOps("itemCat").put(cat.getName(), cat.getTypeId());
		}
		System.out.println("更新缓存:商品分类表");

		return itemCatMapper.selectByExample(example);
	}

	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);
	}

	@Override
	public List<Map> findTemplates() {

		return typeTemplateMapper.findTemplateIds();
	}

	@Override
	public @ResponseBody TbItemCat findOne(long id) {
		return itemCatMapper.selectByPrimaryKey(id);
	}

	@Override
	public void update(TbItemCat itemCat) {
		itemCatMapper.updateByPrimaryKey(itemCat);
	}

	@Override
	public void delete(String[] ids) {
		for (String id : ids) {
			itemCatMapper.deleteByPrimaryKey(Long.parseLong(id));
		}
	}

	@Override
	public List<TbItemCat> findAll() {

		return itemCatMapper.selectByExample(null);
	}

}
