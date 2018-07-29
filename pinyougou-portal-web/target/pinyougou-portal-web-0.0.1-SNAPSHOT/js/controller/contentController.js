app.controller('contentController',function($scope,contentService){
	alert(1);
	$scope.contentList = [];
	$scope.findByCategoryId = function(categoryId){
		contentService.findByCategoryId(categoryId).success(
				function(response){
					$scope.contentList[categoryId] = response;
					alert(JSON.stringify(response));
				}
		);
	}
});