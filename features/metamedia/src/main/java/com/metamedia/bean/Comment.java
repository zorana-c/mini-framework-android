package com.metamedia.bean;

import com.framework.core.bean.UIModelInterface;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Author create by Zhengzelong on 2024-01-17
 * @Email : 171905184@qq.com
 * @Description :
 */
public class Comment implements UIModelInterface {
    @SerializedName("replyList")
    private List<Comment> replyList;

    public List<Comment> getReplyList() {
        return this.replyList;
    }

    public void setReplyList(List<Comment> replyList) {
        this.replyList = replyList;
    }
}
