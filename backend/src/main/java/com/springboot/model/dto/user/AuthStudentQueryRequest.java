package com.springboot.model.dto.user;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthStudentQueryRequest extends PageRequest implements Serializable {

    private Long id;

    /** 学号，精确匹配；不传或空则不参与条件 */
    private String studentCode;

    /** 姓名，模糊匹配（LIKE）；不传或空则不参与条件 */
    private String studentName;

    /** 班级编号，精确匹配；不传或空则不参与条件 */
    private String classCode;

    /** 账号状态，精确匹配（如 0 正常）；不传则不参与条件 */
    private Integer accountStatus;

    private Integer loginFailCount;

    private String lastLoginIp;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
