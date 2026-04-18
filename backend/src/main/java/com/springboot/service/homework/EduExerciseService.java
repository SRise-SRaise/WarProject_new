package com.springboot.service.homework;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.homework.EduExerciseQueryRequest;
import com.springboot.model.dto.homework.ExercisePublishRequest;
import com.springboot.model.dto.homework.StudentExerciseQueryRequest;
import com.springboot.model.entity.homework.EduExercise;
import com.springboot.model.vo.homework.EduExerciseVO;
import com.springboot.model.vo.homework.StudentExerciseVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface EduExerciseService extends IService<EduExercise> {

    void validEduExercise(EduExercise eduExercise, boolean add);

    QueryWrapper<EduExercise> getQueryWrapper(EduExerciseQueryRequest queryRequest);

    EduExerciseVO getEduExerciseVO(EduExercise eduExercise, HttpServletRequest request);

    Page<EduExerciseVO> getEduExerciseVOPage(Page<EduExercise> entityPage, HttpServletRequest request);

    /**
     * 发布作业到指定班级
     * @param publishRequest 发布请求
     * @return 是否成功
     */
    Boolean publishExercise(ExercisePublishRequest publishRequest);

    /**
     * 保存布置草稿（不发布）
     * @param publishRequest 布置草稿参数
     * @return 是否成功
     */
    Boolean saveDraftAssign(ExercisePublishRequest publishRequest);

    /**
     * 关闭作业
     * @param exerciseId 作业ID
     * @return 是否成功
     */
    Boolean closeExercise(Long exerciseId);

    /**
     * 学生查询可见作业列表
     * @param queryRequest 查询请求
     * @return 作业列表
     */
    List<StudentExerciseVO> listForStudent(StudentExerciseQueryRequest queryRequest);
}
