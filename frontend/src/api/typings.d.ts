declare namespace API {
  type AuthAssistant = {
    id?: number;
    username?: string;
    passwordHash?: string;
    bindStudentCode?: string;
    realName?: string;
    classCode?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type AuthAssistantAddRequest = {
    username?: string;
    passwordHash?: string;
    bindStudentCode?: string;
    realName?: string;
    classCode?: string;
  };

  type AuthAssistantQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    username?: string;
    passwordHash?: string;
    bindStudentCode?: string;
    realName?: string;
    classCode?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type AuthAssistantUpdateRequest = {
    id?: number;
    username?: string;
    passwordHash?: string;
    bindStudentCode?: string;
    realName?: string;
    classCode?: string;
  };

  type AuthClass = {
    classCode?: string;
    headmasterName?: string;
    classStatus?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type AuthLoginVO = {
    token?: string;
    userId?: number;
    username?: string;
    role?: string;
  };

  type AuthStudent = {
    id?: number;
    studentCode?: string;
    studentName?: string;
    passwordMd5?: string;
    classCode?: string;
    remark?: string;
    accountStatus?: number;
    loginFailCount?: number;
    lastLoginIp?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type AuthStudentQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    studentCode?: string;
    studentName?: string;
    classCode?: string;
    accountStatus?: number;
  };

  type AuthStudentVO = {
    id?: number;
    studentCode?: string;
    studentName?: string;
    classCode?: string;
    className?: string;
    createdAt?: string;
  };

  type AuthTeacher = {
    id?: number;
    username?: string;
    passwordMd5?: string;
    realName?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type BaseResponse = {
    code?: number;
    data?: any;
    message?: string;
  };

  type BaseResponseBoolean = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponsePageAuthAssistant = {
    code?: number;
    data?: PageAuthAssistant_;
    message?: string;
  };

  type BaseResponsePageAuthStudent = {
    code?: number;
    data?: PageAuthStudent_;
    message?: string;
  };

  type BaseResponsePageEduExperiment = {
    code?: number;
    data?: PageEduExperiment_;
    message?: string;
  };

  type BaseResponsePageEduExperimentVO = {
    code?: number;
    data?: PageEduExperimentVO_;
    message?: string;
  };

  type deleteEduExperimentParams = {
    id?: string;
  };

  type deleteEduExperimentParams = {
    id?: string;
  };

  type EduExperiment = {
    id?: number;
    sortOrder?: number;
    name?: string;
    categoryId?: number;
    fileType?: string;
    requirement?: string;
    contentDesc?: string;
    publishStatus?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduExperimentAddRequest = {
    sortOrder?: number;
    name?: string;
    categoryId?: number;
    fileType?: string;
    requirement?: string;
    contentDesc?: string;
    publishStatus?: number;
  };

  type EduExperimentQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    name?: string;
    categoryId?: number;
    fileType?: string;
    requirement?: string;
    contentDesc?: string;
    publishStatus?: number;
  };

  type EduExperimentUpdateRequest = {
    id?: number;
    sortOrder?: number;
    name?: string;
    categoryId?: number;
    fileType?: string;
    requirement?: string;
    contentDesc?: string;
    publishStatus?: number;
  };

  type EduExperimentVO = {
    id?: number;
    sortOrder?: number;
    name?: string;
    categoryId?: number;
    fileType?: string;
    requirement?: string;
    contentDesc?: string;
    publishStatus?: number;
    categoryName?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type getEduExperimentVOByIdParams = {
    id?: string;
  };

  type Page = {
    current?: number;
    pages?: number;
    records?: any[];
    size?: number;
    total?: number;
  };

  type PageAuthAssistant_ = {
    records?: AuthAssistant[];
    total?: number;
    size?: number;
    current?: number;
    pages?: number;
  };

  type PageAuthStudent_ = {
    records?: AuthStudent[];
    total?: number;
    size?: number;
    current?: number;
    pages?: number;
  };

  type PageEduExperiment_ = {
    records?: EduExperiment[];
    total?: number;
    size?: number;
    current?: number;
    pages?: number;
  };

  type PageEduExperimentVO_ = {
    records?: EduExperimentVO[];
    total?: number;
    size?: number;
    current?: number;
    pages?: number;
  };

  // 实验项目子项相关类型
  type EduExperimentItem = {
    id?: number;
    itemNo?: number;
    itemName?: string;
    itemType?: number;
    itemContent?: string;
    experimentId?: number;
    itemAnswer?: string;
    itemScore?: number;
    status?: number;
    optionsText?: string;
    allowPaste?: boolean;
    language?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduExperimentItemAddRequest = {
    itemNo?: number;
    itemName?: string;
    itemType?: number;
    itemContent?: string;
    experimentId?: number;
    itemAnswer?: string;
    itemScore?: number;
    status?: number;
  };

  type EduExperimentItemUpdateRequest = {
    id?: number;
    itemNo?: number;
    itemName?: string;
    itemType?: number;
    itemContent?: string;
    experimentId?: number;
    itemAnswer?: string;
    itemScore?: number;
    status?: number;
  };

  type EduExperimentItemQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    itemNo?: number;
    itemName?: string;
    itemType?: number;
    experimentId?: number;
    status?: number;
  };

  type EduExperimentItemVO = {
    id?: number;
    itemNo?: number;
    itemName?: string;
    itemType?: number;
    itemContent?: string;
    experimentId?: number;
    experimentName?: string;
    itemAnswer?: string;
    itemScore?: number;
    status?: number;
    optionsText?: string;
    allowPaste?: boolean;
    language?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type PageEduExperimentItem_ = {
    records?: EduExperimentItem[];
    total?: number;
    size?: number;
    current?: number;
    pages?: number;
  };

  type PageEduExperimentItemVO_ = {
    records?: EduExperimentItemVO[];
    total?: number;
    size?: number;
    current?: number;
    pages?: number;
  };

  type BaseResponsePageEduExperimentItem = {
    code?: number;
    data?: PageEduExperimentItem_;
    message?: string;
  };

  type BaseResponsePageEduExperimentItemVO = {
    code?: number;
    data?: PageEduExperimentItemVO_;
    message?: string;
  };

  type BaseResponseEduExperimentItemVO = {
    code?: number;
    data?: EduExperimentItemVO;
    message?: string;
  };

  type deleteEduExperimentItemParams = {
    id?: string;
  };

  type getEduExperimentItemVOByIdParams = {
    id?: string;
  };

  // 实验结果相关类型
  type ResExperimentResult = {
    id?: number;
    experimentId?: number;
    studentId?: number;
    note?: string;
    reportName?: string;
    score?: number;
    status?: number;
    feedback?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResExperimentResultAddRequest = {
    experimentId?: number;
    studentId?: number;
    note?: string;
    reportName?: string;
  };

  type ResExperimentResultUpdateRequest = {
    id?: number;
    experimentId?: number;
    studentId?: number;
    note?: string;
    reportName?: string;
    score?: number;
    status?: number;
    feedback?: string;
  };

  type ResExperimentResultQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    experimentId?: number;
    studentId?: number;
    status?: number;
  };

  type ResExperimentResultVO = {
    id?: number;
    experimentId?: number;
    experimentName?: string;
    studentId?: number;
    studentName?: string;
    className?: string;
    note?: string;
    reportName?: string;
    score?: number;
    status?: number;
    feedback?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type PageResExperimentResult_ = {
    records?: ResExperimentResult[];
    total?: number;
    size?: number;
    current?: number;
    pages?: number;
  };

  type PageResExperimentResultVO_ = {
    records?: ResExperimentResultVO[];
    total?: number;
    size?: number;
    current?: number;
    pages?: number;
  };

  type BaseResponseResExperimentResult = {
    code?: number;
    data?: ResExperimentResult;
    message?: string;
  };

  type BaseResponsePageResExperimentResult = {
    code?: number;
    data?: PageResExperimentResult_;
    message?: string;
  };

  type BaseResponsePageResExperimentResultVO = {
    code?: number;
    data?: PageResExperimentResultVO_;
    message?: string;
  };
}
