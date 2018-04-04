//购物车服务层
app.service('seckillGoodsService',function($http){


    //查询秒杀所有商品
    this.findList=function(){
        return $http.post('seckillgoods/findList.do');
    }



    this.findOneFromRedis=function(id){
        return $http.post('seckillgoods/findOneFromRedis.do?id='+id);
    }



    this.submitOrder=function(id){
        return $http.post('seckillgoods/submitOrder.do?seckillId='+id);
    }
});
