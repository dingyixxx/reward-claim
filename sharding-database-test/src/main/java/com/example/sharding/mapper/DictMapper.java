package com.example.sharding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sharding.entity.Dict;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface DictMapper extends BaseMapper<Dict> {
    /**
     * 插入字典（禁用主键回填）
     */
    @Insert("INSERT INTO t_dict (dict_code, dict_name, dict_value, remark, create_time) " +
            "VALUES (#{dictCode}, #{dictName}, #{dictValue}, #{remark}, NOW())")
    @Options(useGeneratedKeys = false)  // ✅ 禁用主键回填
    int insertDict(Dict dict);

    /**
     * 根据字典编码查询
     */
    @Select("SELECT * FROM t_dict WHERE dict_code = #{dictCode}")
    Dict selectByDictCode(@Param("dictCode") String dictCode);

    /**
     * 根据字典名称模糊查询
     */
    @Select("SELECT * FROM t_dict WHERE dict_name LIKE CONCAT('%', #{dictName}, '%')")
    List<Dict> selectByDictName(@Param("dictName") String dictName);
}
