package com.springboot.model.vo.common;

import com.springboot.model.entity.common.SysConfig;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SysConfigVO implements Serializable {

    private String configKey;

    private String configValue;

    private Date createdAt;

    private Date updatedAt;

    public static SysConfigVO objToVo(SysConfig entity) {
        if (entity == null) {
            return null;
        }
        SysConfigVO vo = new SysConfigVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static SysConfig voToObj(SysConfigVO vo) {
        if (vo == null) {
            return null;
        }
        SysConfig entity = new SysConfig();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<SysConfigVO> objToVo(List<SysConfig> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(SysConfigVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
