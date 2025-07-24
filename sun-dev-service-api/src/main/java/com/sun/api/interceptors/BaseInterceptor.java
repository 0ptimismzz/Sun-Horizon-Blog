package com.sun.api.interceptors;

import com.sun.exception.GraceException;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseInterceptor {

    public static final String REDIS_ALREADY_READ = "redis_already_read";

    @Autowired
    public RedisOperator redisOperator;

    public boolean verifyUserIdToken(String id, String token, String redisKeyPrefix) {
        if(StringUtils.isNotBlank(id) && StringUtils.isNotBlank(token)) {
            String redisToken = redisOperator.get(redisKeyPrefix + ":" + id);
            if(StringUtils.isBlank(id)) {
                GraceException.display(ResponseStatusEnum.UN_LOGIN);
                return false;
            }else {
                if(!redisToken.equalsIgnoreCase(token)) {
                    GraceException.display(ResponseStatusEnum.TICKET_INVALID);
                    return false;
                }
            }
        }else {
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }
        return true;
    }

    public boolean verifyAdminIdToken(String id, String token, String redisKeyPrefix) {
        if(StringUtils.isNotBlank(id) && StringUtils.isNotBlank(token)) {
            String redisToken = redisOperator.get(redisKeyPrefix + ":" + id);
            if(StringUtils.isBlank(id)) {
                GraceException.display(ResponseStatusEnum.UN_LOGIN);
                return false;
            }else {
                if(!redisToken.equalsIgnoreCase(token)) {
                    GraceException.display(ResponseStatusEnum.TICKET_INVALID);
                    return false;
                }
            }
        }else {
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }
        return true;
    }
}
