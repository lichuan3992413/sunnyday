package sunnyday.api.send.controller;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import sunnyday.channel.cache.*;
import sunnyday.api.send.cmpp2.CMPPActiveMessage;
import sunnyday.api.send.cmpp2.CMPPActiveRespMessage;
import sunnyday.api.send.cmpp2.CMPPConnectMessage;
import sunnyday.api.send.cmpp2.CMPPConnectRespMessage;
import sunnyday.api.send.cmpp2.CMPPDeliverMessage;
import sunnyday.api.send.cmpp2.CMPPDeliverRespMessage;
import sunnyday.api.send.cmpp2.CMPPMessage;
import sunnyday.api.send.cmpp2.CMPPSubmitMessage;
import sunnyday.api.send.cmpp2.CMPPSubmitRespMessage;
import sunnyday.api.send.cmpp2.ChargeTermIdPool;
import sunnyday.api.send.cmpp2.CmppMessageUtil;
import sunnyday.api.send.cmpp2.CommonBean;
import sunnyday.common.model.ReportBean;
import sunnyday.common.model.*;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.DateUtil;
import sunnyday.tools.util.UtilTool;

public class Cmpp2SendController extends Thread {

	private Logger log = CommonLogFactory.getLog(Cmpp2SendController.class);
	private Logger monitor_log = CommonLogFactory.getLog("monitor");
	private boolean running = false;
	private String yw_code;
	private int max_queue_size = 1000;
	private ArrayBlockingQueue<SmsMessage> queue;
	private ConcurrentHashMap<String, ArrayBlockingQueue<SmsMessage>> scanMap;
	private String gate_host;
	private int gate_port;
	private String gate_user;
	private String gate_pwd;
	private String gate_corp_code;
	private String gate_service_id;
	private int max_allow_connect = 0;

	private int max_speed_per_second = 0;

	private Map<Integer, CmppMessageUtil> waitSubmitRespMap = new ConcurrentHashMap();
	private Map<Integer, CmppMessageUtil> waitSubmitRespMap_long = new ConcurrentHashMap(1000);

	private Map<String, AtomicInteger> ip_connect_count_map = new ConcurrentHashMap();

	private Map<String, AtomicInteger> current_ip_conn_count_map = new ConcurrentHashMap();
	private long last_check_time;
	private long last_check_time_long;
	private long last_check_login_time;

	String[] params = null;

	public Cmpp2SendController() {
		this.running = true;
		this.scanMap = DataCenter.getScanMap();
		this.last_check_login_time = System.currentTimeMillis();
		this.last_check_time = System.currentTimeMillis();
		this.last_check_time_long = System.currentTimeMillis();
	}

	public void doInit(String param, String channelID) {
		try {
			this.yw_code = channelID;
			Document doc = DocumentHelper.parseText(param);
			Element root = doc.getRootElement();

			this.gate_host = root.attributeValue("gate_ip");
			this.gate_port = Integer.parseInt(root.attributeValue("gate_port"));
			this.gate_user = root.attributeValue("gate_user");
			this.gate_pwd = root.attributeValue("gate_pwd");
			this.gate_corp_code = root.attributeValue("gate_corp_code");
			this.gate_service_id = root.attributeValue("gate_server_id");
			this.max_allow_connect = Integer.parseInt(root.attributeValue("gate_max_connect"));
			this.max_speed_per_second = Integer.parseInt(root.attributeValue("gate_max_speed"));

			if (this.scanMap != null) {
				this.queue = ((ArrayBlockingQueue) this.scanMap.get(this.yw_code));

				if (this.queue == null) {
					this.queue = new ArrayBlockingQueue(this.max_queue_size);
					this.scanMap.put(this.yw_code, this.queue);
				}

			}

			dispatcherIpConnect();

			StringBuilder sb = new StringBuilder();
			sb.append("yw_code:").append(this.yw_code).append(" total_connect=").append(this.max_allow_connect)
					.append(" dispatch connect ");
			for (String ip : this.ip_connect_count_map.keySet()) {
				int connect_count = ((AtomicInteger) this.ip_connect_count_map.get(ip)).get();
				sb.append(ip).append("=").append(connect_count).append(" ");
			}
			String ip_count = sb.toString();
			this.monitor_log.info(ip_count);
			this.log.info(ip_count);
		} catch (Exception e) {
			this.running = false;
			this.log.error("yw_code:" + this.yw_code, e);
		}
	}

	private void dispatcherIpConnect() {
		if (StringUtils.isNotBlank(this.gate_host)) {
			String[] ips = this.gate_host.split(",");
			if ((ips != null) && (ips.length > 0))
				for (int i = 0; i < this.max_allow_connect;)
					for (String ip : ips) {
						if (!this.ip_connect_count_map.containsKey(ip)) {
							this.ip_connect_count_map.put(ip, new AtomicInteger(0));
						}
						((AtomicInteger) this.ip_connect_count_map.get(ip)).getAndIncrement();
						i++;
						if (i >= this.max_allow_connect)
							return;
					}
		}
	}

	public void run() {
		Selector selector = null;
		SelectionKey key = null;

		try {
			selector = Selector.open();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.log.info("yw_code:" + this.yw_code + "    SendThread  is start ");
		this.monitor_log.info("yw_code:" + this.yw_code + "    SendThread  is start ");
		while (this.running) {
			try {
				if ((this.ip_connect_count_map != null) && (this.ip_connect_count_map.size() > 0)) {
					for (String ip : this.ip_connect_count_map.keySet()) {
						if (!this.current_ip_conn_count_map.containsKey(ip)) {
							this.current_ip_conn_count_map.put(ip, new AtomicInteger(0));
						}

						int has_connect = ((AtomicInteger) this.current_ip_conn_count_map.get(ip)).get();
						int ip_max_conn = ((AtomicInteger) this.ip_connect_count_map.get(ip)).get();
						if ((has_connect < ip_max_conn) && (this.queue != null)) {
							SocketChannel socketChannel = SocketChannel.open();
							socketChannel.configureBlocking(false);
							socketChannel.socket().setSoTimeout(60000);
							SelectionKey selectionKey = socketChannel.register(selector, 8);
							CommonBean commonBean = new CommonBean();
							commonBean.setConnected(false);
							commonBean.setIp(ip);
							selectionKey.attach(commonBean);
							((AtomicInteger) this.current_ip_conn_count_map.get(ip)).getAndIncrement();
							socketChannel.connect(new InetSocketAddress(ip, this.gate_port));
						}
					}

					if (System.currentTimeMillis() - this.last_check_login_time > 5000L) {
						this.log.warn("yw_code: " + this.yw_code + " gate_user: " + this.gate_user
								+ ", 检测到5s没有交互,现在重新连接网关服务器[" + this.gate_host + "]");
						this.log.warn("yw_code: " + this.yw_code + " gate_user: " + this.gate_user
								+ ", selectedKeys size:" + selector.selectedKeys().size());
						this.last_check_login_time = System.currentTimeMillis();
						for (String ip : this.current_ip_conn_count_map.keySet()) {
							((AtomicInteger) this.current_ip_conn_count_map.get(ip)).getAndSet(0);
						}
						doReset(selector);
						try {
							if ((this.waitSubmitRespMap.size() == 0) && (this.waitSubmitRespMap_long.size() == 0)
									&& (this.queue.size() == 0)) {
								sleep(100L);
								continue;
							}
							sleep(2L);
						} catch (Exception localException1) {
						}
					}
					if (selector.isOpen()) {
						long time0 = System.currentTimeMillis();
						int interest = selector.select(100L);
						long time1 = System.currentTimeMillis();
						long interval = time1 - time0;
						if (interest > 0) {
							Iterator keys = selector.selectedKeys().iterator();
							while (keys.hasNext()) {
								SmsMessage tmpForm = null;
								try {
									key = (SelectionKey) keys.next();
									SocketChannel socketChannel = (SocketChannel) key.channel();
									CommonBean commonBean = (CommonBean) key.attachment();
									String ip = commonBean.getIp();
									int current_count = ((AtomicInteger) this.current_ip_conn_count_map.get(ip)).get();
									int total_count = ((AtomicInteger) this.ip_connect_count_map.get(ip)).get();
									int other_ip_count = getOtherIpCount(ip);
									try {
										if (key.isConnectable()) {
											this.last_check_login_time = System.currentTimeMillis();
											if (socketChannel.finishConnect()) {
												SelectionKey selectionKey = socketChannel.register(selector, 5);
												CommonBean commonBeanNew = new CommonBean();
												commonBeanNew.setConnected(false);
												commonBeanNew.setIp(ip);
												selectionKey.attach(commonBeanNew);

												if (this.log.isInfoEnabled()) {
													this.log.info("yw_code:" + this.yw_code + " socket to ip:" + ip
															+ " port:" + this.gate_port + " success ip_current_count="
															+ current_count + " ip_total_count=" + total_count
															+ " other_ip_count=" + other_ip_count + " key:"
															+ key.hashCode());
												}

												keys.remove();
											}
										}
									} catch (Exception e) {
										if (this.log.isInfoEnabled()) {
											this.log.info("yw_code:" + this.yw_code + " socket to ip:" + ip + " port:"
													+ this.gate_port + " failure ip_current_count=" + current_count
													+ " ip_total_count=" + total_count + " other_ip_count="
													+ other_ip_count + " key:" + key.hashCode());
										}
										throw new Exception(e);
									}

									if (key.isWritable()) {
										this.last_check_login_time = System.currentTimeMillis();
										if ((commonBean != null) && (!commonBean.getIs_send_connect())) {
											commonBean.setIs_send_connect(true);
											CMPPConnectMessage login = new CMPPConnectMessage(this.gate_user,
													this.gate_pwd, 32);
											socketChannel.write(login.getMessage());
											commonBean.setNotReceiveSendBindRespMessage(true);
											commonBean.setSendBindMessageTimestamp(System.currentTimeMillis());
											commonBean.setSendCheckTimestamp(System.currentTimeMillis());
											if (this.log.isInfoEnabled()) {
												this.log.info("yw_code:" + this.yw_code + " seqNumber:"
														+ login.getCloneMsgHeader().getSequence_id() + " loginName:"
														+ this.gate_user + " loginPwd:xxxxxxxx key:" + key.hashCode());
											}
										}

										if ((commonBean != null) && (commonBean.isConnected())
												&& (commonBean.canSend(
														1000 / (this.max_speed_per_second / this.max_allow_connect)))
												&& (this.waitSubmitRespMap.size() < 100 * this.max_allow_connect)) {
											tmpForm = (SmsMessage) this.queue.poll();
											if (tmpForm != null) {
												String chargeTermId = ChargeTermIdPool
														.getChargeTermId(tmpForm.getSp_number(), this.yw_code);
												String mobile = tmpForm.getMobile();
												String user_id = tmpForm.getUser_id();
												if ((DataCenter.check_user_with86(user_id))
														&& (tmpForm.getMobile().startsWith("86"))) {
													mobile = tmpForm.getMobile().substring(2,
															tmpForm.getMobile().length());
												}

												tmpForm.setMobile(mobile);

												if (tmpForm.getPktotal() > 1) {
													CMPPSubmitMessage submit = new CMPPSubmitMessage(
															tmpForm.getSp_number(), mobile, tmpForm.getMsg_content(),
															this.gate_service_id, this.gate_corp_code,
															tmpForm.getPktotal(), tmpForm.getPknumber(),
															tmpForm.getSub_msg_id(), tmpForm.getMsg_format(),
															chargeTermId);
													CmppMessageUtil u = new CmppMessageUtil();
													tmpForm.setMsg_send_time(DateUtil.currentTimeToMs());
													u.setCmppMessage(submit);
													u.setSmsMessage(tmpForm);
													u.setTry_times(u.getTry_times() + 1);
													u.setSubmit_time(System.currentTimeMillis());
													this.waitSubmitRespMap.put(Integer
															.valueOf(submit.getCloneMsgHeader().getSequence_id()), u);
													socketChannel.write(submit.getMessage());
													this.log.info("yw_code: " + this.yw_code
															+ ", get message from queueSize: " + this.queue.size()
															+ ", sp_number: " + tmpForm.getSp_number()
															+ ", send message wait resp seqNumber: "
															+ submit.getCloneMsgHeader().getSequence_id()
															+ ", pk_total: " + tmpForm.getPktotal() + ", pk_number: "
															+ tmpForm.getPknumber() + ", mobile: "
															+ UtilTool.filterMobile(tmpForm.getMobile())
															+ ", waitSubmitRespMap: " + this.waitSubmitRespMap.size()
															+ " key:" + key.hashCode());
												} else {
													CMPPSubmitMessage submit = new CMPPSubmitMessage(
															tmpForm.getSp_number(), mobile, tmpForm.getMsg_content(),
															this.gate_service_id, this.gate_corp_code,
															tmpForm.getMsg_format(), chargeTermId);
													CmppMessageUtil u = new CmppMessageUtil();
													tmpForm.setMsg_send_time(DateUtil.currentTimeToMs());
													u.setCmppMessage(submit);
													u.setSmsMessage(tmpForm);
													u.setTry_times(u.getTry_times() + 1);
													u.setSubmit_time(System.currentTimeMillis());
													this.waitSubmitRespMap.put(Integer
															.valueOf(submit.getCloneMsgHeader().getSequence_id()), u);
													socketChannel.write(submit.getMessage());
													this.log.info("yw_code: " + this.yw_code
															+ ", get message from queueSize: " + this.queue.size()
															+ ", sp_number: " + tmpForm.getSp_number()
															+ ", send message wait resp seqNumber: "
															+ submit.getCloneMsgHeader().getSequence_id()
															+ ", pk_total: " + tmpForm.getPktotal() + ", pk_number: "
															+ tmpForm.getPknumber() + ", mobile: "
															+ UtilTool.filterMobile(tmpForm.getMobile())
															+ ", waitSubmitRespMap: " + this.waitSubmitRespMap.size()
															+ " key:" + key.hashCode());
												}
											} else if ((commonBean != null) && (System.currentTimeMillis()
													- commonBean.getSendCheckTimestamp() > 30000L)) {
												CMPPActiveMessage activeMessage = new CMPPActiveMessage();
												socketChannel.write(activeMessage.getMessage());
												commonBean.setSendCheckTimestamp(System.currentTimeMillis());
												this.log.info(this.yw_code + " send a active message seqNumber:"
														+ activeMessage.getSequence_id() + " key:" + key.hashCode());
											}

										}

									}

									for (int times = 0; times < 4; times++) {
										if ((key.isReadable()) && (commonBean != null)) {
											this.last_check_login_time = System.currentTimeMillis();
											commonBean.read(socketChannel);
											commonBean.setSendCheckTimestamp(System.currentTimeMillis());
											if (commonBean.isReady()) {
												CMPPMessage message = CMPPMessage.create(commonBean.getCmppHeader(),
														commonBean.getBodyBuffer().array());
												if (message.getCommand_id() == -2147483647) {
													CMPPConnectRespMessage tmp = (CMPPConnectRespMessage) message;
													if (this.log.isInfoEnabled()) {
														this.log.info("yw_code:" + this.yw_code + " seqNumber:"
																+ tmp.getCloneMsgHeader().getSequence_id()
																+ " login result:" + tmp.getConnect_status()
																+ " from ip:" + ip + " port:" + this.gate_port + " key:"
																+ key.hashCode());
													}
													if (tmp.getConnect_status() == 0) {
														commonBean.setConnected(true);
														commonBean.setNotReceiveSendBindRespMessage(false);
														commonBean.setSendBindMessageTimestamp(
																System.currentTimeMillis());
													} else {
														throw new Exception("yw_code:" + this.yw_code
																+ " login fail status: " + tmp.getConnect_status());
													}
												}

												if (message.getCommand_id() == -2147483644) {
													CMPPSubmitRespMessage submitResp = (CMPPSubmitRespMessage) message;
													CmppMessageUtil u = (CmppMessageUtil) this.waitSubmitRespMap
															.remove(Integer.valueOf(
																	submitResp.getCloneMsgHeader().getSequence_id()));
													if (u == null) {
														u = (CmppMessageUtil) this.waitSubmitRespMap_long
																.remove(Integer.valueOf(submitResp.getCloneMsgHeader()
																		.getSequence_id()));
													}
													if (u != null) {
														SmsMessage tmpMessage = u.getSmsMessage();
														if (submitResp.getResult() == 0) {
															tmpMessage.setStatus(1);
															tmpMessage.setTd_code(this.yw_code);
															tmpMessage.setResponse(1000);

															tmpMessage.setTmp_msg_id(submitResp.getSeq_no1());
														} else {
															tmpMessage.setStatus(1);
															tmpMessage.setTd_code(this.yw_code);
															tmpMessage.setResponse(submitResp.getResult());

															tmpMessage.setTmp_msg_id("");
														}
														if (this.log.isInfoEnabled()) {
															this.log.info("yw_code: " + this.yw_code + ", user_id: "
																	+ tmpMessage.getUser_id()
																	+ ", receive and find submitResp for seqNumber: "
																	+ submitResp.getSequence_id() + ", status: "
																	+ submitResp.getResult() + ", mobile: "
																	+ UtilTool.filterMobile(tmpMessage.getMobile())
																	+ " key:" + key.hashCode());
														}

														DataCenter.addSubmitRespMessage(tmpMessage);
													} else if (this.log.isInfoEnabled()) {
														this.log.info("yw_code: " + this.yw_code + ", user_id: "
																+ this.gate_user
																+ ", receive but not find submitResp for seqNumber: "
																+ submitResp.getSequence_id() + ", status: "
																+ submitResp.getResult() + " key:" + key.hashCode());
													}

												}

												if (message.getCommand_id() == 5) {
													CMPPDeliverMessage deliver = (CMPPDeliverMessage) message;
													CMPPDeliverRespMessage deliverResp = new CMPPDeliverRespMessage(
															deliver);
													socketChannel.write(deliverResp.getMessage());

													if (deliver.isReport() == 1) {
														int report_state = 2;
														String fail_describe = "";
														if (deliver.getReport_state() == 0) {
															report_state = 0;
														}
														fail_describe = deliver.getReport_desc();
														String mobile = deliver.getSrcTermid();
														if (mobile.startsWith("86")) {
															mobile = mobile.substring(2);
														}
														ReportBean report = new ReportBean();

														report.setSubmitTime(System.currentTimeMillis());
														report.setMsg_id(deliver.getReport_no1());
														report.setFail_desc(fail_describe);
														report.setMobile(mobile);
														report.setSp_number(deliver.getDestTermid());
														report.setStat(report_state);
														report.setErr(String.valueOf(report_state));
														report.setTd_code(this.yw_code);
														report.setRpt_return_time(DateUtil.currentTimeToMs());
														report.setReveive_time(DateUtil.getcurrentTime());

														this.log.info("addReportMessage-》  yw_code:" + this.yw_code
																+ " receive report msg_id:" + deliver.getReport_no1()
																+ " err:" + report_state + " desc:" + fail_describe
																+ " mobile:" + UtilTool.filterMobile(mobile)
																+ " Sequence_id:" + deliverResp.getSequence_id()
																+ " key:" + key.hashCode());

														if (!DataCenter.addReportMessage(report)) {
															DataCenter.putReportCacheQueue(report);
														}
													} else {
														String mobile = deliver.getSrcTermid();
														if (mobile.startsWith("86")) {
															mobile = mobile.substring(2);
														}
														DeliverBean deliverForm = new DeliverBean();
														deliverForm.setReveive_time(System.currentTimeMillis());
														deliverForm.setSp_number(deliver.getDestTermid());
														deliverForm.setMobile(mobile);
														deliverForm.setMsg_content(deliver.getMsgContent());
														deliverForm.setSub_msg_id(deliver.getLong_msg_id());
														deliverForm.setPk_total(deliver.getLong_msg_count());
														deliverForm.setPk_number(deliver.getLong_msg_sub_sn());
														deliverForm.setMsg_format(deliver.getMsgFormat());

														this.log.info("addDeliverMessage-》 yw_code:" + this.yw_code
																+ " deliver from " + UtilTool.filterMobile(mobile)
																+ " to " + deliver.getDestTermid() + " Content:"
																+ deliver.getMsgContent() + " Sequence_id:"
																+ deliverResp.getSequence_id() + " key:"
																+ key.hashCode());
														DataCenter.addDeliverMessage(deliverForm);
													}

												}

												if (message.getCommand_id() == -2147483640) {
													CMPPActiveRespMessage activeResp = (CMPPActiveRespMessage) message;
													this.log.info("yw_code:" + this.yw_code
															+ " receive a active_resp seqNumber:"
															+ activeResp.getSequence_id() + " key:" + key.hashCode());
												}

												if (message.getCommand_id() == 8) {
													CMPPActiveMessage testMessage = (CMPPActiveMessage) message;

													this.log.info("yw_code:" + this.yw_code
															+ " receive a active seqNumber:"
															+ testMessage.getSequence_id() + " key:" + key.hashCode());
													CMPPActiveRespMessage testRespMessage = new CMPPActiveRespMessage(
															testMessage);
													socketChannel.write(testRespMessage.getMessage());
													this.log.info(
															this.yw_code + " send a active_resp message seqNumber:"
																	+ testRespMessage.getSequence_id() + " key:"
																	+ key.hashCode());
												}

												commonBean.reset();
											}

										}

									}

									if ((commonBean != null) && (System.currentTimeMillis()
											- commonBean.getSendCheckTimestamp() > 60000L)) {
										this.log.info("yw_code: " + this.yw_code + "  not  message over 60s ");
										throw new Exception("yw_code: " + this.yw_code + " not  message over 60s");
									}
									if ((commonBean != null) && (commonBean.isNotReceiveSendBindRespMessage())
											&& (System.currentTimeMillis()
													- commonBean.getSendBindMessageTimestamp() > 60000L)) {
										this.log.info("yw_code: " + this.yw_code
												+ " send bind message over 60s not receive resp");
										throw new Exception("yw_code: " + this.yw_code
												+ " send bind message over 60s not receive resp");
									}
								} catch (Exception e) {
									try {
										CommonBean commonBean = (CommonBean) key.attachment();
										String ip = commonBean.getIp();
										int current_conn = 0;
										if (StringUtils.isNotBlank(ip)) {
											int other_ip_count = getOtherIpCount(ip);
											if (this.current_ip_conn_count_map.containsKey(ip)) {
												current_conn = ((AtomicInteger) this.current_ip_conn_count_map.get(ip))
														.get();
												if (current_conn > 0) {
													current_conn = ((AtomicInteger) this.current_ip_conn_count_map
															.get(ip)).decrementAndGet();
													int total_count = ((AtomicInteger) this.ip_connect_count_map
															.get(ip)).get();
													if (this.log.isErrorEnabled()) {
														this.log.error("yw_code:" + this.yw_code + " ip=" + ip
																+ " connect count has reduce ,ip_current_count is:"
																+ current_conn + " ip_total_count=" + total_count
																+ " other_ip_count=" + other_ip_count
																+ " now_queue_size=[" + this.queue.size() + "] key:"
																+ key.hashCode(), e);
													}
												}
											}
										}

									} catch (Exception e1) {
										this.log.error("yw_code:" + this.yw_code + " key:" + key.hashCode(), e1);
									}

									try {
										key.channel().close();
										key.cancel();
									} catch (Exception ex) {
										this.log.error("yw_code:" + this.yw_code + " key:" + key.hashCode(), ex);
									}
									try {
										sleep(100L);
									} catch (Exception e1) {
										this.log.error("yw_code:" + this.yw_code + " key:" + key.hashCode(), e1);
									}
								} finally {
									keys.remove();
								}
								keys.remove();
							}
						} else {
							this.log.warn(
									"yw_code: " + this.yw_code + ", 检测到有效事件数量为0,并且select时间间隔[" + interval + "] ms");
							try {
								sleep(1000L);
							} catch (Exception localException2) {
							}
						}
					} else {
						try {
							sleep(1000L);
						} catch (Exception localException3) {
						}
					}
					try {
						if (System.currentTimeMillis() - this.last_check_time > 20000L) {
							this.last_check_time = System.currentTimeMillis();

							for (int map_key : this.waitSubmitRespMap.keySet()) {
								// for (??? = this.waitSubmitRespMap.keySet().iterator(); ???.hasNext(); ) {
								// Integer map_key = (Integer).next();
								CmppMessageUtil u = (CmppMessageUtil) this.waitSubmitRespMap.get(map_key);
								if (System.currentTimeMillis() - u.getSubmit_time() > 20000L) {
									this.waitSubmitRespMap_long.put(map_key, u);
									this.log.info("waitSubmitRespMap_long_size[" + this.waitSubmitRespMap_long.size()
											+ "]" + ", yw_code:" + this.yw_code + ", mobile: "
											+ UtilTool.filterMobile(u.getSmsMessage().getMobile()));
									this.waitSubmitRespMap.remove(map_key);
								}
							}
						}
					} catch (Exception e) {
						this.log.error("yw_code:" + this.yw_code, e);
					}

					try {
						if (System.currentTimeMillis() - this.last_check_time_long <= 180000L)
							break ;
						this.last_check_time_long = System.currentTimeMillis();
						Iterator its = this.waitSubmitRespMap_long.values().iterator();
						while (its.hasNext()) {
							CmppMessageUtil u = (CmppMessageUtil) its.next();
							if (System.currentTimeMillis() - u.getSubmit_time() > 300000L) {
								SmsMessage tmpMessage = u.getSmsMessage();
								tmpMessage.setTd_code(this.yw_code);
								if (tmpMessage.getTry_times() >= 3) {
									tmpMessage.setStatus(1);
									tmpMessage.setResponse(2);
									tmpMessage.setFail_desc("send time out 5 min");

									DataCenter.addSubmitRespMessage(tmpMessage);
								} else {
									tmpMessage.setTry_times(tmpMessage.getTry_times() + 1);
									DataCenter.putReSendQueue(this.yw_code, tmpMessage);
									this.log.info("yw_code: " + this.yw_code + ", user_id: " + tmpMessage.getUser_id()
											+ ", msgid: " + tmpMessage.getMsg_id() + ", send: "
											+ tmpMessage.getSp_number() + ", mobile: "
											+ UtilTool.filterMobile(tmpMessage.getMobile()) + ", Pktotal: "
											+ tmpMessage.getPktotal() + ", Pknumber: " + tmpMessage.getPknumber()
											+ ", try times: " + tmpMessage.getTry_times() + ", over_time: ["
											+ (System.currentTimeMillis() - u.getSubmit_time()) / 1000L
											+ "] s, waitSize[" + this.waitSubmitRespMap_long.size() + "]");
								}
								its.remove();
							}
						}
					} catch (Exception e) {
						this.log.error("yw_code:" + this.yw_code, e);
					}
				} else {
					this.log.warn("yw_code:" + this.yw_code + " gate_ip=[" + this.gate_host + "] max_allow_connect=["
							+ this.max_allow_connect + "] gate_ip is not filled in or max_allow_connect<=0...");
					Thread.sleep(3000L);
				}
			} catch (Exception e) {
				this.log.error("yw_code:" + this.yw_code, e);
				try {
					if ((this.waitSubmitRespMap.size() == 0) && (this.waitSubmitRespMap_long.size() == 0)
							&& (this.queue.size() == 0))
						sleep(100L);
					else
						sleep(2L);
				} catch (Exception localException4) {
				}
			} finally {
				try {
					if ((this.waitSubmitRespMap.size() == 0) && (this.waitSubmitRespMap_long.size() == 0)
							&& (this.queue.size() == 0))
						sleep(100L);
					else {
						sleep(2L);
					}
				} catch (Exception localException5) {
				}
			}
		}
		try {
			releaseRecourse(selector);
		} catch (Exception e1) {
			this.log.error("yw_code:" + this.yw_code, e1);
		}
		ArrayBlockingQueue queue;
		try {
			Iterator its = this.waitSubmitRespMap.values().iterator();
			while (its.hasNext()) {
				CmppMessageUtil u = (CmppMessageUtil) its.next();
				SmsMessage tmpMessage = u.getSmsMessage();
				tmpMessage.setTd_code(this.yw_code);
				tmpMessage.setStatus(1);
				tmpMessage.setResponse(1000);
				tmpMessage.setFail_desc("send time out");
				DataCenter.addSubmitRespMessage(tmpMessage);
				its.remove();
			}

			Iterator its_long = this.waitSubmitRespMap_long.values().iterator();
			while (its_long.hasNext()) {
				CmppMessageUtil u = (CmppMessageUtil) its_long.next();
				SmsMessage tmpMessage = u.getSmsMessage();
				tmpMessage.setTd_code(this.yw_code);
				tmpMessage.setStatus(1);
				tmpMessage.setResponse(1000);
				tmpMessage.setFail_desc("send time long out");
				DataCenter.addSubmitRespMessage(tmpMessage);
				its_long.remove();
			}

		} catch (Exception e) {
			this.log.error("yw_code:" + this.yw_code, e);
		} finally {
			if (this.scanMap != null) {
				queue = (ArrayBlockingQueue) this.scanMap.remove(this.yw_code);
				if (queue != null) {
					this.monitor_log.info("发送线程[" + this.yw_code + "]is stoped,线程中积压的数据条数[" + queue.size() + "]");
					if (queue.size() > 0) {
						DataCenter.putScanMap_closed(this.yw_code, queue);
					}
				}
			}
		}

		this.log.info("yw_code:" + this.yw_code + "    SendThread  is closed ");
		this.monitor_log.info("yw_code:" + this.yw_code + "    SendThread  is closed ");
	}

	public int getOtherIpCount(String ip) {
		int other_ip_count = 0;
		for (String each : this.current_ip_conn_count_map.keySet()) {
			if (!each.equalsIgnoreCase(ip)) {
				other_ip_count += ((AtomicInteger) this.current_ip_conn_count_map.get(each)).get();
			}
		}
		return other_ip_count;
	}

	private void releaseRecourse(Selector selector) {
		try {
			Iterator keys = selector.keys().iterator();
			while (keys.hasNext()) {
				SelectionKey key = (SelectionKey) keys.next();
				try {
					((SocketChannel) key.channel()).socket().close();
				} catch (Exception localException1) {
				}
				try {
					key.channel().close();
				} catch (Exception localException2) {
				}
				try {
					key.cancel();
				} catch (Exception localException3) {
				}
			}
			selector.close();
		} catch (Exception e) {
			this.log.info("releaseRecourse", e);
		}
	}

	public int getMax_queue_size() {
		return this.max_queue_size;
	}

	public void setMax_queue_size(int max_queue_size) {
		this.max_queue_size = max_queue_size;
	}

	public void doShutDown() {
		this.running = false;
		interrupt();
	}

	public void doStart() {
		start();
	}

	public boolean checkIsStarted() {
		return this.running;
	}

	public void doReset(Selector selector) {
		try {
			Iterator keys = selector.keys().iterator();
			while (keys.hasNext())
				try {
					SelectionKey key = (SelectionKey) keys.next();
					if (key == null)
						continue;
					try {
						this.log.error("yw_code: " + this.yw_code + " key:" + key.hashCode() + " key is_valid["
								+ key.isValid() + "] key isAcceptable :" + key.isAcceptable() + " key isConnectable :"
								+ key.isConnectable() + " key isReadable :" + key.isReadable() + " key isWritable :"
								+ key.isWritable() + " do close .");
						((SocketChannel) key.channel()).socket().close();
						key.channel().close();
						key.cancel();
					} catch (Exception e2) {
						this.log.error("重置链接-> yw_code:" + this.yw_code, e2);
					}

				} catch (Exception e) {
					this.log.error("重置链接-> yw_code:" + this.yw_code, e);
				}
		} catch (Exception e) {
			this.log.error("重置链接-> yw_code:" + this.yw_code, e);
		}
		this.log.warn("重置所有链接-> yw_code:" + this.yw_code + " 完成 ！！！");
	}
}