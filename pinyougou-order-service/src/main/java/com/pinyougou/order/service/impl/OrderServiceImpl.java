package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.common.IdWorker;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private TbPayLogMapper payLogMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {

		List<Cart> cartList =(List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
		List<String> orderIdList=new ArrayList<>();
		double total_money=0;

		for (Cart cart : cartList) {
			TbOrder tbOrder = new TbOrder();
			Long orderId=idWorker.nextId();
			System.out.println(orderId);
			tbOrder.setCreateTime(new Date());//订单创建时间
			tbOrder.setOrderId(orderId);//订单id

			tbOrder.setPaymentType(order.getPaymentType());//支付类型，从前台提交过来的
			tbOrder.setReceiver(order.getReceiver());//收件人，从前台提交过来
			tbOrder.setReceiverAreaName(order.getReceiverAreaName());//收货地址，从前台提交过来
			tbOrder.setReceiverMobile(order.getReceiverMobile());//收货人联系方式，从前台提交过来
			tbOrder.setSellerId(cart.getSellerId());
			tbOrder.setSourceType(order.getSourceType());//订单来源
			tbOrder.setStatus("1");//1未支付
			tbOrder.setUpdateTime(new Date());
			tbOrder.setUserId(order.getUserId());

			orderIdList.add(orderId.toString());
			double money=0;//订单金额
			for(TbOrderItem orderItem:cart.getOrderItemList()){
				money+=orderItem.getTotalFee().doubleValue();
				orderItem.setId(idWorker.nextId());//设置主键
				orderItem.setOrderId(orderId);//设置订单详情所属订单
				orderItemMapper.insert(orderItem);//保存订单详情
			}
			tbOrder.setPayment(new BigDecimal(money));//设置订单金额
			orderMapper.insert(tbOrder);
			total_money+=money;
		}

		if ("1".equals(order.getPaymentType())){
			TbPayLog payLog=new TbPayLog();
			payLog.setCreateTime(new Date());
			payLog.setUserId(order.getUserId());
			payLog.setOrderList(orderIdList.toString().replace("[","")
					.replace("]","").replace(" ",""));
			payLog.setOutTradeNo(idWorker.nextId()+"");
			payLog.setTotalFee((long)(total_money*100));
			payLog.setTradeState("0"); //		未支付
			payLog.setPayType("1");
			payLogMapper.insert(payLog);
			redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);
		}
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());
	}


	@Override
	public TbPayLog searchPayLogFromRedis(String userId) {

		return (TbPayLog)redisTemplate.boundHashOps("payLog").get(userId);
	}

	@Override
	public void updateOrderStatus(String out_trade_no, String transaction_id) {
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
		payLog.setPayTime(new Date());
		payLog.setTransactionId(transaction_id);
		payLog.setTradeState("1");
		payLogMapper.updateByPrimaryKey(payLog);

		//修改订单状态
		String orders = payLog.getOrderList();
		String[] orderList = orders.split(",");
		for (String orderId : orderList) {
			TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
			tbOrder.setStatus("2");
			orderMapper.updateByPrimaryKey(tbOrder);
		}

		//清除redis缓存数据
		redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());

	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		TbOrderExample.Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}



}
