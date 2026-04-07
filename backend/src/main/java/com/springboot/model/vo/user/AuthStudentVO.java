package com.springboot.model.vo.user;

import com.springboot.model.entity.user.AuthStudent;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AuthStudentVO implements Serializable {

    private Long id;

    private String studentCode;

    private String studentName;

    private String passwordMd5;

    private String classCode;

    private String remark;

    private Integer accountStatus;

    private Integer loginFailCount;

    private String lastLoginIp;

    private Date createdAt;

    private Date updatedAt;

    public static AuthStudentVO objToVo(AuthStudent entity) {
        if (entity == null) {
            return null;
        }
        AuthStudentVO vo = new AuthStudentVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static AuthStudent voToObj(AuthStudentVO vo) {
        if (vo == null) {
            return null;
        }
        AuthStudent entity = new AuthStudent();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<AuthStudentVO> objToVo(List<AuthStudent> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(AuthStudentVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
