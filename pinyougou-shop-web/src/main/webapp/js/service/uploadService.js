/**
 * Created by a2363196581 on 2018/3/11.
 */
app.service("uploadService",function ($http) {
    this.uploadFile=function () {
        var formData=new FormData();
        formData.append("file",file.files[0])
        return $http({
            method:'POST',
            url:"../uploadFile.do",
            data: formData,
            headers: {'Content-Type':undefined},
            transformRequest: angular.identity
        });
    }
})