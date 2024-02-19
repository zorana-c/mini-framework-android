package com.framework.demo.http.bean;

import androidx.annotation.NonNull;

import com.common.http.Req;
import com.google.gson.annotations.SerializedName;

/**
 * @Author create by Zhengzelong on 2023-07-11
 * @Email : 171905184@qq.com
 * @Description :
 */
public class ReqTestCommModel extends Req {
    @SerializedName("commId")
    private final String commId;

    public ReqTestCommModel(@NonNull String commId) {
        this.commId = commId;
    }
}
