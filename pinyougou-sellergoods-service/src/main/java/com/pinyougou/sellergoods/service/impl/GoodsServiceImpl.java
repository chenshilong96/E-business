package com.pinyougou.sellergoods.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsDescExample;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;

	@Override
	public PageResult findByPage(int page, int rows, TbGoods tbGoods) {

		// 获取商家ID
		String sellerId = tbGoods.getSellerId();

		// 条件
		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		if (tbGoods != null) {
			// 根据商家ID 审核通过 未删除来查询
			if (sellerId != null && !("").equals(sellerId)) {
				criteria.andSellerIdEqualTo(sellerId);
			}
			if (tbGoods.getAuditStatus() != null && !("").equals(tbGoods.getAuditStatus())) {
				criteria.andAuditStatusEqualTo(tbGoods.getAuditStatus());
			}
			
			if(tbGoods.getGoodsName()!=null&&!("").equals(tbGoods.getGoodsName())){
				criteria.andGoodsNameLike("%"+tbGoods.getGoodsName()+"%");
			}
			
			criteria.andIsDeleteIsNull();
		}
		// 分页
		PageHelper.startPage(page, rows);
		Page<TbGoods> p = (Page<TbGoods>) goodsMapper.selectByExample(example);

		return new PageResult(p.getTotal(), p.getResult());
	}

	@Override
	public void add(Goods goods) {
		// 添加商品属性信息
		goods.getGoods().setAuditStatus("0"); // 设置未申请状态
		goodsMapper.insert(goods.getGoods());
		// 添加商品描述信息
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc());

		saveItemList(goods);
	}

	private void setItemValus(Goods goods, TbItem item) {
		item.setGoodsId(goods.getGoods().getId());// 商品SPU编号
		item.setSellerId(goods.getGoods().getSellerId());// 商家编号
		item.setCategoryid(goods.getGoods().getCategory3Id());// 商品分类编号（3级）
		item.setCreateTime(new Date());// 创建日期
		item.setUpdateTime(new Date());// 修改日期

		// 品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		// 分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());

		// 商家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());

		// 图片地址（取spu的第一个图片）
		List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if (imageList.size() > 0) {
			item.setImage((String) imageList.get(0).get("url"));
		}
	}

	@Override
	public Goods findOne(Long id) {
		Goods goods = new Goods();

		// 根据商品ID查询商品信息
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		// 根据商品ID查询商品描述信息
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		// 封装成到组合实体中
		goods.setGoods(tbGoods);
		goods.setGoodsDesc(tbGoodsDesc);
		// 查询SKU商品列表
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> itemList = itemMapper.selectByExample(example);
		goods.setItemList(itemList);

		return goods;
	}

	@Override
	public void update(Goods goods) {
		goods.getGoods().setAuditStatus("0");// 设置未申请状态:如果是经过修改的商品，需要重新设置状态
		goodsMapper.updateByPrimaryKey(goods.getGoods());// 保存商品表
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());// 保存商品扩展表
		//删除原有的sku列表数据		
		TbItemExample example=new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());	
		itemMapper.deleteByExample(example);
		//添加新的sku列表数据
		saveItemList(goods);//插入商品SKU列表数据	

	}

	private void saveItemList(Goods goods) {
		// 是否启用规格
		if ("1".equals(goods.getGoods().getIsEnableSpec())) {
			// 启用

			// 添加商品信息
			List<TbItem> itemList = goods.getItemList();
			for (TbItem item : itemList) {
				// 添加副标题
				String title = goods.getGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());
				for (String key : specMap.keySet()) {
					title += " " + specMap.get(key);
				}
				item.setTitle(title);
				setItemValus(goods, item);
				itemMapper.insert(item);

			}

		} else {
			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());// 商品KPU+规格描述串作为SKU名称
			item.setPrice(goods.getGoods().getPrice());// 价格
			item.setStatus("1");// 状态
			item.setIsDefault("1");// 是否默认
			item.setNum(99999);// 库存数量
			item.setSpec("{}");
			setItemValus(goods, item);
			itemMapper.insert(item);
		}
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for(Long id:ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	@Override
	public void delete(Long[] ids) {
		for(Long id:ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	@Override
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] ids, String status) {
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(ids));
		criteria.andStatusEqualTo(status);
		return itemMapper.selectByExample(example );
	}

}
