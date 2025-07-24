package com.sun.utils;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.face.MatchRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaiDuFaceUtils {
    public static boolean verifyUser(AipFace client,
                                    String base64DB,
                                    String tempBase64,
                                    int targetValue) throws JSONException {
        List<MatchRequest> input = new ArrayList<>();
        MatchRequest temp64 = new MatchRequest(tempBase64, "BASE64");
        MatchRequest db64 = new MatchRequest(base64DB, "BASE64");
        input.add(temp64);
        input.add(db64);
        JSONObject result = client.match(input);
        System.out.println(result);
        System.out.println(result.getJSONObject("result").get("score"));
        if ((double)result.getJSONObject("result").get("score") > targetValue) {
            return true;
        }else {
            return false;
        }

    }

}
