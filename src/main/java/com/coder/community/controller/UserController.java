package com.coder.community.controller;

import com.coder.community.annocation.LoginRequired;
import com.coder.community.entity.User;
import com.coder.community.service.FollowService;
import com.coder.community.service.LikeService;
import com.coder.community.service.UserService;
import com.coder.community.utils.CommunityConstant;
import com.coder.community.utils.CommunityUtil;
import com.coder.community.utils.CookieUtil;
import com.coder.community.utils.HostHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private HostHandler hostHandler;
    @Autowired
    private FollowService followService;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private LikeService likeService;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
//        如果没有选择图片，就返回错误信息
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }
//        获取图片的原始名字
        String filename = headerImage.getOriginalFilename();
//        获取图片的后缀名
        String suffix = filename.substring(filename.lastIndexOf("."));
        filename = CommunityUtil.generateUUID() + suffix;

//        确定文件存放的路径
        File desc = new File(uploadPath + "/" + filename);
        try {
//            存储文件
            headerImage.transferTo(desc);
        } catch (IOException e) {
            logger.error("上传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常", e);
        }
//        更新当前用户的头像的路径(web访问路径)
//        http://localhost:8080/community/user/header/xxx.png
        User user = hostHandler.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @GetMapping("/header/{filename}")
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
//        服务器存放的路径
        filename = uploadPath + "/" + filename;
//        文件的后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
//        响应图片
        response.setContentType("image/" + suffix);
        try (FileInputStream fis = new FileInputStream(filename); OutputStream os = response.getOutputStream();) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
        }

    }
    @PostMapping("/updatePassword")
    public String updatePassword(String oldPassword, String newPassword, String confirmPassword, Model model, HttpServletRequest httpServletRequest) {

        User user = hostHandler.getUser();

        if (oldPassword == null) {
            model.addAttribute("oldMsg", "密码不能为空");
            return "site/setting";
        }
        if (newPassword == null) {
            model.addAttribute("newMsg", "密码不能为空");
            return "site/setting";
        }
        if (confirmPassword == null) {
            model.addAttribute("confirmMsg", "密码不能为空");
            return "site/setting";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("confirmMsg", "两次输入的密码不一致");
            return "/site/setting";
        }
//        打印原密码和数据库中的密码
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());

        if (!oldPassword.equals(user.getPassword())) {
            model.addAttribute("oldMsg", "原密码错误");
            return "/site/setting";
        }
        newPassword= CommunityUtil.md5(newPassword + user.getSalt());
        userService.updatePassword(user.getId(), newPassword);
//        取消登录状态
        String ticket = CookieUtil.getValue(httpServletRequest, "ticket");
        userService.logout(ticket);

        return "redirect:/login";
    }
//    获取个人主页
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
//        用户
        model.addAttribute("user", user);
//        点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
//        关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);

//        粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
//        是否已关注
        boolean hasFollowed = false;
        if (hostHandler.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHandler.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }
}
