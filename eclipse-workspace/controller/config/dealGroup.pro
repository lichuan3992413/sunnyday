#Encoding utf-8
#任务监控线程
threads_dataSyncThread = 1
#信息审核模块主控线程
#threads_systemCheckDealThread = 2
#长短信入库线程
threads_longMsgInsertDBThread = 3
#人工审核数据入库线程
threads_manualCheckToDBThread = 4
#没有在redis中完成状态报告匹配的状态报告数据入库线程
threads_unmatchedReportSaveThread = 6
#上行信息匹配线程
threads_dealDeliverThread = 7
#产生通道待发数据线程
threads_submitMessageProductThread = 8
#产生客户状态报告线程
threads_sendReportProductThread = 9
#驳回短信处理线程
threads_rejectMessageDealThread = 10
#下发数据入库线程
threads_sendHistorySaveThread = 11
#尚未完成状态报告的，提交网关数据入库
threads_sentMessageSaveThread = 12
#推送完成或者自取完成的状态报告数据入库线程
threads_sentReportSaveThread = 13
#推送完成或者自取完成的上行数据数据入库线程
threads_sentDeliverSaveThread = 14
#产生客户上行信息线程
threads_sendDeliverProductThread = 15
#处理状态报告超时匹配数据入库线程
threads_dealedReceiveReportSavaThread = 16
#程序监控线程
threads_monitorThread = 17

#状态报告redis匹配，同时处理返回的状态报告超时
#threads_dealReportOnRecReportThread = 18
#状态报告redis匹配，同时处理下发记录超时
threads_dealReportOnSubmitThread = 19
#处理接收到网关状态报告记录,和初步状态报告匹配
threads_receiveReportConsumeThread = 20
#处理下发记录,和初步状态报告匹配
threads_sentMessageConsumeThread = 21
#统计数据入库
threads_smsStatisticsThread = 22
#二次缓存队列数据写入文件
threads_historyDataWriteThread=23
#从文件中读取二次缓存队列
threads_historyDataReadThread=24
#主备探活切换线程
#threads_masterSlaveSwitchThread=25
#打扰短信入库线程
threads_avoidDisturbMessageToDBThread=26
#异常号码入库
threads_exceptionalMobileToDBThread=27
#读取redis交互异常时，回写的数据
threads_submitDoneDataReadThread=28
#当redis交互异常时，把数据进行回写
threads_submitDoneDataWriteThread=29
#定时短信入库线程
threads_fixedTimeMessageToDBThread=30
#生成测试数据
#threads_createSubmitBeanToGateRedisThread=31


#------可以多台机器同时启动的任务
#处理接收队列中的数据
submitDataConsumeTask = 10
#处理接收到的上行信息
receiveDeliverConsumeTask = 10
#处理推状态报告送完成任务
sentReportConsumeTask = 10
#处理推上行送完成任务
sentDeliverConsumeTask = 10
