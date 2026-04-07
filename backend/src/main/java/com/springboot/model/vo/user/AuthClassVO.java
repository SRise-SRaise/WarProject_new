package com.springboot.model.vo.user;

import com.springboot.model.entity.user.AuthClass;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AuthClassVO implements Serializable {

    private String classCode;

    private String headmasterName;

    private Integer classStatus;

    private Date createdAt;

    private Date updatedAt;

    public static AuthClassVO objToVo(AuthClass entity) {
        if (entity == null) {
            return null;
        }
        AuthClassVO vo = new AuthClassVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static AuthClass voToObj(AuthClassVO vo) {
        if (vo == null) {
            return null;
        }
        AuthClass entity = new AuthClass();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<AuthClassVO> objToVo(List<AuthClass> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(AuthClassVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
