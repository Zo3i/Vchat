package com.jo.controller.interceptor;

import com.jo.utils.JSONResult;
import com.jo.utils.JsonUtils;
import com.jo.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class MyInterceptor implements HandlerInterceptor {
    @Autowired
    RedisOperator redis;
    //redis的key
	public static final String USER_REDIS_SESSION ="user_redis_session";
    /**
     * @Desciption: 调用controller之前,拦截请求
     * @version:v-1.00
     * @return: true(放行), false(拦截)
     * @author:张琪灵
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        String userToken = request.getHeader("userToken");
        System.out.println(userId);
        System.out.println(userToken);
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {

            String uniqueToken = redis.get(USER_REDIS_SESSION+ ":" + userId);

            if (StringUtils.isBlank(uniqueToken) && StringUtils.isEmpty(uniqueToken)) {
                System.out.println("请登录");
                returnErrorResponse(response,JSONResult.errorMsg("请登录"));
                return false;
            } else {
                if (!uniqueToken.equals(userToken)) {
                    System.out.println("账号在别的客户端登录!");
                    returnErrorResponse(response, JSONResult.errorMsg("账号被挤出.."));
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            returnErrorResponse(response,JSONResult.errorMsg("请登录"));
            return false;
        }

    }

    /**
     * @Desciption: 请求controller之后,渲染之前,拦截请求
     * @version:v-1.00
     * @return: true(放行), false(拦截)
     * @author:张琪灵
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * @Desciption: controller之后,渲染之后,执行
     * @version:v-1.00
     * @return: true(放行), false(拦截)
     * @author:张琪灵
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    public void returnErrorResponse(HttpServletResponse response, JSONResult result)
            throws IOException {
        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("tetx/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally {
            if(out == null) {
                out.close();
            }
        }

    }
}
