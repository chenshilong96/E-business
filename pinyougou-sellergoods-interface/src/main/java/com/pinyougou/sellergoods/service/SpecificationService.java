package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojogroup.Specification;

import entity.PageResult;

public interface SpecificationService {
	/**
	 * 查询所有规格
	 * @return
	 */
	List<TbSpecification> findAll();
	
	/**
	 * 
	 * @param page 当前页
	 * @param rows 每页显示的记录数
	 * @return
	 */
	PageResult findPage(int page,int rows);
	
	/**
	 * 添加规格
	 * @param specification
	 */
	void add(Specification specification);
	/**
	 * 根据规格的ID查询规格
	 * @param id
	 * @return
	 */
	Specification findOne(Long id);
	
	/**
	 * 更新规格信息
	 * @param specification
	 */
	void update(Specification specification);
	
	/**
	 * 删除规格信息
	 */
	void delete(String[] ids);
	
	/**
	 * 模块管理的下拉列表
	 */
	public List<Map> findOptionList();
}
