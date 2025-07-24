package com.sun.utils;

import com.baidu.aip.contentcensor.AipContentCensor;
import org.json.JSONObject;

public class BaiDuArticleUtils {
    public static JSONObject detectionArticle(AipContentCensor client, String content) {
        return client.textCensorUserDefined(content);
    }
}
