 //控制层 
app.controller('contentController' ,function($scope,$controller   ,contentService ){
	


	$scope.contentList=[]
   $scope.findByCategoryId=function(categoryId){
   		contentService.findByCategoryId(categoryId).success(
   			function(response){
				$scope.contentList[categoryId]=response
   			}
   		);
   	}

   	$scope.toSearch=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords
    }
});	
