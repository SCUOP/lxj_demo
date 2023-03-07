package com.scuop.locationservice.util;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户的地点信息
 */
@Data
@Schema(description = "用户的地点信息")
public class UserLocation {
    // 地点名
    @NotBlank(message = "地点不可为空")
    @Schema(description = "地点")
    private String location;
    // 时间戳
    @Max(1440)
    @Min(0)
    @Schema(description = "时间")
    private Integer time;
}
