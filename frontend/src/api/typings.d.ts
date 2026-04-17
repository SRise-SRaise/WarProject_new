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

  type AuthAssistantVO = {
    id?: number;
    username?: string;
    passwordHash?: string;
    bindStudentCode?: string;
    realName?: string;
    classCode?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type AuthChangePasswordRequest = {
    oldPassword?: string;
    newPassword?: string;
    confirmPassword?: string;
  };

  type AuthClass = {
    classCode?: string;
    headmasterName?: string;
    classStatus?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type AuthClassAddRequest = {
    classCode?: string;
    headmasterName?: string;
    classStatus?: number;
  };

  type AuthClassQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    classCode?: string;
    headmasterName?: string;
    classStatus?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type AuthClassUpdateRequest = {
    classCode?: string;
    headmasterName?: string;
    classStatus?: number;
  };

  type AuthClassVO = {
    classCode?: string;
    headmasterName?: string;
    classStatus?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type AuthLoginRequest = {
    roleType?: string;
    loginAccount?: string;
    loginPassword?: string;
  };

  type AuthLoginVO = {
    sessionId?: string;
    loginPrincipal?: LoginPrincipal;
  };

  type AuthRegisterRequest = {
    roleType?: string;
    loginAccount?: string;
    displayName?: string;
    classCode?: string;
    loginPassword?: string;
    checkPassword?: string;
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

  type AuthStudentAddRequest = {
    studentCode?: string;
    studentName?: string;
    passwordMd5?: string;
    classCode?: string;
    remark?: string;
    accountStatus?: number;
    loginFailCount?: number;
    lastLoginIp?: string;
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
    loginFailCount?: number;
    lastLoginIp?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type AuthStudentUpdateRequest = {
    id?: number;
    studentCode?: string;
    studentName?: string;
    passwordMd5?: string;
    classCode?: string;
    remark?: string;
    accountStatus?: number;
    loginFailCount?: number;
    lastLoginIp?: string;
  };

  type AuthStudentVO = {
    id?: number;
    studentCode?: string;
    studentName?: string;
    classCode?: string;
    remark?: string;
    accountStatus?: number;
    loginFailCount?: number;
    lastLoginIp?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type AuthTeacher = {
    id?: number;
    username?: string;
    passwordMd5?: string;
    realName?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type AuthTeacherAddRequest = {
    username?: string;
    passwordMd5?: string;
    realName?: string;
  };

  type AuthTeacherQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    username?: string;
    passwordMd5?: string;
    realName?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type AuthTeacherUpdateRequest = {
    id?: number;
    username?: string;
    passwordMd5?: string;
    realName?: string;
  };

  type AuthTeacherVO = {
    id?: number;
    username?: string;
    realName?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type BaseResponseAuthAssistantVO = {
    code?: number;
    data?: AuthAssistantVO;
    message?: string;
  };

  type BaseResponseAuthClassVO = {
    code?: number;
    data?: AuthClassVO;
    message?: string;
  };

  type BaseResponseAuthLoginVO = {
    code?: number;
    data?: AuthLoginVO;
    message?: string;
  };

  type BaseResponseAuthStudentVO = {
    code?: number;
    data?: AuthStudentVO;
    message?: string;
  };

  type BaseResponseAuthTeacherVO = {
    code?: number;
    data?: AuthTeacherVO;
    message?: string;
  };

  type BaseResponseBoolean = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseEduExamVO = {
    code?: number;
    data?: EduExamVO;
    message?: string;
  };

  type BaseResponseEduExerciseItemVO = {
    code?: number;
    data?: EduExerciseItemVO;
    message?: string;
  };

  type BaseResponseEduExerciseVO = {
    code?: number;
    data?: EduExerciseVO;
    message?: string;
  };

  type BaseResponseEduExperimentItemVO = {
    code?: number;
    data?: EduExperimentItemVO;
    message?: string;
  };

  type BaseResponseEduExperimentVO = {
    code?: number;
    data?: EduExperimentVO;
    message?: string;
  };

  type BaseResponseEduLectureVO = {
    code?: number;
    data?: EduLectureVO;
    message?: string;
  };

  type BaseResponseEduPaperVO = {
    code?: number;
    data?: EduPaperVO;
    message?: string;
  };

  type BaseResponseEduQuestionBankVO = {
    code?: number;
    data?: EduQuestionBankVO;
    message?: string;
  };

  type BaseResponseEduQuestionTypeVO = {
    code?: number;
    data?: EduQuestionTypeVO;
    message?: string;
  };

  type BaseResponseLoginPrincipal = {
    code?: number;
    data?: LoginPrincipal;
    message?: string;
  };

  type BaseResponseLoginUserVO = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type BaseResponseLong = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponsePageAuthAssistant = {
    code?: number;
    data?: PageAuthAssistant;
    message?: string;
  };

  type BaseResponsePageAuthAssistantVO = {
    code?: number;
    data?: PageAuthAssistantVO;
    message?: string;
  };

  type BaseResponsePageAuthClass = {
    code?: number;
    data?: PageAuthClass;
    message?: string;
  };

  type BaseResponsePageAuthClassVO = {
    code?: number;
    data?: PageAuthClassVO;
    message?: string;
  };

  type BaseResponsePageAuthStudent = {
    code?: number;
    data?: PageAuthStudent;
    message?: string;
  };

  type BaseResponsePageAuthStudentVO = {
    code?: number;
    data?: PageAuthStudentVO;
    message?: string;
  };

  type BaseResponsePageAuthTeacher = {
    code?: number;
    data?: PageAuthTeacher;
    message?: string;
  };

  type BaseResponsePageAuthTeacherVO = {
    code?: number;
    data?: PageAuthTeacherVO;
    message?: string;
  };

  type BaseResponsePageEduExam = {
    code?: number;
    data?: PageEduExam;
    message?: string;
  };

  type BaseResponsePageEduExamVO = {
    code?: number;
    data?: PageEduExamVO;
    message?: string;
  };

  type BaseResponsePageEduExercise = {
    code?: number;
    data?: PageEduExercise;
    message?: string;
  };

  type BaseResponsePageEduExerciseItem = {
    code?: number;
    data?: PageEduExerciseItem;
    message?: string;
  };

  type BaseResponsePageEduExerciseItemVO = {
    code?: number;
    data?: PageEduExerciseItemVO;
    message?: string;
  };

  type BaseResponsePageEduExerciseVO = {
    code?: number;
    data?: PageEduExerciseVO;
    message?: string;
  };

  type BaseResponsePageEduExperiment = {
    code?: number;
    data?: PageEduExperiment;
    message?: string;
  };

  type BaseResponsePageEduExperimentItem = {
    code?: number;
    data?: PageEduExperimentItem;
    message?: string;
  };

  type BaseResponsePageEduExperimentItemVO = {
    code?: number;
    data?: PageEduExperimentItemVO;
    message?: string;
  };

  type BaseResponsePageEduExperimentVO = {
    code?: number;
    data?: PageEduExperimentVO;
    message?: string;
  };

  type BaseResponsePageEduLecture = {
    code?: number;
    data?: PageEduLecture;
    message?: string;
  };

  type BaseResponsePageEduLectureVO = {
    code?: number;
    data?: PageEduLectureVO;
    message?: string;
  };

  type BaseResponsePageEduPaper = {
    code?: number;
    data?: PageEduPaper;
    message?: string;
  };

  type BaseResponsePageEduPaperVO = {
    code?: number;
    data?: PageEduPaperVO;
    message?: string;
  };

  type BaseResponsePageEduQuestionBank = {
    code?: number;
    data?: PageEduQuestionBank;
    message?: string;
  };

  type BaseResponsePageEduQuestionBankVO = {
    code?: number;
    data?: PageEduQuestionBankVO;
    message?: string;
  };

  type BaseResponsePageEduQuestionType = {
    code?: number;
    data?: PageEduQuestionType;
    message?: string;
  };

  type BaseResponsePageEduQuestionTypeVO = {
    code?: number;
    data?: PageEduQuestionTypeVO;
    message?: string;
  };

  type BaseResponsePageRelPaperQuestion = {
    code?: number;
    data?: PageRelPaperQuestion;
    message?: string;
  };

  type BaseResponsePageRelPaperQuestionVO = {
    code?: number;
    data?: PageRelPaperQuestionVO;
    message?: string;
  };

  type BaseResponsePageResExamRecord = {
    code?: number;
    data?: PageResExamRecord;
    message?: string;
  };

  type BaseResponsePageResExamRecordVO = {
    code?: number;
    data?: PageResExamRecordVO;
    message?: string;
  };

  type BaseResponsePageResExerciseRecord = {
    code?: number;
    data?: PageResExerciseRecord;
    message?: string;
  };

  type BaseResponsePageResExerciseRecordVO = {
    code?: number;
    data?: PageResExerciseRecordVO;
    message?: string;
  };

  type BaseResponsePageResExperimentResult = {
    code?: number;
    data?: PageResExperimentResult;
    message?: string;
  };

  type BaseResponsePageResExperimentResultVO = {
    code?: number;
    data?: PageResExperimentResultVO;
    message?: string;
  };

  type BaseResponsePageResFillBlankDetail = {
    code?: number;
    data?: PageResFillBlankDetail;
    message?: string;
  };

  type BaseResponsePageResFillBlankDetailVO = {
    code?: number;
    data?: PageResFillBlankDetailVO;
    message?: string;
  };

  type BaseResponsePageResScoreSummary = {
    code?: number;
    data?: PageResScoreSummary;
    message?: string;
  };

  type BaseResponsePageResScoreSummaryVO = {
    code?: number;
    data?: PageResScoreSummaryVO;
    message?: string;
  };

  type BaseResponsePageResSubmissionLog = {
    code?: number;
    data?: PageResSubmissionLog;
    message?: string;
  };

  type BaseResponsePageResSubmissionLogVO = {
    code?: number;
    data?: PageResSubmissionLogVO;
    message?: string;
  };

  type BaseResponsePageSysAdminLog = {
    code?: number;
    data?: PageSysAdminLog;
    message?: string;
  };

  type BaseResponsePageSysAdminLogVO = {
    code?: number;
    data?: PageSysAdminLogVO;
    message?: string;
  };

  type BaseResponsePageSysConfig = {
    code?: number;
    data?: PageSysConfig;
    message?: string;
  };

  type BaseResponsePageSysConfigVO = {
    code?: number;
    data?: PageSysConfigVO;
    message?: string;
  };

  type BaseResponsePageSysStudentLog = {
    code?: number;
    data?: PageSysStudentLog;
    message?: string;
  };

  type BaseResponsePageSysStudentLogVO = {
    code?: number;
    data?: PageSysStudentLogVO;
    message?: string;
  };

  type BaseResponsePageUser = {
    code?: number;
    data?: PageUser;
    message?: string;
  };

  type BaseResponsePageUserVO = {
    code?: number;
    data?: PageUserVO;
    message?: string;
  };

  type BaseResponseRelPaperQuestionVO = {
    code?: number;
    data?: RelPaperQuestionVO;
    message?: string;
  };

  type BaseResponseResExamRecordVO = {
    code?: number;
    data?: ResExamRecordVO;
    message?: string;
  };

  type BaseResponseResExerciseRecordVO = {
    code?: number;
    data?: ResExerciseRecordVO;
    message?: string;
  };

  type BaseResponseResExperimentResultVO = {
    code?: number;
    data?: ResExperimentResultVO;
    message?: string;
  };

  type BaseResponseResFillBlankDetailVO = {
    code?: number;
    data?: ResFillBlankDetailVO;
    message?: string;
  };

  type BaseResponseResScoreSummaryVO = {
    code?: number;
    data?: ResScoreSummaryVO;
    message?: string;
  };

  type BaseResponseResSubmissionLogVO = {
    code?: number;
    data?: ResSubmissionLogVO;
    message?: string;
  };

  type BaseResponseString = {
    code?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseSysAdminLogVO = {
    code?: number;
    data?: SysAdminLogVO;
    message?: string;
  };

  type BaseResponseSysConfigVO = {
    code?: number;
    data?: SysConfigVO;
    message?: string;
  };

  type BaseResponseSysStudentLogVO = {
    code?: number;
    data?: SysStudentLogVO;
    message?: string;
  };

  type BaseResponseUser = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserVO = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type batchUpdateAccountStatusByClassParams = {
    classCode: string;
    accountStatus: number;
  };

  type checkParams = {
    timestamp: string;
    nonce: string;
    signature: string;
    echostr: string;
  };

  type deleteAuthAssistantParams = {
    id: string;
  };

  type deleteAuthClassParams = {
    id: string;
  };

  type deleteAuthStudentParams = {
    id: string;
  };

  type deleteAuthTeacherParams = {
    id: string;
  };

  type deleteEduExamParams = {
    id: string;
  };

  type deleteEduExerciseItemParams = {
    id: string;
  };

  type deleteEduExerciseParams = {
    id: string;
  };

  type deleteEduExperimentItemParams = {
    id: string;
  };

  type deleteEduExperimentParams = {
    id: string;
  };

  type deleteEduLectureParams = {
    id: string;
  };

  type deleteEduPaperParams = {
    id: string;
  };

  type deleteEduQuestionBankParams = {
    id: string;
  };

  type deleteEduQuestionTypeParams = {
    id: string;
  };

  type deleteRelPaperQuestionParams = {
    id: string;
  };

  type DeleteRequest = {
    id?: number;
  };

  type deleteResExamRecordParams = {
    id: string;
  };

  type deleteResExerciseRecordParams = {
    id: string;
  };

  type deleteResExperimentResultParams = {
    id: string;
  };

  type deleteResFillBlankDetailParams = {
    id: string;
  };

  type deleteResScoreSummaryParams = {
    id: string;
  };

  type deleteResSubmissionLogParams = {
    id: string;
  };

  type deleteSysAdminLogParams = {
    id: string;
  };

  type deleteSysConfigParams = {
    id: string;
  };

  type deleteSysStudentLogParams = {
    id: string;
  };

  type EduExam = {
    id?: number;
    examName?: string;
    durationMin?: number;
    startTime?: string;
    isPublished?: boolean;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduExamAddRequest = {
    examName?: string;
    durationMin?: number;
    startTime?: string;
    isPublished?: boolean;
  };

  type EduExamQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    examName?: string;
    durationMin?: number;
    startTime?: string;
    isPublished?: boolean;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduExamUpdateRequest = {
    id?: number;
    examName?: string;
    durationMin?: number;
    startTime?: string;
    isPublished?: boolean;
  };

  type EduExamVO = {
    id?: number;
    examName?: string;
    durationMin?: number;
    startTime?: string;
    isPublished?: boolean;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduExercise = {
    id?: number;
    sortOrder?: number;
    taskName?: string;
    teacherId?: number;
    relateExpId?: number;
    interactMode?: number;
    description?: string;
    publishStatus?: number;
    startTime?: string;
    endTime?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduExerciseAddRequest = {
    sortOrder?: number;
    taskName?: string;
    relateExpId?: number;
    interactMode?: number;
    description?: string;
    startTime?: string;
    endTime?: string;
  };

  type EduExerciseItem = {
    id?: number;
    exerciseId?: number;
    question?: string;
    optionsText?: string;
    standardAnswer?: string;
    questionType?: number;
    maxScore?: number;
    questionBankId?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduExerciseItemAddRequest = {
    exerciseId?: number;
    question?: string;
    optionsText?: string;
    standardAnswer?: string;
    questionType?: number;
  };

  type EduExerciseItemQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    exerciseId?: number;
    question?: string;
    optionsText?: string;
    standardAnswer?: string;
    questionType?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduExerciseItemUpdateRequest = {
    id?: number;
    exerciseId?: number;
    question?: string;
    optionsText?: string;
    standardAnswer?: string;
    questionType?: number;
  };

  type EduExerciseItemVO = {
    id?: number;
    exerciseId?: number;
    question?: string;
    optionsText?: string;
    standardAnswer?: string;
    questionType?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduExerciseQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    taskName?: string;
    relateExpId?: number;
    interactMode?: number;
    description?: string;
    startTime?: string;
    endTime?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduExerciseUpdateRequest = {
    id?: number;
    sortOrder?: number;
    taskName?: string;
    relateExpId?: number;
    interactMode?: number;
    description?: string;
    startTime?: string;
    endTime?: string;
  };

  type EduExerciseVO = {
    id?: number;
    sortOrder?: number;
    taskName?: string;
    relateExpId?: number;
    interactMode?: number;
    description?: string;
    startTime?: string;
    endTime?: string;
    createdAt?: string;
    updatedAt?: string;
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

  type EduExperimentItem = {
    id?: number;
    sortOrder?: number;
    itemName?: string;
    questionType?: number;
    questionContent?: string;
    experimentId?: number;
    standardAnswer?: string;
    maxScore?: number;
    itemStatus?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduExperimentItemAddRequest = {
    sortOrder?: number;
    itemName?: string;
    questionType?: number;
    questionContent?: string;
    experimentId?: number;
    standardAnswer?: string;
    maxScore?: number;
    itemStatus?: number;
  };

  type EduExperimentItemQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    itemName?: string;
    questionType?: number;
    questionContent?: string;
    experimentId?: number;
    standardAnswer?: string;
    maxScore?: number;
    itemStatus?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduExperimentItemUpdateRequest = {
    id?: number;
    sortOrder?: number;
    itemName?: string;
    questionType?: number;
    questionContent?: string;
    experimentId?: number;
    standardAnswer?: string;
    maxScore?: number;
    itemStatus?: number;
  };

  type EduExperimentItemVO = {
    id?: number;
    sortOrder?: number;
    itemName?: string;
    questionType?: number;
    questionContent?: string;
    experimentId?: number;
    standardAnswer?: string;
    maxScore?: number;
    itemStatus?: number;
    createdAt?: string;
    updatedAt?: string;
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
    createdAt?: string;
    updatedAt?: string;
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
    createdAt?: string;
    updatedAt?: string;
  };

  type EduLecture = {
    id?: number;
    lectureName?: string;
    categoryId?: number;
    fileExtension?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduLectureAddRequest = {
    lectureName?: string;
    categoryId?: number;
    fileExtension?: string;
  };

  type EduLectureQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    lectureName?: string;
    categoryId?: number;
    fileExtension?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduLectureUpdateRequest = {
    id?: number;
    lectureName?: string;
    categoryId?: number;
    fileExtension?: string;
  };

  type EduLectureVO = {
    id?: number;
    lectureName?: string;
    categoryId?: number;
    fileExtension?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduPaper = {
    id?: number;
    paperCode?: number;
    paperName?: string;
    description?: string;
    generationTime?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduPaperAddRequest = {
    paperCode?: number;
    paperName?: string;
    description?: string;
    generationTime?: string;
  };

  type EduPaperQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    paperCode?: number;
    paperName?: string;
    description?: string;
    generationTime?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduPaperUpdateRequest = {
    id?: number;
    paperCode?: number;
    paperName?: string;
    description?: string;
    generationTime?: string;
  };

  type EduPaperVO = {
    id?: number;
    paperCode?: number;
    paperName?: string;
    description?: string;
    generationTime?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduQuestionBank = {
    id?: number;
    questionContent?: string;
    questionType?: number;
    optionsText?: string;
    standardAnswer?: string;
    analysis?: string;
    difficulty?: number;
    creatorTeacherId?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduQuestionBankAddRequest = {
    questionContent?: string;
    questionType?: number;
    optionsText?: string;
    standardAnswer?: string;
    analysis?: string;
    difficulty?: number;
    creatorTeacherId?: number;
  };

  type EduQuestionBankQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    questionContent?: string;
    questionType?: number;
    difficulty?: number;
    creatorTeacherId?: number;
  };

  type EduQuestionBankUpdateRequest = {
    id?: number;
    questionContent?: string;
    questionType?: number;
    optionsText?: string;
    standardAnswer?: string;
    analysis?: string;
    difficulty?: number;
  };

  type EduQuestionBankVO = {
    id?: number;
    questionContent?: string;
    standardAnswer?: string;
    questionType?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type EduQuestionType = {
    typeId?: number;
    typeName?: string;
    createdAt?: string;
  };

  type EduQuestionTypeAddRequest = {
    typeId?: number;
    typeName?: string;
  };

  type EduQuestionTypeQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    typeId?: number;
    typeName?: string;
    createdAt?: string;
  };

  type EduQuestionTypeUpdateRequest = {
    typeId?: number;
    typeName?: string;
  };

  type EduQuestionTypeVO = {
    typeId?: number;
    typeName?: string;
    createdAt?: string;
  };

  type getAuthAssistantVOByIdParams = {
    id: string;
  };

  type getAuthClassVOByIdParams = {
    id: string;
  };

  type getAuthStudentVOByIdParams = {
    id: string;
  };

  type getAuthTeacherVOByIdParams = {
    id: string;
  };

  type getEduExamVOByIdParams = {
    id: string;
  };

  type getEduExerciseItemVOByIdParams = {
    id: string;
  };

  type getEduExerciseVOByIdParams = {
    id: string;
  };

  type getEduExperimentItemVOByIdParams = {
    id: string;
  };

  type getEduExperimentVOByIdParams = {
    id: string;
  };

  type getEduLectureVOByIdParams = {
    id: string;
  };

  type getEduPaperVOByIdParams = {
    id: string;
  };

  type getEduQuestionBankVOByIdParams = {
    id: string;
  };

  type getEduQuestionTypeVOByIdParams = {
    id: string;
  };

  type getRelPaperQuestionVOByIdParams = {
    id: string;
  };

  type getResExamRecordVOByIdParams = {
    id: string;
  };

  type getResExerciseRecordVOByIdParams = {
    id: string;
  };

  type getResExperimentResultVOByIdParams = {
    id: string;
  };

  type getResFillBlankDetailVOByIdParams = {
    id: string;
  };

  type getResScoreSummaryVOByIdParams = {
    id: string;
  };

  type getResSubmissionLogVOByIdParams = {
    id: string;
  };

  type getSysAdminLogVOByIdParams = {
    id: string;
  };

  type getSysConfigVOByIdParams = {
    id: string;
  };

  type getSysStudentLogVOByIdParams = {
    id: string;
  };

  type getUserByIdParams = {
    id: number;
  };

  type getUserVOByIdParams = {
    id: number;
  };

  type LoginPrincipal = {
    userId?: number;
    roleType?: string;
    roleCode?: string;
    loginAccount?: string;
    displayName?: string;
    major?: string;
    classCode?: string;
    lastLoginIp?: string;
  };

  type LoginUserVO = {
    id?: number;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
    createTime?: string;
    updateTime?: string;
  };

  type OrderItem = {
    column?: string;
    asc?: boolean;
  };

  type PageAuthAssistant = {
    records?: AuthAssistant[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageAuthAssistant;
    searchCount?: PageAuthAssistant;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageAuthAssistantVO = {
    records?: AuthAssistantVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageAuthAssistantVO;
    searchCount?: PageAuthAssistantVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageAuthClass = {
    records?: AuthClass[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageAuthClass;
    searchCount?: PageAuthClass;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageAuthClassVO = {
    records?: AuthClassVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageAuthClassVO;
    searchCount?: PageAuthClassVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageAuthStudent = {
    records?: AuthStudent[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageAuthStudent;
    searchCount?: PageAuthStudent;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageAuthStudentVO = {
    records?: AuthStudentVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageAuthStudentVO;
    searchCount?: PageAuthStudentVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageAuthTeacher = {
    records?: AuthTeacher[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageAuthTeacher;
    searchCount?: PageAuthTeacher;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageAuthTeacherVO = {
    records?: AuthTeacherVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageAuthTeacherVO;
    searchCount?: PageAuthTeacherVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduExam = {
    records?: EduExam[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduExam;
    searchCount?: PageEduExam;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduExamVO = {
    records?: EduExamVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduExamVO;
    searchCount?: PageEduExamVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduExercise = {
    records?: EduExercise[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduExercise;
    searchCount?: PageEduExercise;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduExerciseItem = {
    records?: EduExerciseItem[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduExerciseItem;
    searchCount?: PageEduExerciseItem;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduExerciseItemVO = {
    records?: EduExerciseItemVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduExerciseItemVO;
    searchCount?: PageEduExerciseItemVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduExerciseVO = {
    records?: EduExerciseVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduExerciseVO;
    searchCount?: PageEduExerciseVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduExperiment = {
    records?: EduExperiment[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduExperiment;
    searchCount?: PageEduExperiment;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduExperimentItem = {
    records?: EduExperimentItem[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduExperimentItem;
    searchCount?: PageEduExperimentItem;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduExperimentItemVO = {
    records?: EduExperimentItemVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduExperimentItemVO;
    searchCount?: PageEduExperimentItemVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduExperimentVO = {
    records?: EduExperimentVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduExperimentVO;
    searchCount?: PageEduExperimentVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduLecture = {
    records?: EduLecture[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduLecture;
    searchCount?: PageEduLecture;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduLectureVO = {
    records?: EduLectureVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduLectureVO;
    searchCount?: PageEduLectureVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduPaper = {
    records?: EduPaper[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduPaper;
    searchCount?: PageEduPaper;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduPaperVO = {
    records?: EduPaperVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduPaperVO;
    searchCount?: PageEduPaperVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduQuestionBank = {
    records?: EduQuestionBank[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduQuestionBank;
    searchCount?: PageEduQuestionBank;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduQuestionBankVO = {
    records?: EduQuestionBankVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduQuestionBankVO;
    searchCount?: PageEduQuestionBankVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduQuestionType = {
    records?: EduQuestionType[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduQuestionType;
    searchCount?: PageEduQuestionType;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageEduQuestionTypeVO = {
    records?: EduQuestionTypeVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageEduQuestionTypeVO;
    searchCount?: PageEduQuestionTypeVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageRelPaperQuestion = {
    records?: RelPaperQuestion[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageRelPaperQuestion;
    searchCount?: PageRelPaperQuestion;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageRelPaperQuestionVO = {
    records?: RelPaperQuestionVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageRelPaperQuestionVO;
    searchCount?: PageRelPaperQuestionVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageResExamRecord = {
    records?: ResExamRecord[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageResExamRecord;
    searchCount?: PageResExamRecord;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageResExamRecordVO = {
    records?: ResExamRecordVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageResExamRecordVO;
    searchCount?: PageResExamRecordVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageResExerciseRecord = {
    records?: ResExerciseRecord[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageResExerciseRecord;
    searchCount?: PageResExerciseRecord;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageResExerciseRecordVO = {
    records?: ResExerciseRecordVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageResExerciseRecordVO;
    searchCount?: PageResExerciseRecordVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageResExperimentResult = {
    records?: ResExperimentResult[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageResExperimentResult;
    searchCount?: PageResExperimentResult;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageResExperimentResultVO = {
    records?: ResExperimentResultVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageResExperimentResultVO;
    searchCount?: PageResExperimentResultVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageResFillBlankDetail = {
    records?: ResFillBlankDetail[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageResFillBlankDetail;
    searchCount?: PageResFillBlankDetail;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageResFillBlankDetailVO = {
    records?: ResFillBlankDetailVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageResFillBlankDetailVO;
    searchCount?: PageResFillBlankDetailVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageResScoreSummary = {
    records?: ResScoreSummary[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageResScoreSummary;
    searchCount?: PageResScoreSummary;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageResScoreSummaryVO = {
    records?: ResScoreSummaryVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageResScoreSummaryVO;
    searchCount?: PageResScoreSummaryVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageResSubmissionLog = {
    records?: ResSubmissionLog[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageResSubmissionLog;
    searchCount?: PageResSubmissionLog;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageResSubmissionLogVO = {
    records?: ResSubmissionLogVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageResSubmissionLogVO;
    searchCount?: PageResSubmissionLogVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageSysAdminLog = {
    records?: SysAdminLog[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageSysAdminLog;
    searchCount?: PageSysAdminLog;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageSysAdminLogVO = {
    records?: SysAdminLogVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageSysAdminLogVO;
    searchCount?: PageSysAdminLogVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageSysConfig = {
    records?: SysConfig[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageSysConfig;
    searchCount?: PageSysConfig;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageSysConfigVO = {
    records?: SysConfigVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageSysConfigVO;
    searchCount?: PageSysConfigVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageSysStudentLog = {
    records?: SysStudentLog[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageSysStudentLog;
    searchCount?: PageSysStudentLog;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageSysStudentLogVO = {
    records?: SysStudentLogVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageSysStudentLogVO;
    searchCount?: PageSysStudentLogVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageUser = {
    records?: User[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageUser;
    searchCount?: PageUser;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageUserVO = {
    records?: UserVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageUserVO;
    searchCount?: PageUserVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type RelPaperQuestion = {
    id?: number;
    paperId?: number;
    questionId?: number;
    score?: number;
    questionOrder?: number;
    sectionName?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type RelPaperQuestionAddRequest = {
    paperId?: number;
    questionId?: number;
    score?: number;
  };

  type RelPaperQuestionQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    paperId?: number;
    questionId?: number;
    score?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type RelPaperQuestionUpdateRequest = {
    id?: number;
    paperId?: number;
    questionId?: number;
    score?: number;
  };

  type RelPaperQuestionVO = {
    id?: number;
    paperId?: number;
    questionId?: number;
    score?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResExamRecord = {
    id?: number;
    examId?: number;
    paperId?: number;
    paperQuestionId?: number;
    studentId?: number;
    questionId?: number;
    studentAnswer?: string;
    score?: number;
    gradingStatus?: number;
    comment?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResExamRecordAddRequest = {
    studentId?: number;
    questionId?: number;
    studentAnswer?: string;
  };

  type ResExamRecordQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    studentId?: number;
    questionId?: number;
    studentAnswer?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResExamRecordUpdateRequest = {
    id?: number;
    studentId?: number;
    questionId?: number;
    studentAnswer?: string;
  };

  type ResExamRecordVO = {
    id?: number;
    studentId?: number;
    questionId?: number;
    studentAnswer?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResExerciseRecord = {
    id?: number;
    exerciseId?: number;
    itemId?: number;
    studentId?: number;
    choiceAnswer?: string;
    textContent?: string;
    score?: number;
    submittedAt?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResExerciseRecordAddRequest = {
    itemId?: number;
    studentId?: number;
    choiceAnswer?: string;
    textContent?: string;
    score?: number;
    submittedAt?: string;
  };

  type ResExerciseRecordQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    itemId?: number;
    studentId?: number;
    choiceAnswer?: string;
    textContent?: string;
    score?: number;
    submittedAt?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResExerciseRecordUpdateRequest = {
    id?: number;
    itemId?: number;
    studentId?: number;
    choiceAnswer?: string;
    textContent?: string;
    score?: number;
    submittedAt?: string;
  };

  type ResExerciseRecordVO = {
    id?: number;
    itemId?: number;
    studentId?: number;
    choiceAnswer?: string;
    textContent?: string;
    score?: number;
    submittedAt?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResExperimentResult = {
    id?: number;
    studentId?: number;
    itemId?: number;
    subContent?: string;
    score?: number;
    submittedAt?: string;
    gradingStatus?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResExperimentResultAddRequest = {
    studentId?: number;
    itemId?: number;
    subContent?: string;
    score?: number;
    submittedAt?: string;
    gradingStatus?: number;
  };

  type ResExperimentResultQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    studentId?: number;
    itemId?: number;
    subContent?: string;
    score?: number;
    submittedAt?: string;
    gradingStatus?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResExperimentResultUpdateRequest = {
    id?: number;
    studentId?: number;
    itemId?: number;
    subContent?: string;
    score?: number;
    submittedAt?: string;
    gradingStatus?: number;
  };

  type ResExperimentResultVO = {
    id?: number;
    studentId?: number;
    itemId?: number;
    subContent?: string;
    score?: number;
    submittedAt?: string;
    gradingStatus?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResFillBlankDetail = {
    id?: number;
    itemId?: number;
    blankIndex?: number;
    answerContent?: string;
    answerHash?: string;
    submitCount?: number;
    isCorrect?: boolean;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResFillBlankDetailAddRequest = {
    itemId?: number;
    blankIndex?: number;
    answerContent?: string;
    answerHash?: string;
    submitCount?: number;
    isCorrect?: boolean;
  };

  type ResFillBlankDetailQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    itemId?: number;
    blankIndex?: number;
    answerContent?: string;
    answerHash?: string;
    submitCount?: number;
    isCorrect?: boolean;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResFillBlankDetailUpdateRequest = {
    id?: number;
    itemId?: number;
    blankIndex?: number;
    answerContent?: string;
    answerHash?: string;
    submitCount?: number;
    isCorrect?: boolean;
  };

  type ResFillBlankDetailVO = {
    id?: number;
    itemId?: number;
    blankIndex?: number;
    answerContent?: string;
    answerHash?: string;
    submitCount?: number;
    isCorrect?: boolean;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResScoreSummary = {
    id?: number;
    studentId?: number;
    experimentId?: number;
    totalScore?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResScoreSummaryAddRequest = {
    studentId?: number;
    experimentId?: number;
    totalScore?: number;
  };

  type ResScoreSummaryQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    studentId?: number;
    experimentId?: number;
    totalScore?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResScoreSummaryUpdateRequest = {
    id?: number;
    studentId?: number;
    experimentId?: number;
    totalScore?: number;
  };

  type ResScoreSummaryVO = {
    id?: number;
    studentId?: number;
    experimentId?: number;
    totalScore?: number;
    createdAt?: string;
    updatedAt?: string;
  };

  type ResSubmissionLog = {
    id?: number;
    resultId?: number;
    contentSnapshot?: string;
    snapshotTime?: string;
    createdAt?: string;
  };

  type ResSubmissionLogAddRequest = {
    resultId?: number;
    contentSnapshot?: string;
    snapshotTime?: string;
  };

  type ResSubmissionLogQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    resultId?: number;
    contentSnapshot?: string;
    snapshotTime?: string;
    createdAt?: string;
  };

  type ResSubmissionLogUpdateRequest = {
    id?: number;
    resultId?: number;
    contentSnapshot?: string;
    snapshotTime?: string;
  };

  type ResSubmissionLogVO = {
    id?: number;
    resultId?: number;
    contentSnapshot?: string;
    snapshotTime?: string;
    createdAt?: string;
  };

  type SysAdminLog = {
    id?: number;
    account?: string;
    actionType?: number;
    actionDetail?: string;
    opTime?: string;
    clientIp?: string;
    createdAt?: string;
  };

  type SysAdminLogAddRequest = {
    account?: string;
    actionType?: number;
    actionDetail?: string;
    opTime?: string;
    clientIp?: string;
  };

  type SysAdminLogQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    account?: string;
    actionType?: number;
    actionDetail?: string;
    opTime?: string;
    clientIp?: string;
    createdAt?: string;
  };

  type SysAdminLogUpdateRequest = {
    id?: number;
    account?: string;
    actionType?: number;
    actionDetail?: string;
    opTime?: string;
    clientIp?: string;
  };

  type SysAdminLogVO = {
    id?: number;
    account?: string;
    actionType?: number;
    actionDetail?: string;
    opTime?: string;
    clientIp?: string;
    createdAt?: string;
  };

  type SysConfig = {
    configKey?: string;
    configValue?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type SysConfigAddRequest = {
    configKey?: string;
    configValue?: string;
  };

  type SysConfigQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    configKey?: string;
    configValue?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type SysConfigUpdateRequest = {
    configKey?: string;
    configValue?: string;
  };

  type SysConfigVO = {
    configKey?: string;
    configValue?: string;
    createdAt?: string;
    updatedAt?: string;
  };

  type SysStudentLog = {
    id?: number;
    account?: string;
    actionType?: number;
    actionDetail?: string;
    opTime?: string;
    clientIp?: string;
    createdAt?: string;
  };

  type SysStudentLogAddRequest = {
    account?: string;
    actionType?: number;
    actionDetail?: string;
    opTime?: string;
    clientIp?: string;
  };

  type SysStudentLogQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    account?: string;
    actionType?: number;
    actionDetail?: string;
    opTime?: string;
    clientIp?: string;
    createdAt?: string;
  };

  type SysStudentLogUpdateRequest = {
    id?: number;
    account?: string;
    actionType?: number;
    actionDetail?: string;
    opTime?: string;
    clientIp?: string;
  };

  type SysStudentLogVO = {
    id?: number;
    account?: string;
    actionType?: number;
    actionDetail?: string;
    opTime?: string;
    clientIp?: string;
    createdAt?: string;
  };

  type uploadFileParams = {
    biz: string;
  };

  type User = {
    id?: number;
    userAccount?: string;
    userPassword?: string;
    unionId?: string;
    mpOpenId?: string;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
    createTime?: string;
    updateTime?: string;
    isDelete?: number;
  };

  type UserAddRequest = {
    userName?: string;
    userAccount?: string;
    userAvatar?: string;
    userRole?: string;
  };

  type userLoginByWxOpenParams = {
    code: string;
  };

  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };

  type UserQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    unionId?: string;
    mpOpenId?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserRegisterRequest = {
    userAccount?: string;
    userPassword?: string;
    checkPassword?: string;
  };

  type UserUpdateMyRequest = {
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
  };

  type UserUpdateRequest = {
    id?: number;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserVO = {
    id?: number;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
    createTime?: string;
  };
}
