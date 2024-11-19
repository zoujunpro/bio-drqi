package com.bio.drqi.applet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.bio.base.api.RemoteUserService;
import com.bio.base.base.LoginRspDTO;
import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.LoginUser;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.service.TokenService;
import com.bio.drqi.applet.dto.req.WxLoginReqDTO;
import com.bio.drqi.applet.service.LoginService;
import com.bio.drqi.applet.wx.WeChatProperties;
import com.bio.drqi.applet.wx.WeChatService;
import com.bio.drqi.applet.wx.dto.JsCode2sessionRspDTO;
import com.bio.drqi.domain.BioAppletLoginTb;
import com.bio.drqi.mapper.BioAppletLoginTbMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private BioAppletLoginTbMapper bioAppletLoginTbMapper;

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private WeChatService weChatService;

    @Resource
    private WeChatProperties weChatProperties;

    @Resource
    private TokenService tokenService;




    @Override
    public LoginRspDTO login(WxLoginReqDTO wxLoginReqDTO) {
        BioAppletLoginTb bioAppletLoginTb = bioAppletLoginTbMapper.selectOneByTelephone(wxLoginReqDTO.getTelephone());

        JsCode2sessionRspDTO jsCode2sessionRspDTO = weChatService.jsCode2session(wxLoginReqDTO.getCode());
        ResponseResult<UserDetailRspDTO> responseResult = remoteUserService.queryUserByTelephone(bioAppletLoginTb.getTelephone());
        if (responseResult.isError()) {
            throw new BusinessException("用户服务调用异常");
        }
        if (bioAppletLoginTb == null) {
            bioAppletLoginTb = new BioAppletLoginTb();
            bioAppletLoginTb.setAppId(weChatProperties.getAppId());
            bioAppletLoginTb.setOpenId(jsCode2sessionRspDTO.getOpenid());
            bioAppletLoginTb.setTelephone(wxLoginReqDTO.getTelephone());
            bioAppletLoginTb.setJobNum(responseResult.getData().getJobNum());
            bioAppletLoginTb.setCreateTime(new Date());
            bioAppletLoginTbMapper.insert(bioAppletLoginTb);
        }
        UserDetailRspDTO userDetailRspDTO = responseResult.getData();
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
        loginUser.setClientId("wechat");
        Map<String, Object> map = tokenService.createToken(loginUser);
        LoginRspDTO loginRspDTO = new LoginRspDTO();
        BeanUtil.copyProperties(map, loginRspDTO);
        return loginRspDTO;
    }
}
