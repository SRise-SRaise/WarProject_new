package com.springboot.model.dto.common;

import java.io.Serializable;
import lombok.Data;

@Data
public class EduLectureUpdateRequest implements Serializable {

    private Long id;

    private String lectureName;

    private Integer categoryId;

    private String fileExtension;

    /**
     * 文件路径
     */
    private String filePath;

    private static final long serialVersionUID = 1L;
}
