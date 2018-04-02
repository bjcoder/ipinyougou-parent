 //控制层 
app.controller('userController' ,function($scope ,userService){





	$scope.showName=function(){
			userService.showName().success(
				function(response){
					// $cookies.username=response.name
					$scope.name=response
				}
			);
		}
});	
