package com.springboot.service.experiment;

import java.util.List;

/**
 * 实验-班级关联服务接口
 */
public interface ExperimentClassService {

    /**
     * 绑定实验与班级（发布/更新时调用）
     * 先删除旧关联，再批量插入新关联
     *
     * @param experimentId 实验ID
     * @param classCodes   班级编号列表（必须非空，发布时必须选班级）
     */
    void bindClasses(Long experimentId, List<String> classCodes);

    /**
     * 解绑实验的所有班级关联（删除实验时级联清理）
     *
     * @param experimentId 实验ID
     */
    void unbindClasses(Long experimentId);

    /**
     * 获取某实验绑定的班级编号列表
     *
     * @param experimentId 实验ID
     * @return 班级编号列表
     */
    List<String> getClassCodesByExperiment(Long experimentId);

    /**
     * 获取某班级可访问的实验ID列表
     *
     * @param classCode 班级编号
     * @return 实验ID列表
     */
    List<Long> getExperimentIdsByClass(String classCode);

    /**
     * 获取某实验绑定的班级名称列表
     *
     * @param experimentId 实验ID
     * @return 班级名称列表（从 auth_class 查询 headmasterName 作为名称）
     */
    List<String> getClassNamesByExperiment(Long experimentId);

    /**
     * 获取某实验的班级发布数量
     *
     * @param experimentId 实验ID
     * @return 绑定的班级数量
     */
    int getClassCount(Long experimentId);
}
