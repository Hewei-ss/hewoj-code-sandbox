package com.hew.hewojcodesandbox.controller;


import com.hew.hewojcodesandbox.JavaDockerCodesandbox;
import com.hew.hewojcodesandbox.JavaNativeACMCodesandbox;
import com.hew.hewojcodesandbox.JavaNativeCodesandbox;
import com.hew.hewojcodesandbox.model.ExecuteCodeRequest;
import com.hew.hewojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController("/")
public class MainController {
    @GetMapping("/test")
    public String test() {
        return "ok";
    }

    @Resource
    private JavaDockerCodesandbox javaDockerCodesandbox;


    @Resource
    JavaNativeACMCodesandbox javaNativeACMCodesandbox;

    @Resource
    private JavaNativeCodesandbox javaNativeCodesandbox;

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";
    private static final String AUTH_REQUEST_SECRET = "secretKey";


    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    @PostMapping("/executeCode")
    ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 基本的认证
        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        if (!AUTH_REQUEST_SECRET.equals(authHeader)) {
            response.setStatus(403);
            return null;
        }
        if (executeCodeRequest == null) {
            throw new RuntimeException("请求参数为空");
        }
      //   return javaDockerCodesandbox.executeCode(executeCodeRequest);
        return javaNativeACMCodesandbox.executeCode(executeCodeRequest);
    }

}
