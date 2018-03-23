package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;

public interface BrandService {
	/**
	 * 查询品牌所有
	 * @return
	 */
	public List<TbBrand> findAll();

	/**
	 *查询分页
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);

	/**
	 * 增加品牌
	 */
	public void add(TbBrand tbBrand);

	/**
	 * 修改品牌
	 * @param tbBrand
	 */
	public void update(TbBrand tbBrand);

	/**
	 * 查询单个品牌
	 * @param id
	 * @return
	 */
	public TbBrand findOne(Long id);

	/**
	 * 删除品牌
	 * @param ids
	 */
	public void dele(Long[] ids);

	/**
	 * 条件查询
	 * @param tbBrand
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);

	public List<Map> selectOptionList();
}
