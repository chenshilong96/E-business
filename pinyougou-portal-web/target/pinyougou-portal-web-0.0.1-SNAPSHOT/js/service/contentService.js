app.service('contentController',function($http){
	this.findByCategoryId =  function(categoryId){
		
		alert(1);
		return $http.get('content/findByCategoryId.do?categoryId='+categoryId);
	}
});