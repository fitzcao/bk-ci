# OpenAPI
## 说明
    bk-ci接口命名设计包括user/service/build/app/op 五类接口
## 用途
    用于供bkci意外的第三方系统调用bkci提供出来的接口。作为统一接入方案，调用service类接口
## 类型
### 用户态
    模拟用户的身份访问bkci后台,需通过用户登录态产生的accessToke,需对accessToken做校验。校验不通过将被拒绝访问。accessToken登录态有超时时间,动态变化。
### 应用态
    供平台级别上游访问bkci后台,需在apigw平台申请平台账号。会校验apigw生成的平台的appCode和appSercret. 
## 依赖
    blueking--apigw
## 接口列表
### process
接口名称|接口说明|接口详情
------|-------|-------
启动流水线|启动流水线构建| http://
取消流水线|获取流水线状态| http://
获取流水线状态|取消流水线|http://
### project
### store



