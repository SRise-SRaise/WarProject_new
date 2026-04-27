package com.springboot.service.experiment;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.experiment.EduExperimentQuestionQueryRequest;
import com.springboot.model.dto.experiment.DocxImportRequest;
import com.springboot.model.entity.experiment.EduExperiment;
import com.springboot.model.entity.experiment.EduExperimentQuestion;
import com.springboot.model.vo.experiment.DocxImportResult;
import com.springboot.model.vo.experiment.EduExperimentQuestionVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 实验题库服务接口
 */
public interface EduExperimentQuestionService extends IService<EduExperimentQuestion> {

    /**
     * 校验题目信息
     *
     * @param eduExperimentQuestion 题目实体
     * @param add 是否为新增操作
     */
    void validEduExperimentQuestion(EduExperimentQuestion eduExperimentQuestion, boolean add);

    /**
     * 获取查询条件构造器
     *
     * @param queryRequest 查询请求
     * @return QueryWrapper
     */
    QueryWrapper<EduExperimentQuestion> getQueryWrapper(EduExperimentQuestionQueryRequest queryRequest);

    /**
     * 获取题目视图对象
     *
     * @param eduExperimentQuestion 题目实体
     * @param request HTTP请求
     * @return EduExperimentQuestionVO
     */
    EduExperimentQuestionVO getEduExperimentQuestionVO(EduExperimentQuestion eduExperimentQuestion, HttpServletRequest request);

    /**
     * 获取题目分页视图对象
     *
     * @param entityPage 题目分页
     * @param request HTTP请求
     * @return Page<EduExperimentQuestionVO>
     */
    Page<EduExperimentQuestionVO> getEduExperimentQuestionVOPage(Page<EduExperimentQuestion> entityPage, HttpServletRequest request);

    /**
     * 根据条件随机选题
     *
     * @param questionType 题目类型
     * @param difficulty 难度等级
     * @param tag 标签
     * @param count 选取数量
     * @return 随机选取的题目列表
     */
    List<EduExperimentQuestion> selectRandomQuestions(Integer questionType, Integer difficulty, String tag, int count);

    /**
     * 批量导入题目
     *
     * @param questionList 题目列表
     * @return 是否导入成功
     */
    boolean batchImportQuestions(List<EduExperimentQuestion> questionList);

    /**
     * 从docx文档导入实验（包含实验和题目）
     *
     * @param importRequest 导入请求
     * @param experiment    实验实体（如果需要创建）
     * @return 导入结果
     */
    DocxImportResult importExperimentFromDocx(DocxImportRequest importRequest, EduExperiment experiment);

    /**
     * 从docx文档导入实验（支持直接上传文件）
     *
     * @param importRequest 导入请求
     * @param experiment    实验实体（如果需要创建）
     * @param file          上传的docx文件（优先于fileUrl）
     * @return 导入结果
     */
    DocxImportResult importExperimentFromDocx(DocxImportRequest importRequest, EduExperiment experiment, org.springframework.web.multipart.MultipartFile file);
}
