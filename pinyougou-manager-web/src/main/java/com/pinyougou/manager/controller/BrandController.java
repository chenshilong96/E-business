package com.pinyougou.manager.controller;


import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;



@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;
	
	/**
	 * 获取所有品牌列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();
	}
	
	/**
	 * 分页
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page,int rows) {
		return brandService.findPage(page, rows); 
	}
	
	/**
	 * 添加
	 * @param brand
	 * @return
	 */
	@RequestMapping("/addBrand")
	public Result addBrand(@RequestBody TbBrand brand) {
		try {
			brandService.addBrand(brand);
			return new Result(true,"添加成功");
		}catch(Exception ex) {
			ex.printStackTrace();
			return new Result(false,"添加失败");
		}
	}
	
	/**
	 * 根据ID查找
	 * @param id
	 * @return
	 */
	@RequestMapping("/findById")
	public TbBrand findOne(long id) {
		return brandService.findOne(id);
	}
	
	/**
	 * 更新/修改
	 * @param brand
	 * @return
	 */
	@RequestMapping("/updateBrand")
	public Result updateBrand(@RequestBody TbBrand brand) {
		try {
			brandService.updateBrand(brand);
			return new Result(true,"修改成功");
		}catch(Exception ex) {
			ex.printStackTrace();
			return new Result(false,"修改失败");
		}
	}
	
	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/deleteBrand")
	public Result updateBrand(long[] ids) {
		try {
			brandService.deleteBrands(ids);
			return new Result(true,"删除成功");
		}catch(Exception ex) {
			ex.printStackTrace();
			return new Result(false,"删除失败");
		}
	}
	
	/**
	 * 分页查询
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbBrand brand,int page,int rows){
		return brandService.findPage(brand, page, rows);
	}
	
}
