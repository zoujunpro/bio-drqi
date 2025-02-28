package com.bio.drqi.manage.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.bio.base.api.RemoteLoginService;
import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.drqi.manage.auth.rsp.LoginRspDTO;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.LoginUser;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
@Slf4j
public class ClientLoginServiceImpl implements ClientLoginService {

    @Resource
    private RemoteLoginService remoteLoginService;

    @Resource
    private TokenService tokenService;

    @Override
    public LoginRspDTO login(String ticket) {
        ResponseResult<String> ssoCheckTicketResult = remoteLoginService.ssoCheckTicket(ticket);
        if (ssoCheckTicketResult.isError()) {
            log.error("根据凭证登录失败：{}", ssoCheckTicketResult);
            return null;
        }
        ResponseResult<UserDetailRspDTO> dataResult = remoteLoginService.getData(ssoCheckTicketResult.getData());
        if (dataResult.isError()) {
            throw new BusinessException(dataResult.getMessage());
        }
        UserDetailRspDTO userDetailRspDTO = dataResult.getData();
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(userDetailRspDTO.getId());
        loginUser.setUsername(userDetailRspDTO.getUsername());
        loginUser.setNickname(userDetailRspDTO.getNickname());
        loginUser.setJobNum(userDetailRspDTO.getJobNum());
        loginUser.setDeptId(userDetailRspDTO.getDeptId());
        loginUser.setDeptName(userDetailRspDTO.getDeptName());
        loginUser.setFeiShuUserId(userDetailRspDTO.getFeiShuUserId());
        loginUser.setFeiShuDepartmentId(userDetailRspDTO.getFeiShuDepartmentId());
        loginUser.setRoleList(JSONUtil.toList(JSONUtil.toJsonStr(userDetailRspDTO.getRoleList()), LoginUser.Role.class));
        loginUser.setSystemList(JSONUtil.toList(JSONUtil.toJsonStr(userDetailRspDTO.getSystemList()), LoginUser.System.class));
        loginUser.setPermissionsList(JSONUtil.toList(JSONUtil.toJsonStr(userDetailRspDTO.getPermissionsList()), LoginUser.Permissions.class));
        loginUser.setManager(JSONUtil.toBean(JSONUtil.toJsonStr(userDetailRspDTO.getManager()), LoginUser.Manager.class));
        Map<String, Object> map = tokenService.createToken(loginUser);
        LoginRspDTO loginRspDTO = new LoginRspDTO();
        BeanUtil.copyProperties(map, loginRspDTO);
        return loginRspDTO;
    }

    @Override
    public UserDetailRspDTO data() {
        LoginUser loginUser = tokenService.getLoginUser();
        UserDetailRspDTO userDetailRspDTO = new UserDetailRspDTO();
        userDetailRspDTO.setId(loginUser.getUserId());
        userDetailRspDTO.setUsername(loginUser.getUsername());
        userDetailRspDTO.setNickname(loginUser.getNickname());
        userDetailRspDTO.setJobNum(loginUser.getJobNum());
        userDetailRspDTO.setSystemList(JSONUtil.toList(JSONUtil.toJsonStr(loginUser.getRoleList()), UserDetailRspDTO.System.class));
        userDetailRspDTO.setRoleList(JSONUtil.toList(JSONUtil.toJsonStr(loginUser.getRoleList()), UserDetailRspDTO.Role.class));
        userDetailRspDTO.setPermissionsList(JSONUtil.toList(JSONUtil.toJsonStr(loginUser.getRoleList()), UserDetailRspDTO.Permissions.class));
        userDetailRspDTO.setManager(JSONUtil.toBean(JSONUtil.toJsonStr(loginUser.getManager()), UserDetailRspDTO.Manager.class));
        return userDetailRspDTO;
    }

    @Override
    public void logout() {
        remoteLoginService.logout(SecurityContextHolder.getUserName(), "PC");
    }

    @Override
    public void logoutCall(String appId) {
        tokenService.delSessionToken(appId);
    }
}
