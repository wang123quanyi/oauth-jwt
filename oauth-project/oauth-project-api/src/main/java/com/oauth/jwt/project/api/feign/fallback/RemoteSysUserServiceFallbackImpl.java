package com.oauth.jwt.project.api.feign.fallback;

import com.oauth.jwt.common.core.util.R;
import com.oauth.jwt.project.api.entity.UserDetailsInfo;
import com.oauth.jwt.project.api.feign.RemoteSysUserService;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RemoteSysUserServiceFallbackImpl implements RemoteSysUserService {
    @Setter
    private Throwable cause;

    @Override
    public R<UserDetailsInfo> loadUserByUsername(String userName) {
        log.error("feign 根据用户名返回信息失败cause:{}, userName:{}", cause, userName);
//        throw new RuntimeException(); //排查异常被捕获时使用
        return R.failed();
    }

    @Override
    public R update() {
        log.error("feign 测试失败cause:{}", cause);
        return R.failed();
    }
}
