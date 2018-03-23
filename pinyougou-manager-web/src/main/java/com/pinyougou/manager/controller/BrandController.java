package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;


import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

/**
 * 品牌控制层
 * @author a2363196581
 *
 */
@RestController
@RequestMapping("/brand")
public class BrandController {
	@Reference
	private BrandService brandService;

	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();
	}

	@RequestMapping("/findPage")
	public PageResult findPage(int page,int rows){
		return brandService.findPage(page,rows);
	}

	@RequestMapping("/add")
	public Result add(@RequestBody TbBrand tbBrand){
		try {
			brandService.add(tbBrand);
			return new Result(true,"添加成功");
		}catch (Exception e){
			return new Result(false,"添加失败");
		}

	}

	@RequestMapping("/update")
	public Result update(@RequestBody TbBrand tbBrand){
		try {
			brandService.update(tbBrand);
			return  new Result(true,"修改成功");
		}catch (Exception e){
			return new Result(false,"修改失败");
		}

	}

	@RequestMapping("/findOne")
	public TbBrand findOne(Long id){
		return brandService.findOne(id);
	}

	@RequestMapping("/dele")
	public Result dele(Long[] ids){
		try {
			brandService.dele(ids);
			return new Result(true,"删除成功");
		}catch (Exception e){
			return new Result(false,"删除失败");
		}
	}


	@RequestMapping("/search")
	public PageResult search(@RequestBody TbBrand tbBrand,int page,int rows){
		System.out.println(page);
		System.out.println(rows);
		return brandService.findPage(tbBrand,page,rows);
	}

	@RequestMapping("/findBrandList")
	public List<Map> findBrandList(){
		return brandService.selectOptionList();
	}
}
