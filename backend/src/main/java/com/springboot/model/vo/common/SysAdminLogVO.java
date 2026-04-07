package com.springboot.model.vo.common;

import com.springboot.model.entity.common.SysAdminLog;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SysAdminLogVO implements Serializable {

    private Long id;

    private String account;

    private Integer actionType;

    private String actionDetail;

    private Date opTime;

    private String clientIp;

    private Date createdAt;

    public static SysAdminLogVO objToVo(SysAdminLog entity) {
        if (entity == null) {
            return null;
        }
        SysAdminLogVO vo = new SysAdminLogVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static SysAdminLog voToObj(SysAdminLogVO vo) {
        if (vo == null) {
            return null;
        }
        SysAdminLog entity = new SysAdminLog();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<SysAdminLogVO> objToVo(List<SysAdminLog> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(SysAdminLogVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
