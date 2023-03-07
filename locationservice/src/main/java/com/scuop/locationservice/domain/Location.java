package com.scuop.locationservice.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "地点实体")
public class Location {
    // 自增主键
    @TableId(value = "id", type = IdType.AUTO)
    @Hidden
    private Long id;
    // 路线id
    @Min(1)
    @Schema(description = "地点所属路线id")
    private Long routeId;
    // 此时的time表示距离0点的分钟数, 例如1点钟就是60
    // 开始时间戳
    @Max(1440)
    @Min(0)
    @Schema(description = "开始时间戳")
    private Integer startTime;
    // 结束时间戳
    @Max(1440)
    @Min(0)
    @Schema(description = "结束时间戳")
    private Integer endTime;
    // TODO: 之后做校验
    // 地点名
    @NotBlank
    @Schema(description = "地点名")
    private String locationName;
    // 地点顺序
    @Schema(description = "地点的顺序")
    private Integer orderId;
    // 简介
    @Schema(description = "地点简介")
    private String description;
    // 图像链接
    @Schema(description = "地点相关图片的网址")
    private String imgUrl;
    // TODO: 添加联表查询的字段 比如后续可能有点赞数在route表里 从route表里获取

    // 对应列名
    public static final String ID = "id";
    public static final String ROUTEID = "route_id";
    public static final String STRATTIME = "start_time";
    public static final String ENDTIME = "end_time";
    public static final String LOCATIONNAME = "location_name";
    public static final String ORDERID = "order_id";
    public static final String DESCRIPTION = "description";
    public static final String IMGURL = "img_url";
}
