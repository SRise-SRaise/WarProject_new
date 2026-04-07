package com.springboot.model.vo.homework;

import com.springboot.model.entity.homework.ResSubmissionLog;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ResSubmissionLogVO implements Serializable {

    private Long id;

    private Long resultId;

    private String contentSnapshot;

    private Date snapshotTime;

    private Date createdAt;

    public static ResSubmissionLogVO objToVo(ResSubmissionLog entity) {
        if (entity == null) {
            return null;
        }
        ResSubmissionLogVO vo = new ResSubmissionLogVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static ResSubmissionLog voToObj(ResSubmissionLogVO vo) {
        if (vo == null) {
            return null;
        }
        ResSubmissionLog entity = new ResSubmissionLog();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<ResSubmissionLogVO> objToVo(List<ResSubmissionLog> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(ResSubmissionLogVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
