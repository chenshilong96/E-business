package com.pinyougou.shop.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.sellergoods.service.TypeTemplateService;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

	@Reference
	private ItemCatService itemCatService;
	@Reference
	private TypeTemplateService typeTemplateService;
	
	@RequestMapping("/findByParentId")
	public List<TbItemCat> findByParentId(Long parentId) {
		
		
		return itemCatService.findByParentId(parentId) ;
		
	}
	
	@RequestMapping("/findOne")
	public TbItemCat findOne(Long id) {
		return itemCatService.findOne(id);
	}
	
	@RequestMapping("/findBrandByTypeId")
	public TbTypeTemplate findBrandByTypeId(String id) {
		return typeTemplateService.findOne(Long.parseLong(id));
	}
	
	@RequestMapping("/findSpecList")
	public List<Map> findSpecList(Long id){
		return typeTemplateService.findSpecList(id);
	}
	@RequestMapping("/findAll")
	public List<TbItemCat> findAll(){
		return itemCatService.findAll();
	}
	
}
