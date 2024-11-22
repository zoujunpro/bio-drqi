package com.bio.drqi.applet.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
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
import com.bio.drqi.domain.BioAppletLoginTb;
import com.bio.drqi.mapper.BioAppletLoginTbMapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Resource
    private BioAppletLoginTbMapper bioAppletLoginTbMapper;

    @Resource
    private RemoteUserService remoteUserService;


    @Resource
    private TokenService tokenService;

    @Resource
    private WxMaService wxMaService;

    @Override
    public LoginRspDTO login(WxLoginReqDTO wxLoginReqDTO) {

        if (!wxMaService.switchover(wxLoginReqDTO.getAppId())) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", wxLoginReqDTO.getAppId()));
        }

        BioAppletLoginTb bioAppletLoginTb = bioAppletLoginTbMapper.selectOneByTelephone(wxLoginReqDTO.getTelephone());
        WxMaJscode2SessionResult wxMaJscode2SessionResult = null;
        try {
            wxMaJscode2SessionResult = wxMaService.getUserService().getSessionInfo(wxLoginReqDTO.getCode());
        } catch (WxErrorException e) {
            log.error("微信接口调用失败 ", e);
            throw new BusinessException("微信接口调用失败，请联系系统开发人员");
        }
        ResponseResult<UserDetailRspDTO> responseResult = remoteUserService.queryUserByTelephone(bioAppletLoginTb.getTelephone());
        if (responseResult.isError()) {
            throw new BusinessException("用户服务调用异常");
        }
        if (bioAppletLoginTb == null) {
            bioAppletLoginTb = new BioAppletLoginTb();
            bioAppletLoginTb.setAppId(wxLoginReqDTO.getAppId());
            bioAppletLoginTb.setOpenId(wxMaJscode2SessionResult.getOpenid());
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
        loginUser.setClientId(wxLoginReqDTO.getAppId());
        Map<String, Object> map = tokenService.createToken(loginUser);
        LoginRspDTO loginRspDTO = new LoginRspDTO();
        BeanUtil.copyProperties(map, loginRspDTO);
        return loginRspDTO;
    }
}
