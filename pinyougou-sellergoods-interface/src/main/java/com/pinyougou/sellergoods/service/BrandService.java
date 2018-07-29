package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;
import entity.Result;

public interface BrandService {
	
	/**
	 * 查询所有的品牌
	 * @return
	 */
	List<TbBrand> findAll();
	
	/**
	 * 分页查询
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageResult findPage(int pageNum,int pageSize);
	
	/**
	 * 添加商品
	 * @param brand
	 * @return
	 */
	public void addBrand(TbBrand brand);
	
	/**
	 * 根据ID查询单个品牌
	 * @param id
	 */
	public TbBrand findOne(long id);
	
	/**
	 * 修改品牌信息
	 * @param brand
	 */
	public void updateBrand(TbBrand brand);
	
	/**
	 * 删除一个或多个品牌
	 * @param ids
	 * @return
	 */
	public void deleteBrands(long[] ids);
	
	/**
	 * 分页条件查询
	 * @return
	 */
	public PageResult findPage(TbBrand brand,int pageNum,int pageSize );
	
	/**
	 * 模块管理的查询下拉列表
	 */
	public List<Map> findOptionList();
}
