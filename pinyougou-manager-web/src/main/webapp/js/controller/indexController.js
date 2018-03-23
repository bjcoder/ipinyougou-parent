/**
 * Created by a2363196581 on 2018/3/9.
 */
app.controller("indexController",function ($scope,$controller,loginService) {
    $scope.showName=function(){
        loginService.showName().success(
    			function(response){
    				$scope.loginName=response.loginName;
    			}			
    		);
    	}
    
})