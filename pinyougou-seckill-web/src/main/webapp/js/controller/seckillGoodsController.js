//购物车控制层 
app.controller('seckillGoodsController',function($interval,$location,$scope,seckillGoodsService){

    $scope.findList=function(){
    		seckillGoodsService.findList().success(
    			function(response){
    				$scope.seckillGoodslist=response;
    			}
    		);
    	}



    	$scope.findOneFromRedis=function(){
    			seckillGoodsService.findOneFromRedis($location.search()['id']).success(
    				function(response){
    					$scope.seckillGood=response;
                        var allsecond= Math.floor(((new Date($scope.seckillGood.endTime).getTime())-(new Date().getTime()))/1000);
                        var time = $interval(function(){
                            if(allsecond>0){
                                allsecond= allsecond-1;
                                $scope.timeString=toTimeString(allsecond);
                            }else{
                                alert('秒杀已结束');
                                $interval.cancel(time);
                            }

                        },1000)
    				}
    			);
    		}



    toTimeString=function(allsecond){

        var days=Math.floor(allsecond/(60*60*24));
        var hours=Math.floor((allsecond-days*60*60*24)/(60*60));
        var minutes=Math.floor((allsecond-days*60*60*24-hours*60*60)/60);
        var seconds=Math.floor(allsecond-days*60*60*24-hours*60*60-minutes*60);
        timeString="";
        if(days>0){
            timeString+=days+"天";
        }

        timeString+=hours+":"+minutes+":"+seconds;

        return timeString;
    }



    $scope.submitOrder=function(){
    		seckillGoodsService.submitOrder($scope.seckillGood.id).success(
    			function(response){
    				if (response.success){
                        alert("下单成功，请在1分钟内完成支付");
                    location.href="pay.html";
                }else{
                        alert(response.message);
        }

    			}
    		);
    	}
});
