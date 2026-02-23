package com.example.sharding.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_dict")
public class Dict {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String dictCode;

    private String dictName;

    private String dictValue;

    private String remark;

    private LocalDateTime createTime;
}