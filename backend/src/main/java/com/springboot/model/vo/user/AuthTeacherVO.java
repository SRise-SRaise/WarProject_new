package com.springboot.model.vo.user;

import com.springboot.model.entity.user.AuthTeacher;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AuthTeacherVO implements Serializable {

    private Long id;

    private String username;

    private String passwordMd5;

    private String realName;

    private Date createdAt;

    private Date updatedAt;

    public static AuthTeacherVO objToVo(AuthTeacher entity) {
        if (entity == null) {
            return null;
        }
        AuthTeacherVO vo = new AuthTeacherVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static AuthTeacher voToObj(AuthTeacherVO vo) {
        if (vo == null) {
            return null;
        }
        AuthTeacher entity = new AuthTeacher();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<AuthTeacherVO> objToVo(List<AuthTeacher> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(AuthTeacherVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
