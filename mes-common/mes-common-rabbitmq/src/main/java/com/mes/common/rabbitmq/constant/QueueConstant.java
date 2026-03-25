package com.mes.common.rabbitmq.constant;

/**
 * 队列常量
 */
public class QueueConstant {

    /** 生产数据采集队列 */
    public static final String PRODUCTION_DATA_QUEUE = "mes.production.data";

    /** 设备状态队列 */
    public static final String EQUIPMENT_STATUS_QUEUE = "mes.equipment.status";

    /** 质量检测队列 */
    public static final String QUALITY_CHECK_QUEUE = "mes.quality.check";

    /** 告警队列 */
    public static final String ALERT_QUEUE = "mes.alert";

    /** 日志队列 */
    public static final String LOG_QUEUE = "mes.log";

    /** 交换机 */
    public static final String MES_EXCHANGE = "mes.exchange";

    /** 路由键前缀 */
    public static final String ROUTING_KEY_PREFIX = "mes.";
}
