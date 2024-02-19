package com.framework.demo.http.service;

import com.common.http.Req;
import com.framework.core.http.HttpServiceOption;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @Author create by Zhengzelong on 2022/7/22
 * @Email : 171905184@qq.com
 * @Description :
 */
@HttpServiceOption(host = "https://blend.realbrand.net/")
public interface UserService {

    @POST(value = "/platform-management-server/Classify/queryCategoriesCatalog")
    Observable<String> queryUserById(@Body Req req);

    @POST(value = "/platform-management-server/Classify/queryCategoriesCatalog")
    Observable<String> queryUserById(@Body Map<String, Object> req);
}
