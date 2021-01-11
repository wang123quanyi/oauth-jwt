package com.oauth.jwt.project.api.feign;

import com.oauth.jwt.common.core.constants.ServiceNameConstants;
import com.oauth.jwt.common.core.util.R;
import com.oauth.jwt.project.api.entity.UserDetailsInfo;
import com.oauth.jwt.project.api.feign.factory.RemoteSysUserServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteSysUserService", value = ServiceNameConstants.PROJECT, fallbackFactory = RemoteSysUserServiceFallbackFactory.class)
public interface RemoteSysUserService {

    /**
     * 根据用户名返回信息
     *
     * @param userName
     * @return
     */
    @RequestMapping("/user/lubun")
    R<UserDetailsInfo> loadUserByUsername(@RequestParam("username") String userName);

    @RequestMapping("/user/update")
    R update();
}
