package com.pinyougou.sellergoods.service;

import java.util.List;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;

import entity.PageResult;

public interface GoodsService {

	/**
	 * 分页条件查询商品(未完成)
	 * @param page
	 * @param rows
	 * @return
	 */
	public PageResult findByPage(int page,int rows,TbGoods tbGoods);
	/**
	 * 添加商品
	 * @param goods
	 */
	void add(Goods goods);
	
	/**
	 * 根据商品ID查询商品
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);
	
	/**
	 * 修改商品
	 * @param goods
	 */
	void update(Goods goods);
	/**
	 * 修改商品状态
	 * @param ids
	 * @param status
	 */
	void updateStatus(Long[] ids ,String status);
	
	/**
	 * 删除商品
	 * @param ids
	 */
	void delete(Long[] ids);
	/**
	 * 根据商品ID和状态查询商品信息
	 * @param ids
	 * @param status
	 * @return
	 */
	List<TbItem> findItemListByGoodsIdandStatus(Long[] ids ,String status);
}

