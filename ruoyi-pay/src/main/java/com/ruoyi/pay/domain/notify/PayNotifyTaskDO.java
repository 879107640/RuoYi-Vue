package com.ruoyi.pay.domain.notify;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.entity.BaseEntity;
import com.ruoyi.common.enums.notify.PayNotifyStatusEnum;
import com.ruoyi.common.enums.notify.PayNotifyTypeEnum;
import com.ruoyi.pay.domain.app.PayAppDO;
import com.ruoyi.pay.domain.order.PayOrderDO;
import com.ruoyi.pay.domain.refund.PayRefundDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 支付通知
 * 在支付系统收到支付渠道的支付、退款的结果后，需要不断的通知到业务系统，直到成功。
 *
 * @author centre
 */
@TableName("pay_notify_task")
@KeySequence("pay_notify_task_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class PayNotifyTaskDO extends BaseEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 通知频率，单位为秒。
   * <p>
   * 算上首次的通知，实际是一共 1 + 8 = 9 次。
   */
  public static final Integer[] NOTIFY_FREQUENCY = new Integer[]{
      15, 15, 30, 180,
      1800, 1800, 1800, 3600
  };

  /**
   * 编号，自增
   */
  @TableId
  private Long id;
  /**
   * 应用编号
   * <p>
   * 关联 {@link PayAppDO#getId()}
   */
  private Long appId;
  /**
   * 通知类型
   * <p>
   * 外键 {@link PayNotifyTypeEnum}
   */
  private Integer type;
  /**
   * 数据编号，根据不同 type 进行关联：
   * <p>
   * 1. {@link PayNotifyTypeEnum#ORDER} 时，关联 {@link PayOrderDO#getId()}
   * 2. {@link PayNotifyTypeEnum#REFUND} 时，关联 {@link PayRefundDO#getId()}
   */
  private Long dataId;
  /**
   * 商户订单编号
   */
  private String merchantOrderId;
  /**
   * 商户转账单编号
   */
  private String merchantTransferId;
  /**
   * 通知状态
   * <p>
   * 外键 {@link PayNotifyStatusEnum}
   */
  private Integer status;
  /**
   * 下一次通知时间
   */
  private LocalDateTime nextNotifyTime;
  /**
   * 最后一次执行时间
   */
  private LocalDateTime lastExecuteTime;
  /**
   * 当前通知次数
   */
  private Integer notifyTimes;
  /**
   * 最大可通知次数
   */
  private Integer maxNotifyTimes;
  /**
   * 通知地址
   */
  private String notifyUrl;

}
