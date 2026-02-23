package com.example.sharding.service;

import com.example.sharding.entity.Dict;
import com.example.sharding.mapper.DictMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@NoArgsConstructor
public class DictService {

    @Autowired
    private DictMapper dictMapper;

    /**
     * 创建字典（广播表 → 自动同步到所有分片）
     */
    public Dict createDict(String dictCode, String dictName, String dictValue) {
        Dict dict = new Dict();
        dict.setDictCode(dictCode);
        dict.setDictName(dictName);
        dict.setDictValue(dictValue);
        dict.setRemark("广播表测试");

        dictMapper.insert(dict);
        log.info("【广播表】创建字典：code={}", dictCode);
        return dict;
    }

    /**
     * 查询所有字典
     */
    public List<Dict> getAllDicts() {
        return dictMapper.selectList(null);
    }

    /**
     * 根据编码查询
     */
    public Dict getByDictCode(String dictCode) {
        return dictMapper.selectByDictCode(dictCode);
    }
}
