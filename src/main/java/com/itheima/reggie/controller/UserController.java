package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.util.SMSUtils;
import com.itheima.reggie.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) throws Exception {
        //获取手机号
        String phone =user.getPhone();
        //生成随机的4位验证码
        if(StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            //调用阿里云短信服务完成发送短信
            SMSUtils.sendMessage("","",phone,code);

            //将验证码保存到session，等待认证
            session.setAttribute(phone,code);
            return R.success("验证码发送成功");
        }
        return R.success("验证码发送失败");

    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) throws Exception {
        //获取手机号 code
        String phone = map.get("phone").toString();
//        String code = map.get("code").toString();

        //如果能够比对成功，说明登录成功,因为没有获取验证码，所以直接放行登录
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
            user = new User() ;
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);
        }
        session.setAttribute("user",user.getId());
        return R.success(user);



        //当真正实现获取验证时 执行下面代码

        //从session中取出验证码进行验证
//        Object codeInSession =session.getAttribute(phone);

        //判断验证码是否正确
//        if(codeInSession != null && codeInSession. equals(code)) {
//            //如果能够比对成功，说明登录成功
//            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//            queryWrapper.eq(User::getPhone, phone);
//            User user = userService.getOne(queryWrapper);
//            if (user == null) {
//                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册user = new User() ;
//                user.setPhone(phone);
//                user.setStatus(1);
//                userService.save(user);
//            }
//            return R.success(user);
//        }
//        return R.error("登录失败");

    }
}