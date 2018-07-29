package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojogroup.Goods;

public interface ItemCatService {

	/**
	 * 根据parentId来查询分类列表
	 */
	List<TbItemCat> findByParentId(long ParentId);
	
	/**
	 * 添加分类
	 */
	void add(TbItemCat itemCat);
	/**
	 * 模板下拉菜单
	 * @return
	 */
	List<Map> findTemplates();
	
	/**
	 * 根据ID查找
	 * @param id
	 * @return
	 */
	TbItemCat findOne(long id);
	
	/**
	 * 更新分类
	 * @param itemCat
	 */
	void update(TbItemCat itemCat);
	
	/**
	 * 删除分类
	 * @param ids
	 */
	void delete(String[] ids);
	
	/**
	 * 查询所有分类
	 */
	List<TbItemCat> findAll();
	
}
