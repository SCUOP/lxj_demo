package com.scuop.userservice.domain;

import javax.validation.constraints.Pattern;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.scuop.userservice.util.RegexRule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// TODO: 也许可以加一个uid 而不暴露id
// TODO: 正则以后专门放个类写 先不改了 service和controller里面都有硬编码的正则表达式
@Data
@Schema(description = "用户实体")
public class User {
    // 自增id
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户id")
    Long id;
    // 用户名 1-12位且不能有空格
    @Pattern(regexp = RegexRule.NICKNAME, message = "不可包含空格且为1-12位")
    @Schema(description = "用户名")
    String nickname;
    // 头像url
    @Schema(description = "头像链接")
    String avatar;
    // ...拓展...

    // 对应列名
    public static final String ID = "id";
    public static final String NICKNAME = "nickname";
    public static final String AVATAR = "avatar";
}
