app.controller('searchController',function($scope,$location,searchService){
	
	
	$scope.searchMap = {'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':'1',
			'pageSize':'20','sortField':'','sort':''};
	
	//添加搜索选项
	$scope.addSearchItem = function(key,value){
		if(key=="category"||key=="brand"||key=="price"){ //如果点击的是分类或者品牌
			$scope.searchMap[key] = value;
		}else{
			$scope.searchMap.spec[key] = value;
		}
		
		$scope.search();
	}
	
	//移除搜索选项
	$scope.removeSearchItem = function(key){
		if(key=="category"||key=="brand"||key=="price"){ //如果点击的是分类或者品牌
			$scope.searchMap[key] = "";
		}else{
			delete $scope.searchMap.spec[key];
		}
		$scope.search();
	}
	
	$scope.search = function(){
		$scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
		$scope.searchMap.pageSize = parseInt($scope.searchMap.pageSize);
		searchService.search($scope.searchMap).success(
				function(response){
					$scope.resultMap = response;
					//构建分页标签
					buildPageLabel();
				}
		);
	}
	
	//构建分页标签
	
	buildPageLabel = function(){
		
		$scope.pageLabel=[]; 
		
		var maxPageNo = $scope.resultMap.totalPages;//得到最后的页码
		var startPage = 1; //开始页码
		var endPage = maxPageNo; //结束页码
		if( maxPageNo>5){  //如果总页数大于5页 显示部分页码
			
			if($scope.searchMap.pageNo<3){ //如果当前页小于3
				endPage = 5;
			}else if($scope.searchMap.pageNo>=endPage-2){ //如果当前页大于总页数-2
				startPage = maxPageNo - 4;
			}else{ //显示以当前页为中心的5页
				startPage = $scope.searchMap.pageNo-2;
				endPage = $scope.searchMap.pageNo+2;
			}
		}
		
		for(var i=startPage;i<=endPage;i++){
			$scope.pageLabel.push(i);
		}
		
	}
	
	//根据输入的页数来查询
	$scope.queryByPage = function(pageNo){
		
		if(pageNo<1||pageNo>$scope.resultMap.totalPages){
			return;
		}
		
		$scope.searchMap.pageNo = pageNo;
		$scope.search();
		$scope.pageNum = null;
	}
	//排序
	$scope.sortSearch = function(sort,sortField){
		$scope.searchMap.sort = sort;
		$scope.searchMap.sortField = sortField;
		$scope.search();
	}
	
	//判断关键字是不是品牌
	$scope.keywordsIsBrand = function(){
		
		for(var i=0;i<$scope.resultMap.brandList.length;i++){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
				return true;
			}
		}
		return false;
	}
	
	//接收搜索页的关键字
	$scope.receiveKeywords = function(){
		$scope.searchMap.keywords = $location.search()['keywords'];
		$scope.search();
	}
});