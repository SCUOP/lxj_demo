package com.scuop.imgservice.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.scuop.imgservice.util.PicSource;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping // (img)
public class IMGController {

    // 便于分类
    @Autowired
    private PicSource picSource;

    // 注入配置中图片保存路径
    @Value("${path.filepath}")
    private String filePath;

    // 注入配置中的图片访问路径
    @Value("${path.accesspath}")
    private String accessPath;

    // 注入配置中的url
    @Value("${path.url}")
    private String url;

    // 注入项目contextpath
    @Value("${server.servlet.context-path}")
    private String contextPath;

    // 处理上传图片请求的方法
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "上传图片", description = "上传图片并返回图片路径,有user和route两种类型,如果为route类型需传参routeId")
    public SaResult upload(
            @RequestPart("pic") MultipartFile multipartFile,
            @RequestParam("picsrc") String picSrc,
            @RequestParam(value = "route_id", required = false) Long routeId)
            throws IOException {

        int picSrcType = picSource.existPicSource(picSrc);
        // 检查图片源是否符合要求
        if (picSrcType == -1)
            return SaResult.error("数据错误,请检查");
        else if (picSrcType == 0 && routeId == null)
            return SaResult.error("请传入路线id");
        // 以用户id建立该用户的图片文件夹
        Long userId = StpUtil.getLoginIdAsLong();
        // 生成一个随机的名称，避免文件名重复
        UUID uuid = UUID.randomUUID();
        // 获取原文件名称
        String originalFileName = multipartFile.getOriginalFilename();
        // 获取原文件的后缀
        String fileSuffix = originalFileName.substring(originalFileName.lastIndexOf('.'));
        // 重组文件名
        String filename = uuid + fileSuffix;
        // route类型
        if (picSrcType == 0) {
            filename = routeId + "_" + filename;
        }
        // 保存文件
        File file = new File(filePath + "/" + userId + "/" + picSrc + "/" + filename);
        // 创建文件夹
        Files.createDirectories(Paths.get(filePath + "/" + userId + "/" + picSrc));
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
            return SaResult.error("上传失败");
        }
        // 返回图片的完整访问路径
        return SaResult.data(url + contextPath + accessPath + userId + "/" + picSrc + "/" + filename);
    }

    // 删除所属用户所有的图片
    @DeleteMapping("/delAllPicOfUser")
    @Hidden
    public void delAllPicOfUser() throws IOException {
        // 设置token
        // StpUtil.setTokenValue(token);
        File file = new File(filePath + "/" + StpUtil.getLoginIdAsLong());
        FileUtils.deleteDirectory(file);
    }

    // 根据url删除一张图片
    @DeleteMapping("/delAPicByUrl")
    @Operation(summary = "根据传入的图片链接删除一张图片")
    public SaResult delAPicByUrl(@RequestParam(value = "url") URL url) {
        String[] path = url.getPath().split("/");
        File file = new File(filePath + "/" + StpUtil.getLoginIdAsLong() + "/" + path[4] + "/" + path[5]);
        return SaResult.data(file.delete());
    }

    // 删除所属用户某条路线的所有图片
    @DeleteMapping("/delARouteByRouteId")
    @Hidden
    public void delARouteByRouteId(@RequestParam(value = "route_id") Long routeId) {
        // 设置token
        // StpUtil.setTokenValue(token);
        // 不可为空
        if (routeId == null)
            return;
        // 文件夹
        File floder = new File(filePath + "/" + StpUtil.getLoginIdAsLong() + "/route");
        // 文件过滤器,筛选路径号开头的图片
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.getName().matches("^" + routeId + "_.*");
            }
        };
        // 符号条件的文件
        File[] listFiles = floder.listFiles(fileFilter);
        // 逐个删除
        for (File file : listFiles) {
            file.delete();
        }
    }
}
