package com.springboot.model.vo.user;

import com.springboot.model.entity.user.AuthAssistant;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AuthAssistantVO implements Serializable {

    private Long id;

    private String username;

    private String passwordHash;

    private String bindStudentCode;

    private String realName;

    private String classCode;

    private Date createdAt;

    private Date updatedAt;

    public static AuthAssistantVO objToVo(AuthAssistant entity) {
        if (entity == null) {
            return null;
        }
        AuthAssistantVO vo = new AuthAssistantVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static AuthAssistant voToObj(AuthAssistantVO vo) {
        if (vo == null) {
            return null;
        }
        AuthAssistant entity = new AuthAssistant();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<AuthAssistantVO> objToVo(List<AuthAssistant> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(AuthAssistantVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
