/**
 * Created by a2363196581 on 2018/3/9.
 */
app.service("loginService",function ($http) {
    this.showName=function () {
        return $http.get("../login/name.do")
    }
})