package com.springboot.model.dto.common;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EduLectureQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String lectureName;

    private Integer categoryId;

    private String fileExtension;

    private String filePath;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
