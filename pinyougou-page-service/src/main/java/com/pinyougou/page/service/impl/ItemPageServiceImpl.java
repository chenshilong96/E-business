package com.pinyougou.page.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;
import com.pinyougou.pojogroup.Goods;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class ItemPageServiceImpl implements ItemPageService {

	@Value("${pagedir}")
	private String pagedir;
	@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbItemMapper itemMapper;
	
	@Override
	public boolean genItemHtml(Long goodsId) {
		
		
		Configuration configuration = freeMarkerConfig.getConfiguration();
		try {
			//1.获取模板
			Template template = configuration.getTemplate("item.ftl");
			//2.创建数据模型
			Map dataModel = new HashMap();
			
			TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);//获取商品信息
			dataModel.put("goods", goods);
			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId); //获取商品描述信息
			dataModel.put("goodsDesc", goodsDesc);
			
			//查询商品分类
			String category1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
			String category2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
			String category3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
			dataModel.put("category1", category1);
			dataModel.put("category2", category2);
			dataModel.put("category3", category3);
			//查询SKU列表
			TbItemExample example = new TbItemExample();
			Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(goodsId);
			criteria.andStatusEqualTo("1");//状态为有效
			example.setOrderByClause("is_default desc");//按照状态降序，保证第一个为默认
			List<TbItem> itemList = itemMapper.selectByExample(example );
			dataModel.put("itemList", itemList);
			//3.获取输出流对象
			System.out.println("pagedir:"+pagedir);
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(pagedir+goodsId+".html")), "UTF-8");
			PrintWriter printWriter = new PrintWriter(writer);
			//4.将模板文件生成到指定位置
			template.process(dataModel, printWriter);
			//5.关闭资源
			printWriter.close();
			writer.close();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void deleteItemHtml(Long goodsId) {
		new File(pagedir+goodsId+".html").delete();
	}

}
