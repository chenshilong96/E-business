package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.sellergoods.service.SpecificationService;
import com.pinyougou.sellergoods.service.TypeTemplateService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTempalteController {
	
	@Reference
	private TypeTemplateService typeTemplateService;
	@Reference
	private BrandService brandService;
	@Reference
	private SpecificationService specificationService;
	
	@RequestMapping("/findPage")
	public PageResult findPage(int page,int rows) {
		return typeTemplateService.findPage(page, rows);
	}
	
	@RequestMapping("/brandList")
	public List<Map> brandList(){
		return brandService.findOptionList();
	}
	
	@RequestMapping("/specificationList")
	public List<Map> specificationList(){
		return specificationService.findOptionList();
	}
	
	@RequestMapping("/add")
	public Result add(@RequestBody TbTypeTemplate tbTypeTemplate) {
		try {
			
			typeTemplateService.add(tbTypeTemplate);
			return new Result(true,"添加成功");
		}catch(Exception e) {
			return new Result(false,"添加失败");
		}
	}
	
	
	@RequestMapping("/findOne")
	public @ResponseBody TbTypeTemplate findOne(long id) {
		return typeTemplateService.findOne(id) ;
	}
	
	@RequestMapping("/update")
	public Result update(@RequestBody TbTypeTemplate tbTypeTemplate) {
		try {
			typeTemplateService.update(tbTypeTemplate);
			return new Result(true,"修改成功");
		}catch(Exception e) {
			return new Result(false,"修改失败");
		}
	}
	
	@RequestMapping("/delete")
	public Result delete(String[] ids) {
		try {
			typeTemplateService.delete(ids);
			return new Result(true,"删除成功");
		}catch(Exception e) {
			return new Result(false,"删除失败");
		}
	}
	
}
