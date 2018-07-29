app.controller('itemController',function($scope){
	
	$scope.number = 1;
	
	//数量增加或减少
	$scope.addNumber = function(num){
		
		$scope.number += num;
		
		if($scope.number<1){
			$scope.number = 1;
		}
	}
	
	//记录用户选择的规格
	$scope.specificationItems={};
	$scope.selectSpecification = function(key,value){
		$scope.specificationItems[key] = value;
		searchSku();
	}
	
	//判断用户是否选中该规格
	$scope.isSelectSpecification = function(key,value){
		
		if($scope.specificationItems[key]!=''){
			if($scope.specificationItems[key]==value){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	$scope.sku = {};
	
	$scope.loadSku = function(){
		$scope.sku = skuList[0];
		$scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
		
	}
	
	//选择规格更新SKU
	
	matchObject = function(map1,map2){
		
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}			
		}
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}			
		}

		
		return true;
	}
	
	searchSku = function(){
		
		for(var i=0;i<skuList.length;i++){
			if(matchObject($scope.specificationItems,skuList[i].spec)){
				$scope.sku = skuList[i];
				return ;
			}
		}
		$scope.sku={id:0,title:'--------',price:0};//如果没有匹配的
	}
	
});