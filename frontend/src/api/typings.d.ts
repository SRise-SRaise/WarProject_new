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
    us