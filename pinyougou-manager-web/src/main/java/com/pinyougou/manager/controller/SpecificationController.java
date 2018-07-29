package com.pinyougou.manager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojogroup.Specification;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

	@Reference
	private SpecificationService specificationService;
	
	@RequestMapping("/findAll")
	public List<TbSpecification> findAll(){
		
		return specificationService.findAll();
	}
	@RequestMapping("/findPage")
	public PageResult findPage(int page,int rows) {
		return specificationService.findPage(page, rows);
	}
	
	@RequestMapping("/add")
	public Result add(@RequestBody Specification specification) {
		try {
			specificationService.add(specification);
			return new Result(true,"添加成功");
		}catch(Exception e) {
			e.printStackTrace();
			return new Result(false,"添加失败");
		}
	}
	
	@RequestMapping("/findOne")
	public @ResponseBody Specification findOne(long id) {
		
		
		return specificationService.findOne(id);
	}
	
	@RequestMapping("/update")
	public Result update(@RequestBody Specification specification) {
		try {
			specificationService.update(specification);
			return new Result(true,"修改成功");
		}catch(Exception e) {
			e.printStackTrace();
			return new Result(false,"修改失败");
		}
	}
	
	@RequestMapping("/delete")
	public Result delete(String[] ids) {
		
		try {
			specificationService.delete(ids);
			return new Result(true,"删除成功");
		}catch(Exception e) {
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
	}
}
