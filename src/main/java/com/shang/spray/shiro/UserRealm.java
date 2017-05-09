package com.shang.spray.shiro;

import com.shang.spray.entity.User;
import com.shang.spray.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * info:
 * Created by shang on 2017/4/27.
 */
public class UserRealm extends AuthorizingRealm{
    @Resource
    private UserService userService;
//    @Resource
//    private RoleService roleService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String currentLoginName = (String)principals.getPrimaryPrincipal();
        List<String> userRoles = new ArrayList<>();
        List<String> userPermissions = new ArrayList<>();
        //从数据库中获取当前登录用户的详细信息
//        User user = userService.findByUsername(currentLoginName);
//        if(null != user){
//            //获取当前用户下所有ACL权限列表 待续。。。
//            //获取当前用户下拥有的所有角色列表
//            List<Role> roles = roleService.findByUserId(user.getId());
//            for (int i = 0; i < roles.size(); i++) {
//                userRoles.add(roles.get(i).getCode());
//            }
//        }
        //为当前用户设置角色和权限
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addRoles(userRoles);
        authorizationInfo.addStringPermissions(userPermissions);
        return authorizationInfo;
    }

    /** * 验证当前登录的Subject * LoginController.login()方法中执行Subject.login()时 执行此方法 */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authcToken) throws AuthenticationException {
        String username = (String)authcToken.getPrincipal();
        User user = userService.findByUsername(username);
        if(user == null) {
            throw new UnknownAccountException();//没找到帐号
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getUsername(), //用户名
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getPassword()),//salt=username+salt,采用明文访问时，不需要此句
                getName()  //realm name
        );
        return authenticationInfo;
    }


}
