//服务层
app.service('contentService',function($http){
	    	

	//搜索
	this.findByCategoryId=function(categoryId){
		return $http.post('content/banner.do?categoryId='+categoryId);
	}    	
});
