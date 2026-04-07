package com.springboot.model.dto.common;

import java.io.Serializable;
import lombok.Data;

@Data
public class EduLectureAddRequest implements Serializable {

    private String lectureName;

    private Integer categoryId;

    private String fileExtension;

    private static final long serialVersionUID = 1L;
}
