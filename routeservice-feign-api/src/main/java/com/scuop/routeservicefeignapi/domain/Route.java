package com.scuop.routeservicefeignapi.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

// 路线
@Data
public class Route {
    private Long id;
    // 路线名
    private String routeName;
    // 路线属于的用户id
    private Long userId;
    // 路线开始时间 格式为yyyy-MM-DD HH:mm:ss如 2023-01-15 12:30:00 表示2023年一月15日十二点半
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    // 路线结束时间 格式同上
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    // 路线的概述
    private String overview;

    // 列名
    public static final String ID = "id";
    public static final String ROUTENAME = "route_name";
    public static final String USERID = "user_id";
    public static final String STRATTIME = "start_time";
    public static final String ENDTIME = "end_time";
    public static final String OVERVIEW = "overview";
}
