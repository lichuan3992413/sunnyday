#Encoding utf-8
#处理补发数据，或者DB接口表数据线程
threads_testMessageDealThread = 1
#长短信拼接线程
threads_longMsgSpliceThread = 2
#审核完成信息处理线程
threads_checkedMsgDealThread = 3
#处理群发审核信息线程
threads_dealGroupMsgThread = 4
#状态报告表匹配线程
threads_dealReportInDBThread = 5
#长短信拼接超时线程
threads_longMsgTimeOutDealThread = 6
#处理超时未匹配的下发信息线程
threads_dealCatchTimeOutThread = 7
#移表处理线程
threads_dataTransferServiceThread = 8
#状态报告上行重推处理线程
threads_getReporDeliverFromDBThread=9
#客户上行状态报告积压量监控线程
threads_reporDeliverMonitor=10
#余额同步线程
threads_userBalanceSyncThread=11
#余额预警监控线程
threads_balanceWarnThread=12
#定时短信下发处理线程
threads_dealTimedMsgThread=13
#统计定时短信下发的数量
threads_batchMsgThread=14
#更改定时短信批次的下发状态
threads_statisTimedMsgThread=15
#删除已经过期的set值，以防止脏数据出现
threads_dealSetDataThread= 16
#从数据库中加载可下发的打扰短信线程
threads_disturbMessageDealThread=17
#从check_message_batch表更新 check_message线程
threads_dealCheckMsgBatchThread=18
#新的审核完成信息处理线程
threads_dealCheckMsgThread=19

#发送check_message_batch表短信线程
threads_sendCheckMsgBatchThread=20
#发送短信确认签约线程
threads_sendMsgContractConfirmThread=21
#history表统计挪表线程
#threads_sendHistoryDataTransferThread=22
#统计服务管理线程
#threads_staticsServiceManageThread=23

#只可以单机启动的任务 单位 毫秒(ms)
#从数据库中的数据加载【黑名单信息】到redis中的任务
blackMobileSyncTask=300000
#从数据库中的数据加载【审核缓存信息】到redis中的任务
checkCacheSyncTask=60000
#从数据库中的数据加载【 gateConfig配置 错误码 上行匹配参考数据  通道接入号拦截信息 】到redis中的任务
commonInfoSyncTask=60000
#从数据库中的数据加载【线程信息】到redis中的任务
dbThreadControllerSyncTask = 5000
#从数据库中的数据加载【关键字】到redis中的任务
keywordSyncTask=300000
#从数据库中的数据加载【携号转网】到redis中的任务
netSwitchedMobileSyncTask=300000
#从数据库中的数据加载【通道信息】到redis中的任务
tdInfoSyncTask=60000
#从数据库中的数据加载【用户及用户业务信息】到redis中的任务
userInfoSyncTask = 30000
#从数据库中的数据加载【内容白名单模板】到redis中的任务
whiteListTemplateSysnTask = 300000

#从数据库中的数据加载【手机归属信息】到redis中的任务
moblieHomeSyncTask=300000

#从数据库中的数据加载【短信模板信息】到redis中的任务
smsTemplateSysnTask = 30000


