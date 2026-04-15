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

    /**
     * id
     */
    private Long id;

    /**
     * 学号
     */
    private String studentCode;

    /**
     * 姓名
     */
    private String studentName;

    /**
     * 班级编号
     */
    private String classCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 账号状态
     */
    private Integer accountStatus;

    /**
     * 登录失败次数
     */
    private Integer loginFailCount;

    /**
     * 最后登录时间
     */
    private String lastLoginIp;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
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
