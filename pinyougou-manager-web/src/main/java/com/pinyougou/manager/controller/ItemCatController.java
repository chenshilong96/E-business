package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.sellergoods.service.TypeTemplateService;

import entity.Result;

@RestController
@RequestMapping("/item")
public class ItemCatController {
	
	@Reference
	private ItemCatService itemCatService;
	
	/**
	 * 根据parentId查询
	 * @param parentId
	 * @return
	 */
	@RequestMapping("/findByParentId")
	public List<TbItemCat> findByParentId(long parentId){
		
		return itemCatService.findByParentId(parentId);
	}
	
	/**
	 * 添加
	 * @param itemCat
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbItemCat itemCat) {
		
		try {
			itemCatService.add(itemCat);
			
			return new Result(false,"添加成功");
		}catch(Exception e) {
			e.printStackTrace();
			return new Result(false,"添加失败");
		}
	}
	
	/**
	 * 查找模板列表
	 * @return
	 */
	@RequestMapping("/findTemplateIds")
	public List<Map> findTemplateIds(){
		return itemCatService.findTemplates();
	}
	/**
	 * 查找一个
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public @ResponseBody TbItemCat findOne(long id) {
		return itemCatService.findOne(id);
	}
	
	/**
	 * 修改
	 * @param itemCat
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbItemCat itemCat) {
		try {
			itemCatService.update(itemCat);
			return new Result(false,"修改成功");
		}catch(Exception e) {
			e.printStackTrace();
			return new Result(false,"修改失败");
		}
	}
	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(String[] ids) {
		try {
			itemCatService.delete(ids);
			return new Result(false,"删除成功");
		}catch(Exception e) {
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
	}
	/**
	 * 查找所有
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbItemCat> findAll(){
		return itemCatService.findAll();
	}
}
