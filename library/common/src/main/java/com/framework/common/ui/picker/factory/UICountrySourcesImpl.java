package com.framework.common.ui.picker.factory;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.common.bean.UICountryNode;
import com.framework.common.bean.UILetterNode;
import com.framework.common.util.LettersUtils;
import com.framework.core.UIFramework;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author create by Zhengzelong on 2023-04-04
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UICountrySourcesImpl implements UICountrySources {
    public static final String ASSETS_FILE_NAME = "zh_city_cn.json";
    public static final int TYPE_INVALID = 0;   // 无效
    public static final int TYPE_COUNTRY = 1;   // 国家(中国)
    public static final int TYPE_PROVINCE = 2;  // 省份(广东省)
    public static final int TYPE_CITY = 3;      // 市区(深圳市)
    public static final int TYPE_AREA = 4;      // 地区(福田区)

    public static int getTypeByTreeId(@NonNull String treeId) {
        if (TextUtils.isEmpty(treeId)) {
            return TYPE_INVALID;
        }
        final String[] treeIdArgs;
        treeIdArgs = treeId.trim().split(String.valueOf('-'));
        switch (treeIdArgs.length - 1) { // '-' 数量
            case 0:
                return TYPE_COUNTRY;
            case 1:
                return TYPE_PROVINCE;
            case 2:
                return TYPE_CITY;
            case 3:
                return TYPE_AREA;
            default:
                return TYPE_INVALID;
        }
    }

    @NonNull
    public static JsonElement read(@NonNull Context context) throws
            IOException,
            JsonIOException,
            JsonSyntaxException {
        final InputStream inputStream;
        inputStream = context.getAssets().open(ASSETS_FILE_NAME);
        return new JsonParser().parse(new InputStreamReader(inputStream));
    }

    @NonNull
    private final Gson gson = new Gson();

    @NonNull
    @Override
    public List<UILetterNode> queryLetterList() {
        JsonElement jsonElement = null;
        try {
            jsonElement = read(UIFramework.getApplicationContext());
        } catch (@NonNull Exception e) {
            e.printStackTrace();
        }
        if (jsonElement == null) {
            return Collections.emptyList();
        }
        final JsonArray jsonArray = jsonElement.getAsJsonArray();
        final Map<String, List<UICountryNode>> map = new HashMap<>();
        UICountryNode childCountry;
        for (final JsonElement element : jsonArray) {
            childCountry = this.fromJson(element, UICountryNode.class);
            if (!this.isCityNode(childCountry)) {
                continue;
            }
            final String letterName;
            letterName = LettersUtils.letterFirst(childCountry.getName());
            List<UICountryNode> list = map.get(letterName);
            if (list == null) {
                list = new ArrayList<>();
                map.put(letterName, list);
            }
            list.add(childCountry);
        }
        if (map.isEmpty()) {
            return Collections.emptyList();
        }
        final ArrayList<UILetterNode> letterList = new ArrayList<>();
        for (final String letterName : map.keySet()) {
            List<UICountryNode> list = map.get(letterName);
            if (list == null) {
                list = Collections.EMPTY_LIST;
            }
            final UILetterNode letter = new UILetterNode();
            letter.setName(letterName);
            letter.setDownstream(list);
            letterList.add(letter);
            Collections.sort(list, CityComparator.INSTANCE);
        }
        Collections.sort(letterList, LetterComparator.INSTANCE);
        return letterList;
    }

    @NonNull
    @Override
    public List<UICountryNode> queryCountryList() {
        JsonElement jsonElement = null;
        try {
            jsonElement = read(UIFramework.getApplicationContext());
        } catch (@NonNull Exception e) {
            e.printStackTrace();
        }
        if (jsonElement == null) {
            return Collections.emptyList();
        }
        final JsonArray jsonArray = jsonElement.getAsJsonArray();
        final UICountryNode linkedCountry = new UICountryNode();
        UICountryNode parentCountry = null;
        UICountryNode childCountry;
        for (final JsonElement element : jsonArray) {
            childCountry = this.fromJson(element, UICountryNode.class);
            if (parentCountry != null) {
                final String o1 = parentCountry.getTreeID();
                final String o2 = parentCountry.getParent();
                if (!Objects.equals(o1, o2)) {
                    parentCountry = null;
                }
            }
            if (parentCountry == null) {
                parentCountry = this.findParent(linkedCountry, childCountry);
            }
            if (parentCountry == null) {
                this.addToParent(linkedCountry, childCountry);
            } else {
                this.addToParent(parentCountry, childCountry);
            }
        }
        return linkedCountry.getDownstream();
    }

    @NonNull
    public <T> T fromJson(@NonNull JsonElement json,
                          @NonNull Class<T> classOfT) throws JsonSyntaxException {
        return this.gson.fromJson(json, classOfT);
    }

    @Nullable
    private UICountryNode findParent(@NonNull UICountryNode parentCountry,
                                     @NonNull UICountryNode childCountry) {
        final String o1 = parentCountry.getTreeID();
        final String o2 = childCountry.getParent();
        if (Objects.equals(o1, o2)) {
            return parentCountry;
        }
        final List<UICountryNode> downstream = parentCountry.getDownstream();
        if (downstream.isEmpty()) {
            return null;
        }
        for (final UICountryNode next : downstream) {
            parentCountry = this.findParent(next, childCountry);
            if (parentCountry != null) {
                break;
            }
        }
        return parentCountry;
    }

    private void addToParent(@NonNull UICountryNode parentCountry,
                             @NonNull UICountryNode childCountry) {
        if (!TextUtils.isEmpty(parentCountry.getTreeID())) {
            childCountry.setUpstream(parentCountry);
        }
        final List<UICountryNode> downstream = parentCountry.getDownstream();
        downstream.add(childCountry);
    }

    // 直辖市(4个)/行政区(2个)
    private boolean isCityNode(@NonNull UICountryNode countryNode) {
        final String treeId = countryNode.getTreeID();
        return TYPE_CITY == getTypeByTreeId(treeId)
                || "1-1".equals(treeId)
                || "1-2".equals(treeId)
                || "1-9".equals(treeId)
                || "1-22".equals(treeId)
                || "1-33".equals(treeId)
                || "1-34".equals(treeId);
    }

    public static class CityComparator implements Comparator<UICountryNode> {
        static final Comparator<UICountryNode> INSTANCE = new CityComparator();

        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(@NonNull UICountryNode o1, @NonNull UICountryNode o2) {
            final String name1 = o1.getName();
            final String name2 = o2.getName();
            final CollationKey key1 = this.getCollationKey(name1);
            final CollationKey key2 = this.getCollationKey(name2);
            return key1.compareTo(key2);
        }

        @NonNull
        private CollationKey getCollationKey(@NonNull String source) {
            return this.collator.getCollationKey(source);
        }
    }

    public static class LetterComparator implements Comparator<UILetterNode> {
        static final Comparator<UILetterNode> INSTANCE = new LetterComparator();

        @Override
        public int compare(@NonNull UILetterNode o1, @NonNull UILetterNode o2) {
            final char char1 = o1.getName().charAt(0);
            final char char2 = o2.getName().charAt(0);
            return char1 - char2;
        }
    }
}
