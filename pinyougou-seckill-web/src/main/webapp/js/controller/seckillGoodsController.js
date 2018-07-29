app.controller('seckillGoodsController',function($scope,$location,$interval,seckillGoodsService){
	
	//查找符合条件订单秒杀商品
	$scope.findList = function(){
		seckillGoodsService.findList().success(
				function(response){
					$scope.list = response;
					
				}
		);
	}
	
	//获取传递过来的id
	$scope.getId = function(){
		var id = $location.search()['id'];
		//根据id从缓存中获取秒杀商品实体
		seckillGoodsService.findOneFromRedis(id).success(
				function(response){
					$scope.entity = response;
					
					allsecond =Math.floor( (  new Date($scope.entity.endTime).getTime()- (new Date().getTime())) /1000); //总秒数
					var second = allsecond;
					time= $interval(function(){ 
					  if(second>0){ 
						second =second-1;
						$scope.timeString=convertTimeString(second);//转换时间字符串
					  }else{
						  $interval.cancel(time); 		  
						  alert("秒杀服务已结束");
					  }
					},1000);	

				}
		);
	}

	
	
	//转换秒为   天小时分钟秒格式  XXX天 10:22:33
	convertTimeString=function(second){
		var days= Math.floor( second/(60*60*24));//天数
		var hours= Math.floor( (second-days*60*60*24)/(60*60) );//小数数
		var minutes= Math.floor(  (second -days*60*60*24 - hours*60*60)/60    );//分钟数
		var seconds= second -days*60*60*24 - hours*60*60 -minutes*60; //秒数
		var timeString="";
		if(days>0){
			timeString=days+"天 ";
		}
		return timeString+hours+":"+minutes+":"+seconds;
	}

	//提交订单
	$scope.submitOrder = function(){
		seckillGoodsService.submitOrder($scope.entity.id).success(
				function(response){
					if(response.success){
						alert("下单成功，请在1分钟内完成支付");
						location.href = "pay.html";
					}else{
						alert(response.message);
					}
				}
		);
	}
	
	
	
});