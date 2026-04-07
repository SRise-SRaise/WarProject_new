package com.springboot.model.vo.user;

import com.springboot.model.entity.user.SysStudentLog;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SysStudentLogVO implements Serializable {

    private Long id;

    private String account;

    private Integer actionType;

    private String actionDetail;

    private Date opTime;

    private String clientIp;

    private Date createdAt;

    public static SysStudentLogVO objToVo(SysStudentLog entity) {
        if (entity == null) {
            return null;
        }
        SysStudentLogVO vo = new SysStudentLogVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static SysStudentLog voToObj(SysStudentLogVO vo) {
        if (vo == null) {
            return null;
        }
        SysStudentLog entity = new SysStudentLog();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<SysStudentLogVO> objToVo(List<SysStudentLog> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(SysStudentLogVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
