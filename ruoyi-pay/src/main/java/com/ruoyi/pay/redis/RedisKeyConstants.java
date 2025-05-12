package com.ruoyi.pay.redis;

/**
 * System Redis Key 枚举类
 *
 * @author centre
 */
public interface RedisKeyConstants {
  /**
   * 指定收费项目所有子收费项目编号数组的缓存
   * <p>
   * KEY 格式：dept_children_ids:{id}
   * VALUE 数据类型：String 子收费编号集合
   */
  String CHARGE_CHILDREN_ID_LIST = "charge_children_ids";

  String PAY_ACCOUNT_LOCK = "pay_account:lock:%d";
  /**
   * 通知任务的分布式锁
   *
   * KEY 格式：pay_notify:lock:%d // 参数来自 DefaultLockKeyBuilder 类
   * VALUE 数据格式：HASH // RLock.class：Redisson 的 Lock 锁，使用 Hash 数据结构
   * 过期时间：不固定
   */
  String PAY_NOTIFY_LOCK = "pay_notify:lock:%d";

  /**
   * 支付钱包的分布式锁
   *
   * KEY 格式：pay_wallet:lock:%d
   * VALUE 数据格式：HASH // RLock.class：Redisson 的 Lock 锁，使用 Hash 数据结构
   * 过期时间：不固定
   */
  String PAY_WALLET_LOCK = "pay_wallet:lock:%d";

  /**
   * 支付序号的缓存
   *
   * KEY 格式：pay_no:{prefix}
   * VALUE 数据格式：编号自增
   */
  String PAY_NO = "pay_no:";

}
