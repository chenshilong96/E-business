package com.pinyougou.page.service;

/**
 * 商品详细页接口
 * @author csl
 *
 */
public interface ItemPageService {
	
	/**
	 * 生成商品详细页
	 * @param goodsId
	 * @return
	 */
	public boolean genItemHtml(Long goodsId);
	
	/**
	 * 根据商品id删除商品详细页
	 * @param goodsId
	 */
	public void deleteItemHtml(Long goodsId);
}
