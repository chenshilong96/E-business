package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbTypeTemplate;

import entity.PageResult;
import entity.Result;

public interface TypeTemplateService {

	/**
	 * 分页查询
	 * @param page
	 * @param rows
	 * @return
	 */
	PageResult findPage(int page,int rows);
	
	/**
	 * 添加
	 */
	void add(TbTypeTemplate tbTypeTemplate);
	
	/**
	 * 根据ID查询
	 */
	TbTypeTemplate findOne(long id);
	
	/**
	 * 修改
	 */
	void update(TbTypeTemplate tbTypeTemplate);
	
	/**
	 * 删除
	 */
	void delete(String[] ids);
	
	/**
	 * 查询规格列表
	 * @param id
	 * @return
	 */
	List<Map> findSpecList(long id);
	
	/**
	 * 查询所有模板
	 * @return
	 */
	List<TbTypeTemplate> findAll();
}
