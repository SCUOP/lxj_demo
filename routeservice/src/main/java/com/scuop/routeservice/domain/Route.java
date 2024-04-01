package com.scuop.routeservice.domain;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// 路线
@Data
@Schema(description = "路线实体")
public class Route {
    // 自增主键
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "路线id")
    private Long id;
    // 路线名
    @NotBlank
    @Schema(description = "路线名")
    private String routeName;
    // 路线属于的用户id
    @Min(1)
    @Schema(description = "路线所属用户id")
    private Long userId;
    // 路线开始时间 格式为yyyy-MM-DD HH:mm:ss如 2023-01-15 12:30:00 表示2023年一月15日十二点半
    @NotBlank
    @Schema(description = "路线所属地区")
    private String area;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "路线开始时间 格式为 2023-01-15 15:00:00")
    private Date startTime;
    // 路线结束时间 格式同上
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "路线结束时间")
    private Date endTime;
    // 点赞数
    @Min(0)
    @Schema(description = "点赞数")
    private Integer likedCount;
    // 路线的概述
    @Schema(description = "路线概述")
    private String overview;

    // 列名
    public static final String ID = "id";
    public static final String ROUTENAME = "route_name";
    public static final String USERID = "user_id";
    public static final String AREA = "area";
    public static final String STRATTIME = "start_time";
    public static final String ENDTIME = "end_time";
    public static final String LIKEDCOUNT = "liked_count";
    public static final String OVERVIEW = "overview";
}
