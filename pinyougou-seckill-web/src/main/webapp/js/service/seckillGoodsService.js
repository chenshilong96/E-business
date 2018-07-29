app.service('seckillGoodsService',function($http){
	//查找符合条件订单秒杀商品
	this.findList = function(){
		return $http.get('/seckillGoods/findList.do');
	}
	
	//根据id从缓存中获取秒杀商品实体
	this.findOneFromRedis = function(id){
		return $http.get('/seckillGoods/findOneFromRedis.do?id='+id);
	}
	
	//提交订单
	this.submitOrder = function(seckillId){
		return $http.get('/seckillOrder/submitOrder.do?seckillId='+seckillId);
	}
});