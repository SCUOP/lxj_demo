package com.scuop.routeservice.domain;

import javax.validation.constraints.Min;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "路书点赞")
@AllArgsConstructor
@NoArgsConstructor
public class RouteLiked {
    // 自增主键
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Long id;
    // 点赞用户id
    @Min(1)
    @Schema(description = "点赞用户id")
    private Long userId;
    // 路线id
    @Min(1)
    @Schema(description = "路书id")
    private Long routeId;

    public static final String ID = "id";
    public static final String USERID = "user_id";
    public static final String ROUTEID = "route_id";
}
