package com.framework.common.ui.picker.bean;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * @Author create by Zhengzelong on 2023-04-04
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UICountryNode extends UINode<UICountryNode> {
    @SerializedName("name")
    private String name;
    @SerializedName("parent")
    private String parent;
    @SerializedName("treeID")
    private String treeID;
    @SerializedName("treeName")
    private String treeName;

    public String getName() {
        return this.name;
    }

    public String getParent() {
        return this.parent;
    }

    public String getTreeID() {
        return this.treeID;
    }

    public String getTreeName() {
        return this.treeName;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
