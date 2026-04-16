package com.springboot.service.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.common.EduLectureQueryRequest;
import com.springboot.model.entity.common.EduLecture;
import com.springboot.model.vo.common.EduLectureVO;
import jakarta.servlet.http.HttpServletRequest;

public interface EduLectureService extends IService<EduLecture> {

    void validEduLecture(EduLecture eduLecture, boolean add);

    QueryWrapper<EduLecture> getQueryWrapper(EduLectureQueryRequest queryRequest);

    EduLectureVO getEduLectureVO(EduLecture eduLecture, HttpServletRequest request);

    Page<EduLectureVO> getEduLectureVOPage(Page<EduLecture> entityPage, HttpServletRequest request);

    /**
     * 删除资料记录，并尽量同步删除关联的本地文件（根据 {@code filePath}）。
     */
    boolean removeByIdWithFile(String id);

    /**
     * 更新资料记录；若替换了文件路径，则同步删除旧文件。
     */
    boolean updateByIdWithFileCleanup(EduLecture eduLecture);
}
